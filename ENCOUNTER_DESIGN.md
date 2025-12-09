# Encounter Design Guidelines

## Purpose

This document explains how AI-generated encounters are designed to work with Minecraft/RLCraft mechanics.

## Core Principle

**Every encounter MUST spawn actual Minecraft entities that affect gameplay.**

Encounters are not just stories - they translate directly into in-game mechanics:
- Entities spawn near the player
- Combat happens with real mobs
- Trade interactions use villagers
- Choices affect faction reputation

## What Gets Spawned

### Entity Types

The mod can spawn any Minecraft entity, including:

**Hostile Mobs (for enemies):**
- `zombie` - Wasteland Raiders, Scavengers, Blight-infected humans
- `skeleton` - Scouts, Archers, Pre-war Military
- `spider` - Mutant creatures, Blight-touched beasts
- `creeper` - Unstable experiments, Suicide bombers
- `witch` - Blight Cultists, Mad Scientists
- `enderman` - Eldritch horrors, Void-touched
- `blaze` - Fire cult members, Wasteland pyromancers
- `ghast` - Flying terrors, Aerial scouts

**Neutral/Friendly (for NPCs):**
- `villager` - Traders, Faction representatives, Quest givers
- `wolf` - Guard animals, Companions
- `iron_golem` - Faction guards, Protectors

**RLCraft Modded (if available):**
- Any modded entity using its registry name (e.g., `lycanitesmobs:geonach`)

### Equipment System

Entities can be equipped with any Minecraft item:

**Weapons:**
- `iron_sword`, `diamond_sword`, `stone_axe`
- `bow`, `crossbow`
- Modded weapons work too

**Armor:**
- `iron_helmet`, `iron_chestplate`, `iron_leggings`, `iron_boots`
- `leather_`, `chainmail_`, `gold_`, `diamond_` variants
- Mix and match for variety

**Example Equipped NPC:**
```json
{
  "entity_type": "zombie",
  "name": "Wasteland Raider Captain",
  "count": 1,
  "hostile": true,
  "health_modifier": 2.0,
  "damage_modifier": 1.5,
  "equipment": [
    "diamond_sword",
    "iron_helmet",
    "iron_chestplate",
    "iron_leggings",
    "iron_boots"
  ]
}
```

## Encounter Types

### 1. Combat Encounters

**What Spawns:** 1-5 hostile entities with equipment
**Player Action:** Kill the entities
**Outcome:** Loot drops, faction reputation changes

**Example:**
- **Wasteland Raider Patrol**: 3 zombies with iron swords and leather armor
- **Blight Cultist Ambush**: 2 witches with potions
- **Pre-war Military Remnant**: 2 skeletons with bows and chainmail

### 2. Trade Encounters

**What Spawns:** 1-2 villagers (traders)
**Player Action:** Interact via dialogue, trade items
**Outcome:** Items exchanged, faction reputation gain

**Example:**
- **Nomad Merchant**: 1 villager named "Trader Marcus"
- **Caravan Guard**: 1 villager + 1 iron_golem

### 3. Social Encounters

**What Spawns:** 1-3 NPCs (villagers or neutral mobs)
**Player Action:** Talk, make choices via dialogue
**Outcome:** Story progression, reputation changes, information gained

**Example:**
- **Faction Scout**: 1 skeleton named "Iron Legion Scout" (non-hostile if reputation high)
- **Survivor Request**: 1 villager asking for help

### 4. Mystery Encounters

**What Spawns:** Entities that reveal lore or lead to discoveries
**Player Action:** Investigate, follow clues
**Outcome:** Story threads created, secrets revealed

**Example:**
- **Strange Signal**: 1 enderman with custom behavior
- **Corrupted Creature**: 1 spider with high health (boss-like)

## What NOT to Do

### ❌ Abstract/Narrative-Only Encounters

**BAD:**
```
"You feel a strange presence watching you from the ruins..."
Entities: []
```
This does nothing in-game! No entities spawn, nothing happens.

**GOOD:**
```
"You feel eyes watching you. From the shadows, a Blight Scout emerges..."
Entities: [
  {
    "entity_type": "skeleton",
    "name": "Blight Scout",
    "count": 1,
    "hostile": true,
    "equipment": ["bow", "leather_helmet"]
  }
]
```
This spawns an actual skeleton that the player must deal with.

### ❌ Structure/Location-Based

**BAD:**
```
"You discover an abandoned military bunker. Inside, you find..."
```
The mod can't create buildings or structures.

**GOOD:**
```
"You stumble upon a Pre-war Military patrol. Two soldiers in decayed uniforms..."
Entities: [
  {
    "entity_type": "skeleton",
    "name": "Pre-war Soldier",
    "count": 2,
    "hostile": true,
    "equipment": ["iron_sword", "chainmail_chestplate"]
  }
]
```
This spawns entities in the existing terrain.

### ❌ Empty Entity Arrays

**NEVER:**
```json
{
  "entities": []
}
```

**ALWAYS have at least 1 entity:**
```json
{
  "entities": [
    {
      "entity_type": "zombie",
      "name": "Wandering Scavenger",
      "count": 1,
      "hostile": true
    }
  ]
}
```

## Faction Mapping

Different factions spawn different entity types:

| Faction | Entity Type | Equipment Style | Behavior |
|---------|-------------|-----------------|----------|
| Wasteland Raiders | zombie, spider | Iron weapons, leather armor | Always hostile |
| Blight Cultists | witch, enderman | Potions, corrupted items | Hostile, mysterious |
| Nomad Traders | villager | Gold, food items | Friendly, trade-focused |
| Pre-war Military | skeleton, iron_golem | Military gear, guns (bows) | Disciplined, patrol |
| Techno-Scavengers | zombie, creeper | Tools, tech items | Neutral, opportunistic |
| Independent Survivors | villager, wolf | Mixed equipment | Varies by reputation |

## Difficulty Scaling

Control difficulty through entity properties:

**Easy (Level 1-5):**
- 1-2 entities
- Basic equipment (stone, leather)
- health_modifier: 0.8-1.0

**Medium (Level 6-15):**
- 2-3 entities
- Iron equipment
- health_modifier: 1.0-1.5

**Hard (Level 16+):**
- 3-5 entities
- Diamond/Enchanted equipment
- health_modifier: 1.5-2.5
- damage_modifier: 1.2-2.0

## Outcomes and Consequences

Every encounter should have clear outcomes:

### Victory
- Player defeats all entities
- Loot drops from killed mobs (automatic)
- Faction reputation gain/loss
- Story progression

**Example:**
```
"You defeated the Wasteland Raiders! As they fall, you notice they carried supplies
from a nearby settlement. Your reputation with the Nomad Traders increases."
```

### Defeat
- Player dies or entities despawn
- No loot gained
- Possible reputation loss
- Story consequences

**Example:**
```
"You fled from the Cultists. They report your presence to their masters.
Your reputation with the Blight Cult worsens."
```

### Flee
- Player escapes without combat
- Entities despawn
- Neutral outcome, minor consequences

**Example:**
```
"You chose not to engage the patrol. They continue on their route,
unaware of your presence."
```

## Testing Encounters

When testing, ask:

1. ✅ **Does it spawn entities?** - At least 1 entity must spawn
2. ✅ **Can the player interact?** - Combat, dialogue, or choice
3. ✅ **Are outcomes clear?** - What happens for each choice
4. ✅ **Does it fit the biome?** - Desert raiders in desert, snow creatures in tundra
5. ✅ **Is difficulty appropriate?** - Entity count/strength matches player level
6. ✅ **Does it affect reputation?** - Faction standing changes based on choices

## Examples

### Good Combat Encounter

**Title:** "Scavenger Ambush"

**Narrative:** "Three figures emerge from behind the rocks - Wasteland Scavengers, their weapons drawn. They demand your supplies!"

**Entities:**
```json
[
  {
    "entity_type": "zombie",
    "name": "Wasteland Scavenger",
    "count": 3,
    "hostile": true,
    "health_modifier": 1.2,
    "equipment": ["iron_sword", "leather_helmet", "leather_chestplate"]
  }
]
```

**Outcomes:**
- Victory: "You defeat the scavengers. They drop some food and iron."
- Defeat: "You were overwhelmed. The scavengers take your items."
- Flee: "You escape into the wasteland. The scavengers don't pursue."

### Good Trade Encounter

**Title:** "Traveling Merchant"

**Narrative:** "A lone merchant approaches, guarded by two wolves. 'Looking to trade?' he calls out."

**Entities:**
```json
[
  {
    "entity_type": "villager",
    "name": "Trader Silas",
    "count": 1,
    "hostile": false
  },
  {
    "entity_type": "wolf",
    "name": "Guard Wolf",
    "count": 2,
    "hostile": false
  }
]
```

**Dialogue:**
```json
[
  {
    "speaker": "Trader Silas",
    "text": "I've got food, tools, and information. What do you need?"
  }
]
```

## Summary

**The golden rule:** Every encounter must answer the question:
> "What actually spawns in Minecraft when this encounter triggers?"

If the answer is "nothing" or "just a message," the encounter needs entities!
