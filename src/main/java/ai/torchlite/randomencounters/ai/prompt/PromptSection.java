package ai.torchlite.randomencounters.ai.prompt;

import ai.torchlite.randomencounters.ai.StorytellingRequest;

/**
 * A modular section of the encounter generation prompt
 */
public interface PromptSection {

    /**
     * Check if this section should be included in the prompt
     * @param request The storytelling request with context
     * @return true if this section is applicable
     */
    boolean isApplicable(StorytellingRequest request);

    /**
     * Build the content for this section
     * @param request The storytelling request with context
     * @return The formatted section content
     */
    String buildSection(StorytellingRequest request);

    /**
     * Get the priority order for this section (lower = earlier in prompt)
     * @return priority value
     */
    default int getPriority() {
        return 100;
    }
}
