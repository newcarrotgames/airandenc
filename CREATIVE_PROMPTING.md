# Creative Prompting Strategy

## Philosophy

The mod now encourages **maximum creativity within technical constraints**. The AI is instructed to be as creative as possible with storytelling, naming, and atmosphere while strictly adhering to what's actually implementable in Minecraft.

## Key Changes

### 1. Emphasis on Creative Freedom

The prompt now starts with:
> "Generate a **MINECRAFT/RLCRAFT ENCOUNTER** that is creative, atmospheric, and immersive while using ONLY the game mechanics available in Minecraft."

Encourages creativity in:
- ✅ Entity naming (e.g., "Corrupted Wanderer", "Pre-war Sentinel", "Blight-touched Beast")
- ✅ Vivid narrative descriptions and dialogue
- ✅ Compelling backstories and motivations
- ✅ Unique equipment combinations
- ✅ Interesting outcomes and consequences

### 2. Clear Constraints with Creative Workarounds

Instead of just saying "don't do this", the prompt now provides **creative alternatives**:

| ❌ What You CAN'T Do | ✅ Creative Workaround |
|---------------------|----------------------|
| "Abandoned camp" | "Scavenger patrol guarding supplies" |
| "Mysterious artifact" | "Enderman carrying a glowing item" |
| "Locked bunker" | "Iron Golems guarding a Pre-war officer" |
| "Treasure chest" | "Villager trader with rare items" |
| "Trapped ruins" | "Spiders and creepers lurking in the area" |

This teaches the AI to **translate abstract concepts into entity combinations**.

### 3. Inspiring Examples

The prompt includes three creative examples:

**Example 1: "The Blight Prophet"**
- 1 Witch ("Blight Prophet", health x2.0) + 3 Spiders ("Corrupted Crawlers")
- Shows: Boss-like entity with minions, creative naming

**Example 2: "Wasteland Caravan"**
- 1 Villager ("Trader Silas") + 2 Wolves ("Guard Hounds") + 1 Iron Golem ("Caravan Sentinel")
- Shows: Multi-entity composition, mix of friendly types, protective setup

**Example 3: "Pre-war Military Remnant"**
- 1 Skeleton ("Captain", health x1.8, damage x1.5) + 2 Skeletons ("Soldiers")
- Equipment: Captain has bow + full chainmail, soldiers have bows + partial armor
- Shows: Hierarchical groups, varied equipment, lore-driven design

### 4. Conversion Prompt Enhancements

The JSON conversion step now includes explicit creative guidance:

```
- BE CREATIVE with entity names! Examples: "Corrupted Wanderer", "Pre-war Sentinel",
  "Blight Prophet", "Wasteland Caravan Master"

- MIX entity types creatively: A villager (trader) + wolves (guards),
  skeleton (sniper) + zombies (melee), witch (leader) + spiders (minions)

- VARY equipment: Not every raider needs full iron armor.
  Mix leather, chainmail, gold, iron for variety

- USE health/damage modifiers to create bosses:
  health_modifier: 2.5 for a tough leader, 0.8 for a weakling
```

## What This Achieves

### Before (Generic Encounters)
```json
{
  "title": "Zombie Attack",
  "entities": [
    {
      "entity_type": "zombie",
      "name": "Zombie",
      "count": 2,
      "hostile": true,
      "equipment": ["iron_sword"]
    }
  ]
}
```

### After (Creative Encounters)
```json
{
  "title": "The Corrupted Scavengers",
  "entities": [
    {
      "entity_type": "zombie",
      "name": "Blight-touched Scavenger",
      "count": 2,
      "hostile": true,
      "health_modifier": 1.2,
      "equipment": ["stone_axe", "leather_helmet", "chainmail_chestplate"]
    },
    {
      "entity_type": "spider",
      "name": "Wasteland Crawler",
      "count": 1,
      "hostile": true,
      "health_modifier": 0.8
    }
  ],
  "narrative_text": "As you traverse the desolate wasteland, the stench of decay fills the air.
  Three figures emerge from behind rusted debris - two hunched humanoids with
  blackened skin and makeshift weapons, accompanied by a grotesque, oversized spider.
  They've spotted you, and their eyes gleam with desperate hunger..."
}
```

## Technical Boundaries

The prompt explicitly lists what's **impossible** in the current implementation:

❌ **Cannot Do:**
- Abstract/narrative-only encounters (no entities)
- Physical structures (buildings, camps, settlements)
- Environmental hazards without entities (traps, fires)
- Quest items in chests (can't spawn items directly)
- Scripted sequences or cutscenes

✅ **Can Do:**
- Spawn any Minecraft entity (vanilla or modded)
- Give entities custom names with color/formatting
- Equip entities with any item
- Modify health and damage
- Create dialogue and narrative text
- Define multiple outcome texts
- Mix entity types creatively

## Entity Variety Encouraged

The prompt now explicitly encourages **mixing entity types** for interesting compositions:

### Combat Compositions
- **Melee + Ranged:** Zombies (frontline) + Skeletons (backline)
- **Leader + Minions:** Witch (boss) + Spiders (swarm)
- **Tank + DPS:** Iron Golem (protector) + Skeletons (damage)

### Social Compositions
- **Trader + Guards:** Villager + Wolves + Iron Golem
- **Diplomat + Escort:** Villager + Skeletons (honor guard)
- **Scouts + Wildlife:** Skeletons + Wolves (patrol)

### Equipment Variety
Instead of uniform equipment, encourage variety:
- **Captain:** Diamond sword + full iron armor + high health
- **Veterans:** Iron swords + partial chainmail + normal health
- **Recruits:** Stone weapons + leather armor + low health

This creates visual and mechanical interest.

## Atmospheric Writing

The prompt now emphasizes **sensory details**:

- ✅ **Sights:** "Decayed uniforms", "Glowing eyes", "Rusted weapons"
- ✅ **Sounds:** "Rattling bones", "Hissing spiders", "Echoing footsteps"
- ✅ **Smells:** "Stench of decay", "Acrid smoke", "Metallic blood"
- ✅ **Atmosphere:** "Oppressive silence", "Eerie fog", "Distant screams"

The narrative text is displayed to players in-game, so vivid descriptions enhance immersion.

## Outcome Creativity

Victory/defeat/flee texts should be **story-driven**:

### Generic (Before)
- Victory: "You defeated the enemies."
- Defeat: "You were defeated."
- Flee: "You escaped."

### Creative (After)
- Victory: "The Blight Prophet falls, her corrupted minions scattering into the wastes. Among her belongings, you find a tattered journal mentioning 'The Source' - whatever that is..."
- Defeat: "The Prophet's mad cackling follows you as darkness takes you. The Blight spreads ever further..."
- Flee: "You retreat from the Prophet's domain. Her voice echoes behind you: 'You cannot escape the Blight forever...'"

These outcomes:
- Continue the narrative
- Hint at larger story threads
- Create emotional impact
- Suggest consequences

## Summary

The creative prompting strategy achieves:

1. **Maximum creativity** in naming, descriptions, and storytelling
2. **Clear technical boundaries** that prevent impossible requests
3. **Inspiring examples** that show what's achievable
4. **Creative workarounds** for translating abstract concepts to entities
5. **Atmospheric writing** with sensory details
6. **Entity variety** through mixing types and equipment
7. **Story-driven outcomes** that enhance immersion

The result: **Encounters that feel unique and atmospheric while being 100% implementable in Minecraft.**
