package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;
import ai.torchlite.randomencounters.entity.EntityRegistry;

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
        section.append("Generate a **MINECRAFT/RLCRAFT ENCOUNTER** that is creative, atmospheric, and immersive ");
        section.append("while using ONLY the game mechanics available in Minecraft.\n\n");
        section.append("Be as creative as possible with:\n");
        section.append("- Naming entities (e.g., \"Corrupted Wanderer\", \"Pre-war Sentinel\", \"Blight-touched Beast\")\n");
        section.append("- Writing vivid narrative descriptions and dialogue\n");
        section.append("- Creating compelling backstories and motivations\n");
        section.append("- Designing unique equipment combinations\n");
        section.append("- Crafting interesting outcomes and consequences\n\n");
        section.append("But remember: Every encounter MUST translate to actual spawnable Minecraft entities with equipment.\n\n");

        section.append("**REQUIREMENTS:**\n\n");

        section.append("1. **SPAWN ENTITIES** - Every encounter MUST include at least 1-3 spawnable entities\n\n");

        // Include dynamic entity list from registry
        EntityRegistry registry = EntityRegistry.getInstance();
        section.append(registry.buildEntityListForPrompt(true, 20));
        section.append("\n");

        section.append("**Entity Usage Guidelines:**\n");
        section.append("   - Hostile factions spawn hostile mobs (zombies, skeletons, spiders, modded creatures)\n");
        section.append("   - Friendly factions spawn NPCs (villagers, wolves as guards, iron golems)\n");
        section.append("   - Traders can be villagers with custom names\n");
        section.append("   - Scouts/patrols are skeletons or zombies with custom names and equipment\n");
        section.append("   - Use modded entities for unique encounters (dragons, elementals, parasites, etc.)\n");
        section.append("   - Mix vanilla and modded entities for variety\n\n");

        section.append("2. **GIVE THEM EQUIPMENT** - NPCs should have appropriate gear\n");
        section.append("   - Weapons: iron_sword, bow, crossbow, diamond_sword\n");
        section.append("   - Armor: iron_helmet, iron_chestplate, leather_boots, chainmail_leggings\n");
        section.append("   - Raiders have weapons, scouts have bows, traders carry gold\n\n");

        section.append("3. **ACTIONABLE OUTCOMES** - Player choices must have clear results\n");
        section.append("   - Combat: Kill the entities (victory_text describes loot/reputation gain)\n");
        section.append("   - Trade: Offer items via dialogue (mention specific items)\n");
        section.append("   - Flee: Escape and entities despawn (defeat_text describes consequences)\n\n");

        section.append("4. **RESPECT FACTION RELATIONS** - NPCs react based on player's reputation\n");
        section.append("   - High reputation: Friendly greetings, trade offers, assistance\n");
        section.append("   - Low reputation: Hostile attacks, demands for tribute\n");
        section.append("   - Neutral: Cautious but willing to negotiate\n\n");

        section.append("**CRITICAL CONSTRAINTS (What You CANNOT Do):**\n");
        section.append("- ❌ Abstract/narrative-only encounters with no entities (\"you feel a presence watching...\")\n");
        section.append("- ❌ Buildings, camps, settlements, or physical structures (we can't build these)\n");
        section.append("- ❌ Environmental hazards without entities (traps, fires, collapsing ruins)\n");
        section.append("- ❌ Quest items or loot that must be placed in chests (we can't spawn items directly)\n");
        section.append("- ❌ Scripted sequences or cutscenes (only entity spawning and dialogue)\n\n");
        section.append("**CREATIVE WORKAROUNDS (How to Make It Work):**\n");
        section.append("- Instead of \"abandoned camp\" → \"Scavenger patrol guarding supplies\"\n");
        section.append("- Instead of \"mysterious artifact\" → \"Enderman carrying a glowing item\"\n");
        section.append("- Instead of \"locked bunker\" → \"Iron Golems guarding a Pre-war officer\"\n");
        section.append("- Instead of \"treasure chest\" → \"Villager trader with rare items\"\n");
        section.append("- Instead of \"trapped ruins\" → \"Spiders and creepers lurking in the area\"\n\n");
        section.append("Think: \"What creative entity combination can represent this concept?\"\n\n");

        section.append("**CREATIVE EXAMPLES (for inspiration):**\n\n");
        section.append("Example 1: \"The Blight Prophet\"\n");
        section.append("- Entities: 1 Witch (\"Blight Prophet\", health x2.0) + 3 Spiders (\"Corrupted Crawlers\")\n");
        section.append("- Equipment: Witch has potions, spiders are unequipped but numerous\n");
        section.append("- Narrative: A witch preaching about the Blight, surrounded by mutated spiders\n\n");
        section.append("Example 2: \"Wasteland Caravan\"\n");
        section.append("- Entities: 1 Villager (\"Trader Silas\") + 2 Wolves (\"Guard Hounds\") + 1 Iron Golem (\"Caravan Sentinel\")\n");
        section.append("- Equipment: Villager unequipped, golem is protection\n");
        section.append("- Narrative: A traveling merchant with heavy protection, willing to trade\n\n");
        section.append("Example 3: \"Pre-war Military Remnant\"\n");
        section.append("- Entities: 1 Skeleton (\"Captain\", health x1.8, damage x1.5) + 2 Skeletons (\"Soldiers\")\n");
        section.append("- Equipment: Captain has bow + full chainmail, soldiers have bows + partial armor\n");
        section.append("- Narrative: Disciplined undead soldiers from before the apocalypse, still on patrol\n\n");

        section.append("## Response Format:\n\n");
        section.append("Write a creative, immersive encounter narrative in markdown format. Include:\n\n");
        section.append("- **Title**: A compelling, evocative name\n");
        section.append("- **Type**: combat, exploration, social, trade, or mystery\n");
        section.append("- **Setting**: Vivid description with sensory details (sights, sounds, smells)\n");
        section.append("- **Narrative**: The encounter story with atmosphere and tension\n");
        section.append("- **Entities**: What creatures/NPCs appear, their appearance, behavior, and equipment\n");
        section.append("- **Dialogue**: Spoken interactions that reveal personality and motivation\n");
        section.append("- **Outcomes**: Compelling consequences for success, failure, or fleeing\n\n");

        section.append("Be maximally creative with naming, descriptions, and storytelling while staying within Minecraft's entity system.\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 90; // Show near the end, before generation guidance
    }
}
