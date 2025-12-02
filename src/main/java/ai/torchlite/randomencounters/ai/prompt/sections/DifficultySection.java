package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;

/**
 * Provides difficulty and encounter balance guidance
 */
public class DifficultySection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return true; // Always include difficulty guidance
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        float difficulty = request.getLocalDifficultyRating();
        int playerLevel = request.getPlayerLevel();

        section.append("## Encounter Difficulty\n\n");
        section.append("**Local Difficulty:** ").append(String.format("%.2f", difficulty))
               .append(" (").append(getDifficultyDescription(difficulty)).append(")\n");
        section.append("**Player Level:** ").append(playerLevel).append("\n\n");

        section.append("**Balance Guidelines:**\n");

        if (difficulty < 0.3f) {
            section.append("- Create easier encounters, perhaps opportunities for trade or information\n");
            section.append("- 1-2 weak entities or non-hostile NPCs\n");
            section.append("- Focus on narrative and world-building\n");
        } else if (difficulty < 0.6f) {
            section.append("- Balanced challenge appropriate for the player's level\n");
            section.append("- 2-3 moderate entities, possibly with mixed hostility\n");
            section.append("- Provide tactical options and meaningful choices\n");
        } else {
            section.append("- Challenging encounter with significant danger\n");
            section.append("- 3-5 entities or strong enemies with enhanced abilities\n");
            section.append("- Player should feel pressured but not hopeless\n");
        }

        section.append("\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 50; // Show after player context, before task instructions
    }

    private String getDifficultyDescription(float difficulty) {
        if (difficulty < 0.2f) return "Very Easy";
        if (difficulty < 0.4f) return "Easy";
        if (difficulty < 0.6f) return "Moderate";
        if (difficulty < 0.8f) return "Hard";
        return "Very Hard";
    }
}
