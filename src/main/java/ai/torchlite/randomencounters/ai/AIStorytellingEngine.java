package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.context.ContextEnrichmentEngine;
import ai.torchlite.randomencounters.story.PlayerStoryState;
import ai.torchlite.randomencounters.story.StoryStateManager;
import ai.torchlite.randomencounters.story.StoryThread;
import ai.torchlite.randomencounters.story.StoryThreadManager;
import ai.torchlite.randomencounters.story.StorytellingResponse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main orchestrator for AI-driven storytelling encounters
 */
public class AIStorytellingEngine {

    private final ContextEnrichmentEngine contextEngine;
    private final NarrativePromptBuilder promptBuilder;
    private final StoryThreadManager threadManager;
    private final List<IAIStorytellingService> services;

    public AIStorytellingEngine() {
        this.contextEngine = new ContextEnrichmentEngine();
        this.promptBuilder = new NarrativePromptBuilder();
        this.threadManager = new StoryThreadManager();
        this.services = new ArrayList<>();

        // Register AI services
        services.add(new OpenAIStorytellingService());
        services.add(new AnthropicStorytellingService());

        // Sort by priority
        services.sort(Comparator.comparingInt(IAIStorytellingService::getPriority));
    }

    /**
     * Generate a story-driven encounter for a player
     *
     * @param player The player to generate an encounter for
     * @param world The world the player is in
     * @return StorytellingResponse containing the encounter and story updates, or null if generation fails
     */
    public StorytellingResponse generateEncounter(EntityPlayer player, World world) {
        try {
            // Get player story state
            StoryStateManager stateManager = StoryStateManager.getInstance();
            if (stateManager == null) {
                RandomEncounters.LOGGER.error("StoryStateManager not initialized");
                return null;
            }

            PlayerStoryState state = stateManager.getOrCreateState(player);

            // Build enriched context
            StorytellingRequest request = contextEngine.buildStorytellingRequest(player, world);

            // Select story thread to focus on (if any)
            StoryThread focusThread = threadManager.selectThreadForEncounter(state);
            if (focusThread != null) {
                request.setFocusThread(focusThread);
                RandomEncounters.LOGGER.info("Focusing encounter on thread: " + focusThread.getTitle());
            }

            // Build prompt
            String prompt;
            if (focusThread != null) {
                prompt = promptBuilder.buildPrompt(request);
            } else {
                prompt = promptBuilder.buildEmergentPrompt(request);
                RandomEncounters.LOGGER.info("Generating emergent encounter (no thread selected)");
            }

            // Try each AI service in priority order
            StorytellingResponse response = null;
            Exception lastException = null;

            for (IAIStorytellingService service : services) {
                if (!service.isAvailable()) {
                    RandomEncounters.LOGGER.debug("Skipping unavailable service: " + service.getServiceName());
                    continue;
                }

                try {
                    RandomEncounters.LOGGER.info("Attempting generation with: " + service.getServiceName());
                    response = service.generateEncounter(prompt);

                    if (response != null) {
                        RandomEncounters.LOGGER.info("Successfully generated encounter with: " + service.getServiceName());
                        break;
                    }
                } catch (Exception e) {
                    RandomEncounters.LOGGER.error("Failed to generate with " + service.getServiceName() + ": " + e.getMessage());
                    lastException = e;
                }
            }

            if (response == null) {
                if (lastException != null) {
                    RandomEncounters.LOGGER.error("All AI services failed. Last error: " + lastException.getMessage());
                } else {
                    RandomEncounters.LOGGER.error("No AI services available");
                }
                return null;
            }

            // Process story updates
            processStoryUpdates(state, response);

            // Save updated state
            stateManager.saveState(state);

            return response;

        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Error generating encounter: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Process and apply story updates from AI response
     */
    private void processStoryUpdates(PlayerStoryState state, StorytellingResponse response) {
        if (response.getStoryUpdates() == null) {
            return;
        }

        StorytellingResponse.StoryUpdates updates = response.getStoryUpdates();

        // Apply thread updates
        if (updates.getThreadUpdates() != null) {
            for (ai.torchlite.randomencounters.story.StoryThreadUpdate update : updates.getThreadUpdates()) {
                try {
                    threadManager.updateThread(state, update);
                    RandomEncounters.LOGGER.info("Updated thread: " + update.getThreadId());
                } catch (Exception e) {
                    RandomEncounters.LOGGER.error("Failed to update thread " + update.getThreadId() + ": " + e.getMessage());
                }
            }
        }

        // Add new threads
        if (updates.getNewThreads() != null) {
            for (StoryThread newThread : updates.getNewThreads()) {
                try {
                    state.addThread(newThread);
                    RandomEncounters.LOGGER.info("Created new thread: " + newThread.getTitle());
                } catch (Exception e) {
                    RandomEncounters.LOGGER.error("Failed to create thread: " + e.getMessage());
                }
            }
        }

        // Add encounter to history
        if (updates.getEncounterSummary() != null) {
            // Create encounter summary from the description
            // This will be enhanced when we add outcome tracking
            ai.torchlite.randomencounters.story.EncounterSummary summary =
                new ai.torchlite.randomencounters.story.EncounterSummary(
                    java.util.UUID.randomUUID().toString(),
                    "ai_generated",
                    updates.getEncounterSummary(),
                    "pending" // Will be updated by outcome tracker
                );

            if (updates.getKeyChoices() != null) {
                summary.getKeyChoices().addAll(updates.getKeyChoices());
            }

            state.addEncounterSummary(summary);
        }
    }

    /**
     * Get list of available AI services
     */
    public List<String> getAvailableServices() {
        List<String> available = new ArrayList<>();
        for (IAIStorytellingService service : services) {
            if (service.isAvailable()) {
                available.add(service.getServiceName());
            }
        }
        return available;
    }

    /**
     * Check if any AI service is available
     */
    public boolean hasAvailableService() {
        for (IAIStorytellingService service : services) {
            if (service.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
