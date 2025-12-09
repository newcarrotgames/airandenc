package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;
import ai.torchlite.randomencounters.config.ConfigHandler;

/**
 * Provides world lore and setting context
 */
public class WorldContextSection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return ConfigHandler.enableDregoraLore;
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## World Setting: RLCraft Dregora\n\n");
        section.append("**The Blight:** A mysterious corruption that devastated civilization. ");
        section.append("It twists nature, mutates creatures, and drives people to madness. ");
        section.append("The exact origin remains unknown, though many seek answers.\n\n");

        section.append("**Survivors & Factions:** Scattered groups fight for survival in the wasteland. ");
        section.append("Factions control territory through patrols, scouts, and armed groups. ");
        section.append("Trust is earned through actions; reputation determines how NPCs treat you. ");
        section.append("Faction members travel, trade, hunt, and defend their interests.\n\n");

        section.append("**Technology:** Pre-Blight technology is highly valued. Ancient weapons, ");
        section.append("medical supplies, and power sources are carried by traders and sought by scavengers. ");
        section.append("Faction members may be armed with salvaged gear.\n\n");

        if (ConfigHandler.emphasizeBlight) {
            section.append("**Blight Influence:** The corruption is ever-present. ");
            section.append("It can appear suddenly, spreading through air, water, or touch. ");
            section.append("Long exposure causes mutations or death.\n\n");
        }

        section.append("**Tone:** ").append(getToneDescription(ConfigHandler.defaultTone)).append("\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 10; // Show first to establish setting
    }

    private String getToneDescription(String tone) {
        switch (tone.toLowerCase()) {
            case "grimdark_with_hope":
                return "Dark and brutal, but with glimmers of hope. Not all is lost.";
            case "pure_survival":
                return "Harsh and unforgiving. Every day is a struggle to survive.";
            case "mysterious":
                return "Strange and unsettling. The world holds dark secrets.";
            case "rebuilding":
                return "Cautiously optimistic. Humanity struggles to rebuild.";
            default:
                return "Post-apocalyptic survival in a corrupted world.";
        }
    }
}
