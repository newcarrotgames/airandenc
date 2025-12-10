# Dynamic Entity Registry

## Overview

The mod now automatically discovers **all available entities** at startup and provides this information to the AI for encounter generation. This means the mod will work with any combination of mods without needing manual configuration.

## How It Works

### 1. Entity Discovery (Startup)

When the mod loads during the `FMLInitializationEvent`, it:

1. **Scans the entity registry** - Uses `EntityList.getEntityNameList()` to find all registered entities
2. **Categorizes entities** - Classifies each entity as hostile, neutral, passive, or boss
3. **Adds descriptions** - Provides helpful context for each entity type
4. **Logs the results** - Shows what was discovered in the console

Example startup log:
```
[RandomEncounters] Entity registry initialized
[RandomEncounters] Entity discovery complete:
[RandomEncounters]   Total entities: 247
[RandomEncounters]   Hostile: 89
[RandomEncounters]   Neutral: 23
[RandomEncounters]   Passive: 31
[RandomEncounters]   Boss: 4
```

### 2. Entity Categorization

Entities are categorized by their class hierarchy:

**Hostile Entities** (extends `EntityMob`, `EntitySlime`, `EntityGhast`, `EntityBlaze`)
- Zombies, Skeletons, Spiders, Creepers, Witches, Endermen
- Lycanites hostile creatures (Cinders, Reapers, Geonachs, etc.)
- Ice and Fire dragons, cyclops, etc.
- Parasites and other hostile modded creatures

**Passive Entities** (extends `EntityAnimal`, `EntityVillager`, `EntityGolem`)
- Villagers, Wolves, Horses, Cows, Sheep
- Iron Golems, Snow Golems
- Friendly modded creatures

**Neutral Entities** (extends `EntityLiving` but not hostile/passive)
- Zombie Pigmen, Endermen (can be both)
- Polar Bears
- Neutral modded creatures

**Boss Entities** (special detection)
- Ender Dragon, Wither
- Modded bosses (detected by name patterns: "dragon", "wither", "boss")

### 3. AI Prompt Integration

The discovered entities are automatically included in AI prompts:

**Story Generation Prompt** (Step 1):
Shows up to 20 entities per category with descriptions:
```
**HOSTILE ENTITIES (for combat encounters):**
  - `minecraft:zombie` - Melee attacker - good for raiders, scavengers
  - `minecraft:skeleton` - Ranged attacker - good for scouts, soldiers
  - `lycanitesmobs:geonach` - Hostile creature
  - `iceandfire:fire_dragon` - Boss creature - extremely dangerous
  ... and 85 more
```

**JSON Conversion Prompt** (Step 2):
Shows a compact list of available entities:
```
Valid entity types: minecraft:zombie, minecraft:skeleton, minecraft:spider,
minecraft:creeper, minecraft:witch, minecraft:enderman, minecraft:villager,
minecraft:wolf, minecraft:iron_golem, lycanitesmobs:geonach,
lycanitesmobs:cinder, iceandfire:fire_dragon, plus 235 more
```

## Benefits

### 1. **Automatic Mod Compatibility**
No need to manually configure which entities are available. The mod discovers:
- All vanilla Minecraft entities
- Lycanites Mobs creatures
- Ice and Fire dragons and creatures
- Parasites
- Any other modded entities

### 2. **Better AI Understanding**
The AI knows exactly what's available and can:
- Use modded entities creatively (dragons for epic encounters)
- Mix vanilla and modded entities
- Choose appropriate entities for the context

### 3. **Helpful Descriptions**
Each entity gets a description to guide the AI:
- "Melee attacker - good for raiders, scavengers"
- "Ranged attacker - good for scouts, soldiers"
- "NPC - perfect for traders, quest givers"
- "Guardian - good for protectors, guards"

### 4. **Safe Boss Handling**
Boss entities are flagged separately with warnings:
- "Boss creature - extremely dangerous"
- The AI is instructed to use them sparingly

## Technical Details

### EntityRegistry Class

**Location:** `ai.torchlite.randomencounters.entity.EntityRegistry`

**Key Methods:**

```java
// Get singleton instance
EntityRegistry registry = EntityRegistry.getInstance();

// Get all entities
List<String> all = registry.getAllEntities();

// Get by category
List<String> hostile = registry.getHostileEntities();
List<String> passive = registry.getPassiveEntities();
List<String> neutral = registry.getNeutralEntities();
List<String> bosses = registry.getBossEntities();

// Get description
String desc = registry.getEntityDescription("minecraft:zombie");
// Returns: "Melee attacker - good for raiders, scavengers"

// Build formatted list for prompts
String promptList = registry.buildEntityListForPrompt(true, 20);
String compactList = registry.buildCompactEntityList();

// Validate entity exists
boolean valid = registry.isValidEntity("minecraft:zombie"); // true
boolean invalid = registry.isValidEntity("nonexistent:entity"); // false
```

### Initialization

The registry is initialized in `RandomEncounters.init()`:

```java
@EventHandler
public void init(FMLInitializationEvent event) {
    // Discover available entities
    EntityRegistry.getInstance();
    LOGGER.info("Entity registry initialized");

    // ... rest of initialization
}
```

### Prompt Integration

Both prompt sections use the registry:

**TaskInstructionsSection.java** (Story generation):
```java
EntityRegistry registry = EntityRegistry.getInstance();
section.append(registry.buildEntityListForPrompt(true, 20));
```

**ModularPromptBuilder.java** (JSON conversion):
```java
EntityRegistry registry = EntityRegistry.getInstance();
prompt.append("- " + registry.buildCompactEntityList() + "\n");
```

## Example Encounters with Modded Entities

### Lycanites Encounter
```json
{
  "title": "Geonach Awakening",
  "entities": [
    {
      "entity_type": "lycanitesmobs:geonach",
      "name": "Ancient Stone Guardian",
      "count": 1,
      "hostile": true,
      "health_modifier": 2.0
    }
  ],
  "narrative_text": "The ground trembles as a massive stone elemental rises..."
}
```

### Ice and Fire Encounter
```json
{
  "title": "Dragon's Claim",
  "entities": [
    {
      "entity_type": "iceandfire:fire_dragon",
      "name": "Scorchwing the Terrible",
      "count": 1,
      "hostile": true,
      "health_modifier": 3.0,
      "damage_modifier": 2.0
    }
  ],
  "narrative_text": "A shadow passes overhead. You look up to see massive wings..."
}
```

### Mixed Encounter
```json
{
  "title": "Cult of the Parasite",
  "entities": [
    {
      "entity_type": "minecraft:witch",
      "name": "Corrupted Cultist",
      "count": 1,
      "hostile": true,
      "health_modifier": 1.5
    },
    {
      "entity_type": "srparasites:rupter",
      "name": "Parasitic Minion",
      "count": 3,
      "hostile": true
    }
  ],
  "narrative_text": "A robed figure stands among writhing parasitic creatures..."
}
```

## Limitations

### Entity Class Detection
The system categorizes entities based on their class hierarchy. Some modded entities might not fit perfectly:
- Custom hostile entities that don't extend `EntityMob` might be categorized as neutral
- Custom NPCs that don't extend `EntityVillager` might be categorized incorrectly

### Equipment Support
Not all entities can be equipped:
- Some modded entities don't support standard equipment slots
- Flying entities might not render armor properly
- Boss entities often ignore equipment

### Spawn Success
The spawner can attempt to spawn any entity, but some might fail:
- Entities requiring special conditions (water, lava, etc.)
- Entities with custom spawn logic
- Dimension-locked entities

The spawner logs failures and continues with other entities.

## Future Enhancements

Potential improvements:

1. **Entity Tags** - Use Forge tags to better categorize modded entities
2. **Blacklist/Whitelist** - Config option to exclude certain entities
3. **Equipment Compatibility** - Detect which entities support equipment
4. **Spawn Requirements** - Check entity spawn conditions before spawning
5. **Custom Descriptions** - Allow users to add custom entity descriptions
6. **Entity Variants** - Support for entity variants (baby zombies, wither skeletons, etc.)

## Summary

The dynamic entity registry makes the mod **fully compatible with any mod pack** by automatically discovering and categorizing all available entities. The AI receives this information and can use any entity creatively in encounters, from vanilla zombies to modded dragons and parasites.
