package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.story.StorytellingResponse;

/**
 * Interface for AI storytelling service implementations
 */
public interface IAIStorytellingService {

    /**
     * Generate an encounter with story context using AI
     *
     * @param prompt The narrative prompt containing all context
     * @return StorytellingResponse containing encounter and story updates
     * @throws Exception if generation fails
     */
    StorytellingResponse generateEncounter(String prompt) throws Exception;

    /**
     * Check if this service is configured and available
     *
     * @return true if the service can be used
     */
    boolean isAvailable();

    /**
     * Get the name of this service (e.g., "OpenAI GPT-4", "Anthropic Claude")
     *
     * @return service name
     */
    String getServiceName();

    /**
     * Get the priority of this service (lower = tried first)
     *
     * @return priority value
     */
    int getPriority();
}
