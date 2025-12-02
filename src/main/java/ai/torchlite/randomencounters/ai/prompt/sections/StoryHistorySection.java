package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;

/**
 * Provides player's story history and narrative summary
 */
public class StoryHistorySection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return request.getNarrativeSummary() != null &&
               !request.getNarrativeSummary().isEmpty();
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## Player's Story\n\n");
        section.append(request.getNarrativeSummary()).append("\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 40; // Show after basic context
    }
}
