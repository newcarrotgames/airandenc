# Encounter Commands

## Overview

The `/encounter` command provides access to story state, reputation, context preview, and encounter generation.

## Commands

### `/encounter reputation` (or `/encounter rep`)
**Permission:** None (all players)
**Description:** View your faction reputations

Shows a sorted list of all factions you've encountered and your standing with them.

**Example Output:**
```
=== Faction Reputations ===
  Nomad Traders: 45 (Friendly)
  Independent Survivors: 12 (Neutral)
  Wasteland Raiders: -30 (Hostile)
  Blight Cultists: -50 (Hostile)
```

**Reputation Levels:**
- **Revered** (75+) - Green - Faction treats you as a hero
- **Friendly** (25-74) - Green - Faction is welcoming and helpful
- **Neutral** (0-24) - White - Faction is cautious
- **Unfriendly** (-24 to -1) - Yellow - Faction is suspicious
- **Hostile** (-74 to -25) - Red - Faction may attack on sight
- **Hated** (-75 or lower) - Dark Red - Faction attacks immediately

### `/encounter context` (or `/encounter ctx`)
**Permission:** None (all players)
**Description:** View AI context that would be used for encounter generation

Shows all the metadata that would be sent to the AI if an encounter were generated right now. This is useful for understanding what influences encounter generation.

**Example Output:**
```
=== AI Context Preview ===
This is the context that would be sent to the AI:

Location:
  Biome: Desert
  Dimension: Overworld
  Position: 1234, 64, 5678
  Time: Dusk
  Weather: Clear

Player:
  Level: 23
  Health: 17.5/20.0
  Difficulty Rating: 0.75

Faction Reputations:
  Nomad Traders: 45 (Friendly)
  Wasteland Raiders: -30 (Hostile)

Story State:
  Total Encounters: 15
  Active Threads: 3
  Recent:
    - combat: Defeated a Wasteland Raider patrol
    - trade: Traded with Nomad Merchant Marcus
    - mystery: Investigated strange signals

Notable Items:
  - Diamond Sword (Enchanted)
  - Iron Armor Set
  - Rare Artifacts
```

**Context Sections:**

**Location Context:**
- Current biome (influences encounter type and factions)
- Dimension (Overworld, Nether, End, etc.)
- Coordinates
- Time of day (dawn, day, dusk, night)
- Weather conditions
- Named locations (if near a structure)

**Player Context:**
- Experience level (influences difficulty)
- Current health (AI may generate easier encounters if low)
- Local difficulty rating (0.0-1.0)

**Faction Context:**
- List of all faction reputations
- Color-coded by standing
- Influences which factions appear in encounters

**Story State:**
- Total number of encounters experienced
- Number of active story threads
- Recent encounter summaries (last 3)
- Shows how your history influences new encounters

**Notable Items:**
- Unique or rare items in your inventory
- Enchanted equipment
- Quest items
- AI may reference these in encounter narratives

### `/encounter story`
**Permission:** None (all players)
**Description:** View your complete story state

Shows:
- Player name
- Total encounter count
- Active story threads
- Full faction reputation list

### `/encounter threads`
**Permission:** None (all players)
**Description:** List active story threads

Shows all ongoing story arcs with:
- Thread title
- Priority (LOW, MEDIUM, HIGH, URGENT)
- Progress (0-100%)
- Current objective

### `/encounter history [count]`
**Permission:** None (all players)
**Description:** View recent encounter history

Shows summaries of your last N encounters (default: 5, max: 20).

**Example:**
```
/encounter history 10
```

### `/encounter generate`
**Permission:** None (all players)
**Description:** Generate an AI-powered encounter

Generates a new encounter using:
- Current location and biome
- Faction reputations
- Active story threads
- Player level and equipment
- Recent encounter history

The encounter spawns immediately with entities, dialogue, and outcomes.

### `/encounter clear`
**Permission:** None (all players)
**Description:** Clear your active encounter

Removes any active encounter and despawns all encounter entities. Use this if an encounter is stuck or you want to reset.

### `/encounter services`
**Permission:** None (all players)
**Description:** List available AI services

Shows which AI services are configured and available:
- OpenAI (GPT-4o, o1-preview, etc.)
- Anthropic (Claude)

Also shows which service is currently active.

### `/encounter reload`
**Permission:** Operator (level 2+)
**Description:** Reload configuration from disk

Reloads the randomencounters.cfg file without restarting the server.

## Tab Completion

All commands support tab completion. Press TAB after `/encounter ` to see available subcommands.

## Use Cases

### Planning Your Actions

Use `/encounter context` before generating encounters to see what you'll likely face:

- **In a desert with negative Raider reputation** → Expect hostile Raider encounters
- **At night with low health** → AI may generate easier encounters
- **With high Trader reputation in plains** → May encounter friendly traders

### Managing Reputation

Use `/encounter reputation` to track faction standings:

- See which factions to avoid (hostile)
- Identify trading opportunities (friendly)
- Plan where to travel safely

### Debugging Encounters

If an encounter seems stuck or won't spawn:

1. Use `/encounter clear` to reset
2. Check `/encounter context` to verify your state
3. Try `/encounter generate` again

### Understanding Story Progression

Use `/encounter threads` and `/encounter history` to:

- See which story arcs are active
- Review what you've already done
- Understand how encounters connect

## Tips

1. **Check context before generating** - Use `/encounter context` to see what kind of encounter you're likely to get

2. **Monitor reputation** - Regularly check `/encounter reputation` to track faction relationships

3. **Don't spam generate** - Give each encounter time to play out before generating another

4. **Clear stuck encounters** - If entities don't spawn or the encounter seems broken, use `/encounter clear`

5. **Review history** - Use `/encounter history` to see patterns in your encounters and story progression

## Technical Notes

### Context Building

The context shown by `/encounter context` is built using the same `ContextEnrichmentEngine` that powers actual encounter generation. This means:

- Context is computed in real-time
- Shows exactly what the AI would see
- Updates immediately when you move or change equipment

### Reputation Changes

Reputation is affected by:
- Encounter outcomes (victory, defeat, flee, negotiation)
- Dialogue choices during encounters
- Story thread progression
- Faction-specific events

### Story Threads

Story threads are:
- Created by AI during encounters
- Progress based on related encounters
- Can branch into sub-threads
- May require specific conditions to advance
