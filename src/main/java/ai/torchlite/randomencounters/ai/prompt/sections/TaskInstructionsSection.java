package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;

/**
 * Provides the core task instructions for the AI
 */
public class TaskInstructionsSection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return true; // Always include task instructions
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## Your Task:\n\n");
        section.append("Generate a contextually appropriate random encounter for this player. ");
        section.append("The encounter should:\n\n");

        section.append("1. **Fit the Setting** - Match the biome, time, weather, and world lore\n");
        section.append("2. **Respect Player History** - Reference or build upon recent encounters if available\n");
        section.append("3. **Match Difficulty** - Challenge appropriate to location and player level\n");
        section.append("4. **Respect Faction Presence** - Only include factions appropriate to this biome\n");
        section.append("5. **Provide Choices** - Give players meaningful decisions with consequences\n");
        section.append("6. **Create Continuity** - Actions should have potential long-term impact\n");
        section.append("7. **Be Immersive** - Use sensory details, tension, and atmospheric writing\n\n");

        section.append("## Response Format:\n\n");
        section.append("Write a creative, immersive encounter narrative in markdown format. Include:\n\n");
        section.append("- **Title**: A compelling name for the encounter\n");
        section.append("- **Type**: combat, exploration, social, trade, or mystery\n");
        section.append("- **Setting**: Vivid description of the location and atmosphere\n");
        section.append("- **Narrative**: The encounter story with sensory details and tension\n");
        section.append("- **Entities**: What creatures/NPCs are involved, their appearance, behavior\n");
        section.append("- **Dialogue**: Any spoken interactions\n");
        section.append("- **Outcomes**: What happens if the player succeeds, fails, or flees\n\n");

        section.append("Be creative and atmospheric. This will be converted to game format in a second step.\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 90; // Show near the end, before generation guidance
    }
}
