package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.ai.prompt.ModularPromptBuilder;
import ai.torchlite.randomencounters.story.EncounterSummary;
import ai.torchlite.randomencounters.story.StoryThread;

import java.util.List;
import java.util.Map;

/**
 * Builds narrative prompts for AI storytelling with Dregora lore context
 *
 * NOTE: This class contains the legacy monolithic prompt builder.
 * For a more maintainable approach, use ModularPromptBuilder instead.
 */
public class NarrativePromptBuilder {

    private final ModularPromptBuilder modularBuilder;
    private boolean useModularBuilder = true; // Default to new modular system

    private static final String DREGORA_LORE_CONTEXT =
        "# World Context: RLCraft Dregora\n\n" +
        "Dregora is a post-apocalyptic Earth, hundreds of years after \"The Blight\" - " +
        "a catastrophic event that reduced cities to ruins and transformed the world. " +
        "The players are descendants of colonists from the Dregorian Voyager, a generation ship " +
        "that returned after centuries in space to find Earth unrecognizable.\n\n" +
        "## World State:\n" +
        "- Nuclear-devastated wasteland with pockets of civilization\n" +
        "- Ancient ruins from pre-Blight (early 21st century) and older civilizations\n" +
        "- Mutant creatures and eldritch horrors roam the land\n" +
        "- \"The green of corruption\" - Blight-touched zones\n" +
        "- Harsh survival conditions - RLCraft difficulty\n\n" +
        "## Tone:\n" +
        "- Grimdark survival with glimmers of hope\n" +
        "- Post-apocalyptic but with mystery and wonder\n" +
        "- Consequences matter - choices echo through the wasteland\n" +
        "- Dark fantasy meets sci-fi remnants\n\n";

    private static final String FACTION_CONTEXT =
        "## Major Factions:\n" +
        "1. **Dregorian Colonists** - Ship returnees, technologically savvy, organized\n" +
        "2. **Wasteland Nomads** - Survivors adapted to harsh conditions, resourceful\n" +
        "3. **Scavenger Clans** - Opportunistic raiders, salvage pre-Blight tech\n" +
        "4. **Blight Cultists** - Worship the corruption, seek to spread it\n" +
        "5. **Pre-War Military Remnants** - Disciplined survivors of old armies\n" +
        "6. **Trading Guilds** - Merchants connecting isolated settlements\n" +
        "7. **Techno-Scavengers** - Obsessed with pre-Blight technology\n" +
        "8. **Mutant Tribes** - Blight-changed humans, some retain intelligence\n" +
        "9. **Independent Survivors** - Loners, hermits, those who trust no one\n\n";

    public NarrativePromptBuilder() {
        this.modularBuilder = new ModularPromptBuilder();
    }

    /**
     * Set whether to use the modular builder (default: true)
     */
    public void setUseModularBuilder(boolean useModular) {
        this.useModularBuilder = useModular;
    }

    /**
     * Get access to the modular builder for customization
     */
    public ModularPromptBuilder getModularBuilder() {
        return modularBuilder;
    }

    /**
     * Build a complete AI prompt for encounter generation
     */
    public String buildPrompt(StorytellingRequest request) {
        if (useModularBuilder) {
            return modularBuilder.buildStoryPrompt(request);
        }

        // Legacy monolithic builder
        StringBuilder prompt = new StringBuilder();

        // System context
        prompt.append(DREGORA_LORE_CONTEXT);
        prompt.append(FACTION_CONTEXT);

        // Player story context
        if (request.getNarrativeSummary() != null) {
            prompt.append("## Player Journey:\n");
            prompt.append(request.getNarrativeSummary()).append("\n\n");
        }

        // Recent encounters
        if (request.getRecentEncounters() != null && !request.getRecentEncounters().isEmpty()) {
            prompt.append("## Recent Encounters:\n");
            int count = Math.min(5, request.getRecentEncounters().size());
            List<EncounterSummary> recent = request.getRecentEncounters();
            for (int i = recent.size() - count; i < recent.size(); i++) {
                EncounterSummary encounter = recent.get(i);
                prompt.append(String.format("- [%s] %s (Outcome: %s)\n",
                    encounter.getEncounterType(),
                    encounter.getBriefDescription(),
                    encounter.getOutcome()
                ));
            }
            prompt.append("\n");
        }

        // Active story threads
        if (request.getActiveThreads() != null && !request.getActiveThreads().isEmpty()) {
            prompt.append("## Active Story Threads:\n");
            for (StoryThread thread : request.getActiveThreads()) {
                prompt.append(String.format("- **%s** (Progress: %d/10, Priority: %s)\n",
                    thread.getTitle(),
                    thread.getProgressLevel(),
                    thread.getPriority()
                ));
                prompt.append(String.format("  %s\n", thread.getDescription()));
                if (thread.getNarrativeContext() != null) {
                    prompt.append(String.format("  Context: %s\n", thread.getNarrativeContext()));
                }
            }
            prompt.append("\n");
        }

        // Faction reputation
        if (request.getFactionReputation() != null && !request.getFactionReputation().isEmpty()) {
            prompt.append("## Faction Standing:\n");
            for (Map.Entry<String, Integer> entry : request.getFactionReputation().entrySet()) {
                String standing = getReputationDescription(entry.getValue());
                prompt.append(String.format("- %s: %s (%d)\n",
                    entry.getKey(),
                    standing,
                    entry.getValue()
                ));
            }
            prompt.append("\n");
        }

        // Current situation
        prompt.append("## Current Situation:\n");
        prompt.append(buildCurrentSituation(request));
        prompt.append("\n");

        // Focus thread if specified
        if (request.getFocusThread() != null) {
            prompt.append("## Priority Focus:\n");
            prompt.append(String.format("Advance the story thread: **%s**\n",
                request.getFocusThread().getTitle()
            ));
            prompt.append(String.format("Current state: %s\n\n",
                request.getFocusThread().getNarrativeContext()
            ));
        }

        // Generation guidance
        prompt.append(buildGenerationGuidance(request));

        // Task instructions
        prompt.append(buildTaskInstructions(request));

        return prompt.toString();
    }

    /**
     * Build current situation description
     */
    private String buildCurrentSituation(StorytellingRequest request) {
        StringBuilder situation = new StringBuilder();

        situation.append(String.format("**Player:** %s (Level %d, Health: %.1f/%.1f)\n",
            request.getPlayerName(),
            request.getPlayerLevel(),
            request.getPlayerHealth(),
            request.getPlayerMaxHealth()
        ));

        situation.append(String.format("**Location:** %s biome",
            request.getBiome()
        ));

        if (request.getCurrentLocation() != null) {
            situation.append(String.format(" near %s", request.getCurrentLocation()));
        }

        situation.append(String.format(" (%d, %d, %d)\n",
            request.getPosX(),
            request.getPosY(),
            request.getPosZ()
        ));

        situation.append(String.format("**Time:** %s, Weather: %s\n",
            request.getTimeOfDay(),
            request.getWeather()
        ));

        // Equipment
        if (request.getEquipment() != null && !request.getEquipment().isEmpty()) {
            situation.append("**Equipment:** ");
            boolean first = true;
            for (Map.Entry<String, String> entry : request.getEquipment().entrySet()) {
                if (!first) situation.append(", ");
                situation.append(entry.getValue());
                first = false;
            }
            situation.append("\n");
        }

        // Notable items
        if (request.getNotableItems() != null && !request.getNotableItems().isEmpty()) {
            situation.append("**Notable Items:** ");
            situation.append(String.join(", ", request.getNotableItems()));
            situation.append("\n");
        }

        // Nearby players
        if (request.getNearbyPlayerNames() != null && !request.getNearbyPlayerNames().isEmpty()) {
            situation.append("**Nearby Players:** ");
            situation.append(String.join(", ", request.getNearbyPlayerNames()));
            situation.append("\n");
        }

        return situation.toString();
    }

    /**
     * Build generation guidance section
     */
    private String buildGenerationGuidance(StorytellingRequest request) {
        StringBuilder guidance = new StringBuilder();
        guidance.append("## Generation Guidelines:\n");

        if (request.getNarrativeTone() != null) {
            guidance.append(String.format("**Tone:** %s\n", request.getNarrativeTone()));
        } else {
            guidance.append("**Tone:** Grimdark survival with hope\n");
        }

        if (request.getDesiredDifficulty() != null) {
            guidance.append(String.format("**Challenge Level:** %.1f/1.0\n",
                request.getDesiredDifficulty()
            ));
        } else {
            guidance.append(String.format("**Challenge Level:** %.1f/1.0 (calculated from location)\n",
                request.getLocalDifficultyRating()
            ));
        }

        if (request.getPreferredType() != null) {
            guidance.append(String.format("**Encounter Type:** %s\n",
                request.getPreferredType()
            ));
        }

        guidance.append("\n");
        return guidance.toString();
    }

    /**
     * Build task instructions for creative story generation (Step 1)
     */
    private String buildTaskInstructions(StorytellingRequest request) {
        StringBuilder instructions = new StringBuilder();
        instructions.append("## Your Task:\n\n");
        instructions.append("Generate a contextually appropriate random encounter for this player. ");
        instructions.append("The encounter should:\n\n");
        instructions.append("1. **Fit the Dregora setting** - Post-apocalyptic, wasteland survival themes\n");
        instructions.append("2. **Respect player history** - Reference or build upon recent encounters\n");
        instructions.append("3. **Advance story threads** - Progress active narratives when appropriate\n");
        instructions.append("4. **Consider faction relations** - NPCs should react to player reputation\n");
        instructions.append("5. **Match difficulty** - Challenge appropriate to location and player level\n");
        instructions.append("6. **Provide choices** - Give players meaningful decisions with consequences\n");
        instructions.append("7. **Create continuity** - Actions should have potential long-term impact\n\n");

        instructions.append("## Response Format:\n\n");
        instructions.append("Write a creative, immersive encounter narrative in markdown format. Include:\n\n");
        instructions.append("- **Title**: A compelling name for the encounter\n");
        instructions.append("- **Type**: combat, exploration, social, trade, or mystery\n");
        instructions.append("- **Setting**: Vivid description of the location and atmosphere\n");
        instructions.append("- **Narrative**: The encounter story with sensory details and tension\n");
        instructions.append("- **Entities**: What creatures/NPCs are involved, their appearance, behavior\n");
        instructions.append("- **Dialogue**: Any spoken interactions\n");
        instructions.append("- **Outcomes**: What happens if the player succeeds, fails, or flees\n\n");

        instructions.append("Be creative and atmospheric. This will be converted to game format in a second step.\n");

        return instructions.toString();
    }

    /**
     * Build conversion prompt for transforming story to JSON (Step 2)
     */
    public String buildConversionPrompt(String narrativeStory) {
        // Always use modular builder for conversion prompts
        return modularBuilder.buildConversionPrompt(narrativeStory);
    }

    /**
     * Legacy conversion prompt builder
     */
    private String buildConversionPromptLegacy(String narrativeStory) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Task: Convert Encounter Story to Game JSON\n\n");
        prompt.append("Convert the following encounter narrative into structured JSON format for a Minecraft mod.\n\n");
        prompt.append("## Original Story:\n\n");
        prompt.append(narrativeStory);
        prompt.append("\n\n");

        prompt.append("## Required JSON Structure:\n\n");
        prompt.append("Return ONLY valid JSON (no markdown, no explanations) with this structure:\n\n");
        prompt.append("{\n");
        prompt.append("  \"encounter_json\": \"{...}\",\n");
        prompt.append("  \"story_updates\": {...}\n");
        prompt.append("}\n\n");

        prompt.append("### encounter_json field (must be an ESCAPED JSON string):\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"Encounter Title\",\n");
        prompt.append("  \"type\": \"combat|exploration|social|trade|mystery\",\n");
        prompt.append("  \"description\": \"Brief one-sentence summary\",\n");
        prompt.append("  \"narrative_text\": \"Full narrative for the player\",\n");
        prompt.append("  \"entities\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"entity_type\": \"zombie|skeleton|spider|creeper|enderman|witch|etc\",\n");
        prompt.append("      \"name\": \"Custom name\",\n");
        prompt.append("      \"count\": 1,\n");
        prompt.append("      \"hostile\": true,\n");
        prompt.append("      \"health_modifier\": 1.0,\n");
        prompt.append("      \"damage_modifier\": 1.0,\n");
        prompt.append("      \"equipment\": [\"item1\", \"item2\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"dialogue\": [\n");
        prompt.append("    {\"speaker\": \"NPC Name\", \"text\": \"What they say\"}\n");
        prompt.append("  ],\n");
        prompt.append("  \"outcomes\": {\n");
        prompt.append("    \"victory_text\": \"Success message\",\n");
        prompt.append("    \"defeat_text\": \"Failure message\",\n");
        prompt.append("    \"flee_text\": \"Escape message\"\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");

        prompt.append("### story_updates field:\n");
        prompt.append("{\n");
        prompt.append("  \"encounter_summary\": \"One-line summary\",\n");
        prompt.append("  \"thread_updates\": [],\n");
        prompt.append("  \"new_threads\": [],\n");
        prompt.append("  \"key_choices\": []\n");
        prompt.append("}\n\n");

        prompt.append("CRITICAL:\n");
        prompt.append("- encounter_json must be a STRING with escaped quotes (\\\")\n");
        prompt.append("- Use only valid Minecraft entity types\n");
        prompt.append("- Return ONLY the JSON, nothing else\n");

        return prompt.toString();
    }

    /**
     * Get reputation description from numeric value
     */
    private String getReputationDescription(int reputation) {
        if (reputation >= 75) return "Revered";
        if (reputation >= 50) return "Honored";
        if (reputation >= 25) return "Friendly";
        if (reputation >= 0) return "Neutral";
        if (reputation >= -25) return "Unfriendly";
        if (reputation >= -50) return "Hostile";
        return "Hated";
    }

    /**
     * Build a simplified prompt for emergent (non-thread) encounters
     */
    public String buildEmergentPrompt(StorytellingRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append(DREGORA_LORE_CONTEXT);
        prompt.append("\n## Current Situation:\n");
        prompt.append(buildCurrentSituation(request));
        prompt.append("\n");
        prompt.append(buildGenerationGuidance(request));
        prompt.append("\n## Your Task:\n\n");
        prompt.append("Generate a fresh, emergent encounter that introduces something new to the player's story. ");
        prompt.append("This could be the start of a new story thread, a random event, or an unexpected situation.\n\n");
        prompt.append("Follow the same response format as above.\n");

        return prompt.toString();
    }
}
