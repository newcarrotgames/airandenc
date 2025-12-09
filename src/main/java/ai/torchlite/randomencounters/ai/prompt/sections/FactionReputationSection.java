package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;
import ai.torchlite.randomencounters.story.PlayerStoryState;
import ai.torchlite.randomencounters.story.StoryStateManager;

import java.util.Map;

/**
 * Provides faction reputation context for encounter generation
 */
public class FactionReputationSection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return true; // Always show faction info
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## Faction Relations\n\n");

        // Get player's faction reputation from story state
        // Note: We can't access player UUID from the request at this stage,
        // so we'll show generic faction guidance. The AI will consider
        // reputation from the narrative summary if available.
        Map<String, Integer> reputations = null;

        // If reputation data was provided in the request context, use it
        // This would need to be added to StorytellingRequest in the future

        if (reputations != null && !reputations.isEmpty()) {
            section.append("**Player's Faction Standing:**\n");
            for (Map.Entry<String, Integer> entry : reputations.entrySet()) {
                String faction = entry.getKey();
                int rep = entry.getValue();
                section.append("- ").append(faction).append(": ")
                       .append(rep).append(" (").append(getReputationLevel(rep)).append(")\n");
            }
            section.append("\n");
        }

        section.append("**IMPORTANT - Faction-Based Encounters:**\n");
        section.append("- Generate encounters involving NPCs and creatures from the local factions\n");
        section.append("- NPCs should react based on the player's reputation with their faction\n");
        section.append("- Hostile factions (negative reputation) may attack on sight\n");
        section.append("- Friendly factions (positive reputation) may offer aid or trade\n");
        section.append("- Neutral factions may be cautious or offer quests\n\n");

        section.append("**DO NOT:**\n");
        section.append("- Generate new buildings, camps, or structures to explore\n");
        section.append("- Reference specific named locations that don't exist\n");
        section.append("- Create encounters requiring world generation\n\n");

        section.append("**DO:**\n");
        section.append("- Create encounters with NPCs representing local factions\n");
        section.append("- Focus on character interactions, combat, or dialogue\n");
        section.append("- Use the existing terrain and biome as the backdrop\n");
        section.append("- Create patrols, scouts, traders, or lone wanderers from factions\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 45; // After story history, before difficulty
    }

    private String getReputationLevel(int reputation) {
        if (reputation >= 75) return "Exalted";
        if (reputation >= 50) return "Revered";
        if (reputation >= 25) return "Honored";
        if (reputation >= 10) return "Friendly";
        if (reputation >= -10) return "Neutral";
        if (reputation >= -25) return "Unfriendly";
        if (reputation >= -50) return "Hostile";
        return "Hated";
    }
}
