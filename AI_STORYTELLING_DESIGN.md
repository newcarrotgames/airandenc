# AI-Driven Dynamic Storytelling System
## Design Document v2.0 - RLCraft Dregora Edition

---

## Executive Summary

This design presents a **dynamic AI-driven narrative engine** where encounters are generated on-the-fly based on player state, history, and ongoing story threads. The AI acts as a persistent storyteller, creating emergent narratives that respond to player choices and evolve over time.

**Setting:** RLCraft Dregora - A post-apocalyptic world where Earth was devastated by "The Blight" and colonists from the Dregorian Voyager returned centuries later to rebuild civilization among ruins, mutants, and eldritch horrors.

### Core Principles

1. **AI-First Architecture**: All encounters generated dynamically by AI, no static JSON files
2. **Persistent Story State**: Each player has a narrative history that influences future encounters
3. **Context-Rich Generation**: AI receives comprehensive player state, world context, and story threads
4. **Emergent Storytelling**: Story arcs emerge from player actions, not pre-scripted content
5. **Memory & Continuity**: AI maintains narrative memory across sessions and encounters
6. **Lore Consistency**: All encounters respect RLCraft Dregora's established world lore and themes

---

## Table of Contents

1. [RLCraft Dregora World Setting](#rlcraft-dregora-world-setting)
2. [Architecture Overview](#architecture-overview)
3. [Story State System](#story-state-system)
4. [AI Storytelling Engine](#ai-storytelling-engine)
5. [Context Gathering & Enrichment](#context-gathering--enrichment)
6. [Encounter Generation Flow](#encounter-generation-flow)
7. [Narrative Continuity System](#narrative-continuity-system)
8. [Story Thread Management](#story-thread-management)
9. [Dregora-Specific Integration](#dregora-specific-integration)
10. [Implementation Plan](#implementation-plan)
11. [Leveraging Existing Code](#leveraging-existing-code)
12. [Configuration & Tuning](#configuration--tuning)

---

## RLCraft Dregora World Setting

> **Note:** For comprehensive lore details, see [DREGORA_LORE_GUIDE.md](DREGORA_LORE_GUIDE.md)

### Quick Lore Summary

**The Past:**
- Year 2116: Earth's oil ran out, causing civilization collapse
- Nuclear disasters and mutant hordes pushed humanity to the brink
- The Dregorian Voyager, a colonization ship, launched to find new worlds
- The Blight struck Earth, turning it into a nuclear wasteland
- The ship's hyperdrive malfunctioned, passengers slept for centuries

**The Present:**
- The Dregorian Voyager returned to find Earth transformed
- Players are descendants of these returning colonists
- The world is now **Dregora** - ruins, mutants, and struggling survivors
- 800+ biomes ranging from corrupted jungles to nuclear wastelands
- Underground cities, medieval settlements, and futuristic ruins coexist
- Eldritch horrors and primal forces shape the landscape

### Key Themes for AI Encounters

1. **Post-Apocalyptic Survival** - Resources are scarce, danger is constant
2. **Lost Civilization** - Ruins hold secrets of the old world
3. **Nature's Revenge** - Mutated flora and fauna reclaim the land
4. **Corruption vs. Purity** - The Blight's influence lingers
5. **Hope in Darkness** - Small victories matter in a harsh world
6. **Eldritch Mystery** - Ancient forces beyond understanding

### Major Factions

- **Dregorian Colonists** - Tech-savvy returnees rebuilding civilization
- **Wasteland Nomads** - Survivor traders and wanderers
- **Kingdom of Daeroc** - One of the major surviving kingdoms
- **Scavenger Guilds** - Ruin explorers and looters
- **Eldritch Cults** - Worshippers of primal forces
- **Nature's Guardians** - Druids protecting sacred groves
- **The Corrupted** - Mutants and Blight-touched beings
- **Underground Cities** - Subterranean civilizations in The Underneath
- **Merchant League** - Gem traders and herbalists

### Important Locations

- **The City of Origins** - Major Dregorian settlement
- **Kingdom of Daeroc** - Established authority
- **Nuclear Ruins** - Dangerous old-world cities
- **The Underneath** - Dimension filled with parasites and ancient cities
- **Brutal Towers** - End-game challenges with powerful bosses
- **Libraries** - Scattered repositories of old world knowledge

---

## Architecture Overview

### High-Level Design

```
┌─────────────────────────────────────────────────────────────┐
│                    ENCOUNTER TRIGGER                         │
│  (Player Tick Event / Command / Story Thread Callback)      │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                 STORY STATE MANAGER                          │
│  • Load Player Story State                                   │
│  • Retrieve Active Story Threads                             │
│  • Get Encounter History Summary                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│               CONTEXT ENRICHMENT ENGINE                      │
│  • Environmental Context (biome, time, weather, location)    │
│  • Player State (level, health, inventory, equipment)        │
│  • Social Context (nearby players, factions, reputation)     │
│  • Historical Context (past encounters, choices, outcomes)   │
│  • Narrative Context (active threads, unresolved conflicts)  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│               AI STORYTELLING ENGINE                         │
│  • Construct Narrative Prompt                                │
│  • Call AI Service (OpenAI/Anthropic/Local)                  │
│  • Parse AI Response (Encounter JSON + Story Updates)        │
│  • Validate & Sanitize Generated Content                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              ENCOUNTER EXECUTION ENGINE                      │
│  • Execute Generated Encounter (via JsonEncounter)           │
│  • Track Encounter Outcomes                                  │
│  • Capture Player Choices & Actions                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              STORY STATE UPDATE                              │
│  • Record Encounter Summary                                  │
│  • Update Story Thread Progress                              │
│  • Create New Threads from Outcomes                          │
│  • Update Faction Reputation / Relationships                 │
│  • Persist State to Disk                                     │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

| Component | Purpose | File Location |
|-----------|---------|---------------|
| **StoryStateManager** | Persistent story state per player | `ai/torchlite/randomencounters/story/StoryStateManager.java` |
| **StoryThread** | Individual narrative arc tracking | `ai/torchlite/randomencounters/story/StoryThread.java` |
| **EncounterHistorySummarizer** | Compress history for AI context | `ai/torchlite/randomencounters/story/EncounterHistorySummarizer.java` |
| **ContextEnrichmentEngine** | Gather comprehensive context | `ai/torchlite/randomencounters/context/ContextEnrichmentEngine.java` |
| **NarrativePromptBuilder** | Construct storytelling prompts | `ai/torchlite/randomencounters/ai/NarrativePromptBuilder.java` |
| **AIStorytellingEngine** | Enhanced AI generator with story awareness | `ai/torchlite/randomencounters/ai/AIStorytellingEngine.java` |
| **EncounterOutcomeTracker** | Capture outcomes and choices | `ai/torchlite/randomencounters/story/EncounterOutcomeTracker.java` |

---

## Story State System

### Player Story State

Each player has a persistent story state stored as JSON in `world/data/randomencounters/players/<uuid>.json`

```java
public class PlayerStoryState {
    // Identity
    private UUID playerUUID;
    private String playerName;
    private long firstSeenTimestamp;
    private long lastSeenTimestamp;

    // Encounter History (compressed summaries)
    private List<EncounterSummary> encounterHistory; // Last 100 encounters
    private String narrativeSummary; // AI-generated summary of player's journey

    // Active Story Threads
    private Map<String, StoryThread> activeThreads; // threadId -> StoryThread

    // Relationships & Reputation
    private Map<String, Integer> factionReputation; // faction -> score (-100 to 100)
    private Map<String, RelationshipState> npcRelationships; // npcId -> state

    // Player Characteristics (AI-inferred)
    private Map<String, String> playerTraits; // "playstyle" -> "aggressive", "alignment" -> "chaotic_good"

    // World Impact
    private List<WorldEvent> causedEvents; // Major events caused by player
    private Map<String, Integer> itemsCollected; // Track notable items for context

    // Story Preferences (learned from player behavior)
    private Map<String, Float> encounterTypePreferences; // "combat" -> 0.8, "social" -> 0.3
}
```

### Encounter Summary

```java
public class EncounterSummary {
    private String encounterId;
    private long timestamp;
    private String encounterType; // "combat", "social", "mystery", "trade"
    private String briefDescription; // 1-2 sentences
    private String outcome; // "victory", "fled", "negotiated", "failed"
    private List<String> keyEntities; // Notable NPCs or entities
    private List<String> keyChoices; // Important decisions made
    private Map<String, String> storyImpact; // threadId -> impact description
}
```

### Story Thread

```java
public class StoryThread {
    private String threadId; // "thread_bandit_leader_revenge"
    private String title; // "The Bandit Leader's Revenge"
    private String description; // Brief summary of the arc

    // Thread State
    private ThreadStatus status; // ACTIVE, DORMANT, RESOLVED, FAILED
    private int progressLevel; // 0-10, how far along this arc is
    private long createdTimestamp;
    private long lastUpdateTimestamp;

    // Narrative Elements
    private List<String> keyNPCs; // Characters involved in this thread
    private List<String> keyLocations; // Important places
    private String currentObjective; // What's happening next
    private Map<String, String> threadState; // Arbitrary key-value state

    // Scheduling
    private ThreadPriority priority; // LOW, MEDIUM, HIGH, URGENT
    private Integer minEncountersUntilNext; // Cooldown before next encounter
    private String triggerCondition; // Optional condition: "near_village", "night", etc.

    // AI Context
    private String narrativeContext; // What the AI needs to know about this thread
}
```

---

## AI Storytelling Engine

### Enhanced AI Service Interface

Extends existing `IAIEncounterService` with story-aware methods:

```java
public interface IAIStorytellingService extends IAIEncounterService {
    /**
     * Generate an encounter with full story context
     */
    CompletableFuture<StorytellingResponse> generateStoryEncounter(
        StorytellingRequest request
    );

    /**
     * Summarize encounter history for compact context
     */
    CompletableFuture<String> summarizeHistory(
        List<EncounterSummary> history
    );

    /**
     * Infer player traits from behavior patterns
     */
    CompletableFuture<Map<String, String>> inferPlayerTraits(
        PlayerStoryState state
    );
}
```

### Storytelling Request

```java
public class StorytellingRequest extends AIEncounterRequest {
    // Existing context fields (biome, time, weather, player state)
    // ... inherited from AIEncounterRequest

    // NEW: Story Context
    private String narrativeSummary; // Compressed player journey
    private List<EncounterSummary> recentEncounters; // Last 5-10 encounters
    private List<StoryThread> activeThreads; // Current story arcs
    private Map<String, Integer> factionReputation;
    private Map<String, String> playerTraits;

    // NEW: Inventory & Equipment Context
    private List<ItemStack> notableItems; // Unique/rare items
    private Map<String, ItemStack> equipment; // Worn equipment

    // NEW: Social Context
    private List<String> nearbyPlayerNames;
    private String currentLocation; // Named location if near structure

    // NEW: Generation Guidance
    private String narrativeTone; // "epic", "horror", "whimsical", "grimdark"
    private Float desiredDifficulty; // 0.0-1.0
    private EncounterType preferredType; // null = surprise me
}
```

### Storytelling Response

```java
public class StorytellingResponse {
    // The generated encounter (standard JSON format)
    private String encounterJson;

    // Story Updates
    private List<StoryThreadUpdate> threadUpdates; // Updates to existing threads
    private List<StoryThread> newThreads; // New threads spawned

    // Narrative Metadata
    private String encounterSummary; // AI-generated summary for history
    private List<String> keyChoices; // Expected player decisions
    private Map<String, String> expectedOutcomes; // Predicted consequences

    // AI Reasoning (for debugging)
    private String narrativeRationale; // Why the AI chose this encounter
}
```

---

## Context Gathering & Enrichment

### Context Enrichment Engine

Builds on existing `AIEncounterRequest.buildRequest()` but adds rich context:

```java
public class ContextEnrichmentEngine {

    public StorytellingRequest buildEnrichedContext(
        EntityPlayer player,
        World world,
        PlayerStoryState storyState
    ) {
        StorytellingRequest request = new StorytellingRequest();

        // 1. ENVIRONMENTAL CONTEXT (existing)
        enrichEnvironmentalContext(request, player, world);

        // 2. PLAYER STATE CONTEXT (enhanced)
        enrichPlayerStateContext(request, player);

        // 3. INVENTORY & EQUIPMENT (NEW)
        enrichInventoryContext(request, player);

        // 4. SOCIAL CONTEXT (NEW)
        enrichSocialContext(request, player, world);

        // 5. LOCATION CONTEXT (NEW)
        enrichLocationContext(request, player, world);

        // 6. STORY CONTEXT (NEW)
        enrichStoryContext(request, storyState);

        return request;
    }

    private void enrichInventoryContext(StorytellingRequest request, EntityPlayer player) {
        // Notable items: enchanted gear, rare materials, quest items
        List<ItemStack> notable = new ArrayList<>();
        for (ItemStack stack : player.inventory.mainInventory) {
            if (isNotable(stack)) {
                notable.add(stack);
            }
        }
        request.setNotableItems(notable);

        // Equipment snapshot
        Map<String, ItemStack> equipment = new HashMap<>();
        equipment.put("mainhand", player.getHeldItemMainhand());
        equipment.put("offhand", player.getHeldItemOffhand());
        equipment.put("helmet", player.inventory.armorInventory.get(3));
        equipment.put("chest", player.inventory.armorInventory.get(2));
        equipment.put("legs", player.inventory.armorInventory.get(1));
        equipment.put("boots", player.inventory.armorInventory.get(0));
        request.setEquipment(equipment);
    }

    private void enrichSocialContext(StorytellingRequest request, EntityPlayer player, World world) {
        // Nearby players
        List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(
            EntityPlayer.class,
            player.getEntityBoundingBox().grow(100)
        );
        List<String> names = nearbyPlayers.stream()
            .filter(p -> p != player)
            .map(Entity::getName)
            .collect(Collectors.toList());
        request.setNearbyPlayerNames(names);
    }

    private void enrichLocationContext(StorytellingRequest request, EntityPlayer player, World world) {
        // Detect if near village, mansion, temple, etc.
        BlockPos pos = player.getPosition();
        // Check for nearby structures (requires structure tracking)
        String location = detectNamedLocation(world, pos);
        request.setCurrentLocation(location);
    }

    private void enrichStoryContext(StorytellingRequest request, PlayerStoryState state) {
        request.setNarrativeSummary(state.getNarrativeSummary());
        request.setRecentEncounters(state.getRecentEncounters(10));

        // Only include HIGH and URGENT priority threads in context
        List<StoryThread> relevantThreads = state.getActiveThreads().values().stream()
            .filter(t -> t.getPriority() == ThreadPriority.HIGH ||
                         t.getPriority() == ThreadPriority.URGENT)
            .collect(Collectors.toList());
        request.setActiveThreads(relevantThreads);

        request.setFactionReputation(state.getFactionReputation());
        request.setPlayerTraits(state.getPlayerTraits());
    }
}
```

---

## Encounter Generation Flow

### Step-by-Step Flow

#### 1. Trigger Event
- **Sources**: Player tick (automatic), command (`/encounter`), story thread callback
- **Action**: Check cooldowns, limits, conditions

#### 2. Load Story State
```java
PlayerStoryState state = StoryStateManager.getOrCreateState(player.getUniqueID());
```

#### 3. Build Enriched Context
```java
StorytellingRequest request = ContextEnrichmentEngine.buildEnrichedContext(
    player, world, state
);
```

#### 4. Determine Encounter Driver
- **Story-Driven**: If URGENT thread exists, prioritize it
- **Player-Driven**: If player near specific location/condition
- **Random Emergent**: AI creates new narrative hook

```java
StoryThread urgentThread = state.getUrgentThread();
if (urgentThread != null) {
    request.setNarrativeFocus(urgentThread);
}
```

#### 5. Generate Encounter via AI
```java
CompletableFuture<StorytellingResponse> future =
    AIStorytellingEngine.generateStoryEncounter(request);

future.thenAcceptAsync(response -> {
    // Execute encounter
    // Update story state
}, MinecraftServer::addScheduledTask);
```

#### 6. Validate Generated Content
```java
// Use existing EncounterValidator
EncounterConfig.Encounter validated = EncounterValidator.validate(
    response.getEncounterJson()
);
```

#### 7. Execute Encounter
```java
// Use existing JsonEncounter execution
JsonEncounter encounter = new JsonEncounter(validated);
encounter.execute(world, player, pos);
```

#### 8. Track Outcome
```java
// Monitor encounter events (kills, dialogues, item pickups)
EncounterOutcomeTracker tracker = new EncounterOutcomeTracker(encounter);
tracker.startTracking(world, player);
```

#### 9. Update Story State
```java
// After encounter concludes
EncounterSummary summary = tracker.generateSummary();
state.addEncounterSummary(summary);

// Apply thread updates from AI response
for (StoryThreadUpdate update : response.getThreadUpdates()) {
    state.updateThread(update);
}

// Add new threads
for (StoryThread newThread : response.getNewThreads()) {
    state.addThread(newThread);
}

StoryStateManager.saveState(state);
```

---

## Narrative Continuity System

### Prompt Structure for Continuity

The AI prompt must include structured story context to maintain continuity:

```
=== NARRATIVE CONTEXT ===

PLAYER JOURNEY SUMMARY:
{narrativeSummary}

RECENT ENCOUNTERS:
1. {encounter1.briefDescription} - Outcome: {encounter1.outcome}
2. {encounter2.briefDescription} - Outcome: {encounter2.outcome}
...

ACTIVE STORY THREADS:
1. [{thread1.title}] Status: {thread1.status}, Progress: {thread1.progressLevel}/10
   Objective: {thread1.currentObjective}
   Context: {thread1.narrativeContext}

2. [{thread2.title}] Status: {thread2.status}, Progress: {thread2.progressLevel}/10
   Objective: {thread2.currentObjective}
   Context: {thread2.narrativeContext}

FACTION REPUTATION:
- {faction1}: {reputation1} ({getReputationLabel(reputation1)})
- {faction2}: {reputation2} ({getReputationLabel(reputation2)})

PLAYER TRAITS:
- Playstyle: {playerTraits.playstyle}
- Alignment: {playerTraits.alignment}
- Preferred Approach: {playerTraits.preferredApproach}

=== CURRENT SITUATION ===

Location: {biome} biome, {distanceFromSpawn}m from spawn
Time: {timeOfDay}, Weather: {weather}
Player State: Level {playerLevel}, Health {playerHealth}%
Notable Items: {notableItems}
Nearby Players: {nearbyPlayerNames}

=== YOUR TASK ===

Generate an encounter that:
1. Feels like a natural continuation of the player's journey
2. References or advances at least one active story thread (if applicable)
3. Respects the player's established relationships and reputation
4. Matches the current environmental and social context
5. Provides meaningful choices with lasting consequences

Return your response as JSON with two sections:

{
  "encounter": { /* Standard encounter JSON */ },
  "story_updates": {
    "encounter_summary": "Brief description for history",
    "thread_updates": [
      {
        "thread_id": "thread_id_here",
        "progress_change": +1,
        "narrative_update": "What happened to advance this thread"
      }
    ],
    "new_threads": [ /* New story threads created */ ],
    "key_choices": ["Choice 1", "Choice 2"]
  }
}
```

### Narrative Prompt Builder

```java
public class NarrativePromptBuilder {

    public String buildStorytellingPrompt(StorytellingRequest request) {
        StringBuilder prompt = new StringBuilder();

        // System message for AI personality
        prompt.append(getSystemMessage());

        // Narrative context section
        prompt.append("\n=== NARRATIVE CONTEXT ===\n\n");
        prompt.append("PLAYER JOURNEY SUMMARY:\n");
        prompt.append(request.getNarrativeSummary()).append("\n\n");

        // Recent encounters
        prompt.append("RECENT ENCOUNTERS:\n");
        int count = 1;
        for (EncounterSummary enc : request.getRecentEncounters()) {
            prompt.append(String.format("%d. %s - Outcome: %s\n",
                count++, enc.getBriefDescription(), enc.getOutcome()));
        }
        prompt.append("\n");

        // Active story threads
        if (!request.getActiveThreads().isEmpty()) {
            prompt.append("ACTIVE STORY THREADS:\n");
            for (StoryThread thread : request.getActiveThreads()) {
                prompt.append(formatThread(thread));
            }
            prompt.append("\n");
        }

        // Faction reputation
        if (!request.getFactionReputation().isEmpty()) {
            prompt.append("FACTION REPUTATION:\n");
            for (Map.Entry<String, Integer> entry : request.getFactionReputation().entrySet()) {
                String label = getReputationLabel(entry.getValue());
                prompt.append(String.format("- %s: %d (%s)\n",
                    entry.getKey(), entry.getValue(), label));
            }
            prompt.append("\n");
        }

        // Player traits
        if (!request.getPlayerTraits().isEmpty()) {
            prompt.append("PLAYER TRAITS:\n");
            for (Map.Entry<String, String> trait : request.getPlayerTraits().entrySet()) {
                prompt.append(String.format("- %s: %s\n",
                    trait.getKey(), trait.getValue()));
            }
            prompt.append("\n");
        }

        // Current situation
        prompt.append("=== CURRENT SITUATION ===\n\n");
        prompt.append(formatCurrentSituation(request));

        // Task instructions
        prompt.append("\n=== YOUR TASK ===\n\n");
        prompt.append(getTaskInstructions());

        // JSON format example
        prompt.append("\n\n");
        prompt.append(getJsonFormatExample());

        return prompt.toString();
    }

    private String getSystemMessage() {
        return "You are a creative dungeon master for a Minecraft adventure. " +
               "Generate dynamic encounters that create an emergent narrative. " +
               "Each encounter should feel like a natural part of an ongoing story, " +
               "referencing past events and setting up future possibilities. " +
               "Be creative but stay grounded in the Minecraft world.";
    }

    private String getReputationLabel(int rep) {
        if (rep >= 75) return "Revered";
        if (rep >= 50) return "Honored";
        if (rep >= 25) return "Friendly";
        if (rep >= -25) return "Neutral";
        if (rep >= -50) return "Unfriendly";
        if (rep >= -75) return "Hostile";
        return "Hated";
    }
}
```

---

## Story Thread Management

### Thread Lifecycle

```
CREATED → ACTIVE → DORMANT ⇄ ACTIVE → RESOLVED
                      ↓
                   FAILED
```

### Thread Creation

Threads are created by:
1. **AI Response**: AI generates new thread in `story_updates.new_threads`
2. **Player Action**: Major events trigger thread creation (e.g., killing a faction leader)
3. **World Event**: Random world changes create threads (e.g., village under attack)

### Thread Progression

```java
public class StoryThreadManager {

    public void updateThread(StoryThread thread, StoryThreadUpdate update) {
        // Apply progress change
        thread.setProgressLevel(
            Math.max(0, Math.min(10, thread.getProgressLevel() + update.getProgressChange()))
        );

        // Update narrative context
        if (update.getNarrativeUpdate() != null) {
            thread.appendNarrativeContext(update.getNarrativeUpdate());
        }

        // Check for resolution
        if (thread.getProgressLevel() >= 10) {
            thread.setStatus(ThreadStatus.RESOLVED);
        }

        // Update priority based on progress and time
        updateThreadPriority(thread);

        thread.setLastUpdateTimestamp(System.currentTimeMillis());
    }

    private void updateThreadPriority(StoryThread thread) {
        // Threads at high progress become URGENT
        if (thread.getProgressLevel() >= 8) {
            thread.setPriority(ThreadPriority.URGENT);
        } else if (thread.getProgressLevel() >= 5) {
            thread.setPriority(ThreadPriority.HIGH);
        }

        // Old threads without progress become DORMANT
        long daysSinceUpdate = (System.currentTimeMillis() - thread.getLastUpdateTimestamp()) / (1000 * 60 * 60 * 24);
        if (daysSinceUpdate > 7 && thread.getProgressLevel() < 3) {
            thread.setStatus(ThreadStatus.DORMANT);
        }
    }

    public StoryThread selectThreadForEncounter(PlayerStoryState state) {
        // Priority: URGENT > HIGH > MEDIUM > LOW
        // Within same priority, prefer threads not recently updated

        List<StoryThread> candidates = state.getActiveThreads().values().stream()
            .filter(t -> t.getStatus() == ThreadStatus.ACTIVE)
            .filter(t -> meetsEncounterCooldown(t))
            .sorted(Comparator
                .comparing(StoryThread::getPriority)
                .thenComparing(StoryThread::getLastUpdateTimestamp))
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return null; // Generate new emergent encounter
        }

        // Weighted random selection favoring high priority
        return weightedRandomSelect(candidates);
    }
}
```

### Thread Examples

**Example 1: Bandit Revenge Arc**
```json
{
  "threadId": "thread_bandit_revenge_001",
  "title": "The Bandit Leader's Revenge",
  "description": "After defeating a bandit patrol, their leader swore vengeance.",
  "status": "ACTIVE",
  "progressLevel": 3,
  "createdTimestamp": 1703123456789,
  "lastUpdateTimestamp": 1703234567890,
  "keyNPCs": ["Gorm the Scarred", "Bandit Lieutenant Vex"],
  "keyLocations": ["Darkwood Forest", "Abandoned Mine"],
  "currentObjective": "The bandits are regrouping and planning an ambush",
  "threadState": {
    "bandits_killed": "5",
    "leader_encountered": "false",
    "player_stance": "aggressive"
  },
  "priority": "HIGH",
  "minEncountersUntilNext": 2,
  "triggerCondition": null,
  "narrativeContext": "After you defeated a bandit patrol near Darkwood Forest, the surviving lieutenant, Vex, fled. She reported back to Gorm the Scarred, a notorious bandit leader. Gorm took your actions personally and has put a bounty on your head."
}
```

**Example 2: Mysterious Artifact**
```json
{
  "threadId": "thread_artifact_mystery_042",
  "title": "The Whispering Amulet",
  "description": "A strange amulet found in ruins speaks to you in dreams.",
  "status": "ACTIVE",
  "progressLevel": 5,
  "createdTimestamp": 1702123456789,
  "lastUpdateTimestamp": 1703234567890,
  "keyNPCs": ["The Wandering Scholar", "Voice in the Amulet"],
  "keyLocations": ["Ancient Ruins", "Underground Temple"],
  "currentObjective": "The amulet whispers of a temple beneath the desert",
  "threadState": {
    "amulet_owned": "true",
    "voice_trust": "medium",
    "clues_found": "3"
  },
  "priority": "MEDIUM",
  "minEncountersUntilNext": null,
  "triggerCondition": "desert_biome",
  "narrativeContext": "You found a strange amulet in ancient ruins. It whispers to you, revealing fragments of a forgotten civilization. A scholar you met believes it's connected to an underground temple, but warned that the voice may not be trustworthy."
}
```

---

## Dregora-Specific Integration

### Lore-Aware Prompt System

The AI must be primed with Dregora lore to generate authentic encounters. The prompt system includes:

#### System Message Enhancement

```
You are a storyteller for RLCraft Dregora, a post-apocalyptic Minecraft world.

WORLD SETTING:
Earth was devastated by "The Blight" in the year ~2120, following an energy crisis
and nuclear catastrophe. Centuries later, the Dregorian Voyager - a colonization ship
whose passengers were in stasis - returned to find Earth transformed into Dregora.

Players are descendants of these returning colonists, struggling to survive in a world
of ruins, mutants, corrupted nature, and eldritch horrors. The world blends medieval
settlements, nuclear wastelands, and futuristic remnants.

TONE: Grimdark fantasy with hope. Survival is hard, death is common, but moments of
humanity and small victories matter. Mix post-apocalyptic survival, Lovecraftian
mystery, and dark fairy tale elements.

KEY FACTIONS:
- Dregorian Colonists (player's heritage) - tech-savvy, rebuilding
- Wasteland Nomads - traders, neutral, pragmatic
- Kingdom of Daeroc - established authority
- Scavenger Guilds - ruin explorers
- Eldritch Cults - worshippers of dark forces
- Nature's Guardians - druids, herbalists
- The Corrupted - mutants, Blight-touched
- Underground Cities - subterranean civilizations
- Merchant League - gem traders, herbalists

IMPORTANT LOCATIONS:
- The City of Origins (Dregorian settlement)
- Kingdom of Daeroc (major kingdom)
- Nuclear Ruins (dangerous old-world cities)
- The Underneath (dimension of parasites and underground cities)
- Brutal Towers (end-game boss challenges)
- Libraries (repositories of old world knowledge)

Generate encounters that feel native to this world. Reference the Blight, the
Dregorian heritage, and the struggle for survival. Use appropriate entity types
(mutants, corrupted wildlife, bandits, undead, parasites, constructs).
```

### Biome-Specific Encounter Templates

The AI should receive biome context and generate appropriate encounters:

| Biome Type | Common Encounters | Themes |
|------------|-------------------|--------|
| **Ancient Rainforest** | Nature spirits, giant insects, hidden temples | Wonder, ancient secrets |
| **Corrupted Dense Jungle** | Blight-touched animals, cultists, twisted flora | Corruption, madness |
| **Ravenous Jungle** | Carnivorous plants, desperate herbalists, trapped explorers | Nature's revenge, survival |
| **Nuclear Ruins** | Mutants, scavengers, malfunctioning robots, radiation zones | Old world tech, danger |
| **Dead Wastelands** | Desperate survivors, bandits, wasteland nomads | Harsh survival, desperation |
| **Mystic Grove** | Friendly druids, magical creatures, guardian spirits | Magic, protection, beauty |
| **Underground (The Underneath)** | Parasites, lost miners, ancient city dwellers | Body horror, claustrophobia |
| **Lavender Fields** | Peaceful herbalists, refugees seeking safety, rare respite | Hope, peace (rare) |
| **Salt Plains** | Salt traders, mirages, dried remnants of sea life | Desolation, strange beauty |

### Faction Reputation Integration

Dregora-specific faction reputation system:

```java
// Initial faction reputation for new players
Map<String, Integer> defaultReputation = new HashMap<>();
defaultReputation.put("Dregorian Colonists", 50);      // Your people - Friendly
defaultReputation.put("Wasteland Nomads", 0);          // Neutral
defaultReputation.put("Kingdom of Daeroc", 0);         // Neutral
defaultReputation.put("Scavenger Guilds", 0);          // Neutral
defaultReputation.put("Eldritch Cults", -25);          // Unfriendly
defaultReputation.put("Nature's Guardians", 10);       // Slightly positive
defaultReputation.put("The Corrupted", -75);           // Hostile
defaultReputation.put("Underground Cities", -10);      // Suspicious
defaultReputation.put("Merchant League", 25);          // Friendly (traders)
```

### Story Thread Examples - Dregora Edition

**Thread 1: "Echoes of the Voyager"**
```json
{
  "threadId": "thread_voyager_echoes_001",
  "title": "Echoes of the Voyager",
  "description": "Strange signals detected - another cryo-pod from the Dregorian Voyager",
  "narrativeContext": "While exploring old ruins, you picked up a distress beacon from another Dregorian cryo-pod. Someone else survived the long sleep. But their worldview may be centuries out of date, and they may not accept what Earth has become."
}
```

**Thread 2: "The Spreading Blight"**
```json
{
  "threadId": "thread_blight_spread_042",
  "title": "The Spreading Blight",
  "description": "Green corruption is advancing through a forest region",
  "narrativeContext": "The Blight isn't just history - it's still active. A Nature's Guardian druid warned you that corruption is spreading through the Mystic Grove. If it isn't stopped, the entire region will fall to the 'green of corruption' mentioned in old world texts."
}
```

**Thread 3: "Secrets of The Underneath"**
```json
{
  "threadId": "thread_underneath_mystery_013",
  "title": "Secrets of The Underneath",
  "description": "Dreams call you to the underground dimension",
  "narrativeContext": "You've been having vivid dreams of an underground city. A voice whispers coordinates and promises of 'truth beneath the surface.' A portal to The Underneath has been found nearby. The parasites that inhabit it are terrifying, but something wants you to descend."
}
```

**Thread 4: "The Last Library"**
```json
{
  "threadId": "thread_last_library_099",
  "title": "The Last Library",
  "description": "A complete archive of pre-Blight knowledge may exist",
  "narrativeContext": "An old scholar from the City of Origins mentioned legends of the 'Archive Eternal' - a library that survived the Blight intact. Multiple factions seek it: Dregorians want technology, Scavengers want treasure, Eldritch Cults want forbidden knowledge. The race is on."
}
```

**Thread 5: "Champion of the Brutal Tower"**
```json
{
  "threadId": "thread_brutal_tower_challenge_005",
  "title": "Champion of the Brutal Tower",
  "description": "Training to challenge an end-game tower boss",
  "narrativeContext": "The Brutal Towers are remnants of the old world's military might, now inhabited by corrupted guardians. Warriors speak of 'brutal coins' as the ultimate proof of strength. You've decided to train and challenge a tower, but it will require legendary equipment and allies."
}
```

### Entity Whitelisting - Dregora Additions

Extend the `EncounterValidator` whitelist with Dregora-appropriate entities:

```java
// Vanilla hostile (already included)
"minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:creeper"

// RLCraft additions (if available in 1.12.2 modpack)
"lycanitesmobs:geonach",        // Earth elemental
"lycanitesmobs:cinder",         // Fire creature
"lycanitesmobs:reaper",         // Death entity
"lycanitesmobs:banshee",        // Ghostly horror
"iceandfire:dread_knight",      // Corrupted warrior
"srparasites:primitive_host",   // Parasite infected
// ... add more based on what's actually in RLCraft Dregora

// Narrative entity types (for AI context)
// AI generates: "type": "mutant_survivor"
// System maps to: "minecraft:zombie" with custom name/equipment
```

**Entity Type Mapping:**
- **Mutants** → Zombies with custom names, equipment
- **Corrupted Wildlife** → Hostile animals (wolves, bears) with Blight-themed names
- **Wasteland Bandits** → Skeletons/zombies with weapons and armor
- **Parasites** → Silverfish or modded parasite mobs (if available)
- **Eldritch Horrors** → Endermen with custom names, or witches
- **Guardians** → Iron golems or witches with protective behavior
- **Traders** → Villagers with custom trades

### Location Context Enhancement

Add Dregora-specific location detection:

```java
public class DregoraLocationDetector {

    public String detectLocation(World world, BlockPos pos) {
        // Check for nearby structure types
        String structureType = detectStructureType(world, pos);

        if (structureType != null) {
            return getDregoraLocationName(structureType);
        }

        return null; // No special location
    }

    private String getDregoraLocationName(String structureType) {
        switch (structureType) {
            case "mansion": return "Abandoned Pre-Blight Estate";
            case "village": return "Survivor Settlement";
            case "stronghold": return "Old World Bunker";
            case "temple": return "Ancient Ritual Site";
            case "monument": return "Submerged Research Facility";
            case "fortress": return "Military Outpost Ruins";
            // Add more Dregora-specific structures
            default: return "Unknown Structure";
        }
    }
}
```

### AI Prompt - Complete Dregora Example

```
=== WORLD: RLCRAFT DREGORA ===

You are narrating an encounter in Dregora, a post-apocalyptic Earth where "The Blight"
devastated civilization centuries ago. Players are descendants of the Dregorian Voyager
colonists who returned from space to find Earth transformed.

=== PLAYER BACKGROUND ===

Name: Steve
Heritage: Dregorian Colonist descendant
Journey Summary: Steve helped a group of wasteland nomads survive a mutant attack.
He gained respect among traders but earned the enmity of a scavenger gang who wanted
to loot the nomads. He's been exploring nuclear ruins seeking old world technology.

=== ACTIVE STORY THREADS ===

1. [The Scavenger's Revenge] Progress: 4/10, Priority: HIGH
   Objective: The scavenger gang leader, "Rust", swore vengeance after Steve
   interfered with their raid.
   Context: Rust is gathering allies in the wastelands. An ambush is coming.

=== CURRENT SITUATION ===

Location: Dead Wastelands biome, 2,500m from spawn
Time: Dusk, heavy rain (reduces visibility)
Player State: Level 15, Health 80%, wearing scavenged iron armor
Notable Items: Pre-Blight multitool, radiation medicine (3 doses)
Nearby: Abandoned gas station (rusted, partially collapsed)

Faction Reputation:
- Wasteland Nomads: +35 (Friendly)
- Scavenger Guilds: -40 (Hostile)
- Merchant League: +15 (Neutral-Positive)

=== YOUR TASK ===

Generate an encounter that:
1. Advances "The Scavenger's Revenge" thread (this is HIGH priority)
2. Uses the Dead Wastelands setting (harsh, desperate, dangerous)
3. Incorporates the rain and ruined gas station
4. Respects Steve's reputation (nomads like him, scavengers hate him)
5. Feels like Dregora (post-apocalyptic, survival-focused, grimdark with hope)

The encounter should be challenging but fair for a level 15 player in iron armor.
Include meaningful choices - will Steve fight, flee, negotiate, or find another way?

Return JSON with encounter definition and story_updates.
```

### Configuration - Dregora Mode

Add Dregora-specific config options:

```java
// Dregora Lore Integration
public static boolean enableDregoraLore = true;
public static boolean enforceFactionsOnlyDregora = true; // Use only Dregora factions
public static boolean allowCustomEntityNames = true; // "Mutant Scavenger" instead of "Zombie"
public static String worldSetting = "dregora"; // Options: "dregora", "generic", "custom"

// Narrative Preferences
public static String defaultTone = "grimdark_with_hope"; // Dregora's default tone
public static boolean emphasizeBlight = true; // Include Blight references
public static boolean emphasizeDregorianHeritage = true; // Reference player's colonist background
```

### Testing Dregora Integration

Create test scenarios specific to Dregora:

**Test 1: Nuclear Ruins Exploration**
- Biome: Nuclear Ruins
- Player: Low radiation medicine
- Expected: Encounter with mutants or radiation hazard, possibly a trader selling medicine

**Test 2: Mystic Grove Discovery**
- Biome: Mystic Grove
- Player: No prior interaction with Nature's Guardians
- Expected: Druid encounter, protection quest, or magical discovery

**Test 3: Scavenger Conflict**
- Location: Any wasteland
- Reputation: Negative with Scavenger Guilds
- Expected: Ambush or confrontation based on past actions

**Test 4: Dregorian Heritage Moment**
- Trigger: Finding pre-Blight technology
- Expected: Encounter that references player's connection to the Voyager colonists

**Test 5: The Underneath Portal**
- Location: Near underground portal
- Expected: Warnings about parasites, opportunity to explore dimension

---

## Implementation Plan

### Phase 1: Foundation (1-2 weeks)

**Goal**: Set up story state infrastructure

1. **Create Story State Classes**
   - `PlayerStoryState`
   - `EncounterSummary`
   - `StoryThread`
   - `StoryThreadUpdate`
   - Enums: `ThreadStatus`, `ThreadPriority`

2. **Implement StoryStateManager**
   - Load/save player state to JSON files
   - In-memory caching for active players
   - Periodic auto-save (every 5 minutes)
   - Graceful shutdown save

3. **Create Basic Thread Management**
   - `StoryThreadManager` class
   - Thread creation, update, resolution logic
   - Thread selection for encounters

**Files to Create:**
- `src/main/java/ai/torchlite/randomencounters/story/PlayerStoryState.java`
- `src/main/java/ai/torchlite/randomencounters/story/EncounterSummary.java`
- `src/main/java/ai/torchlite/randomencounters/story/StoryThread.java`
- `src/main/java/ai/torchlite/randomencounters/story/StoryThreadUpdate.java`
- `src/main/java/ai/torchlite/randomencounters/story/StoryStateManager.java`
- `src/main/java/ai/torchlite/randomencounters/story/StoryThreadManager.java`

**Testing:**
- Create dummy player states
- Verify JSON serialization/deserialization
- Test thread lifecycle transitions

---

### Phase 2: Context Enrichment (1 week)

**Goal**: Gather comprehensive context for AI

1. **Enhance Context Collection**
   - Create `ContextEnrichmentEngine`
   - Implement inventory scanning (notable items)
   - Implement social context (nearby players)
   - Implement location detection (structure proximity)

2. **Create StorytellingRequest**
   - Extend `AIEncounterRequest`
   - Add all new context fields
   - Serialize to JSON for AI prompt

**Files to Create:**
- `src/main/java/ai/torchlite/randomencounters/context/ContextEnrichmentEngine.java`
- `src/main/java/ai/torchlite/randomencounters/ai/StorytellingRequest.java`
- `src/main/java/ai/torchlite/randomencounters/ai/StorytellingResponse.java`

**Files to Modify:**
- `EncounterManager.java` - Use enriched context instead of basic

**Testing:**
- Print enriched context for test player
- Verify notable item detection works
- Check biome/location detection accuracy

---

### Phase 3: Narrative Prompt System (1 week)

**Goal**: Build sophisticated AI prompts with story context

1. **Create Prompt Builder**
   - `NarrativePromptBuilder` class
   - System message for AI personality
   - Structured context sections
   - Task instructions and JSON format

2. **Implement History Summarization**
   - `EncounterHistorySummarizer` class
   - Compress last 100 encounters into concise narrative
   - Option to call AI for summarization (expensive but good)
   - Option for rule-based summarization (cheap but basic)

**Files to Create:**
- `src/main/java/ai/torchlite/randomencounters/ai/NarrativePromptBuilder.java`
- `src/main/java/ai/torchlite/randomencounters/story/EncounterHistorySummarizer.java`

**Testing:**
- Generate prompts for various scenarios
- Verify prompt length is reasonable (not exceeding AI context limits)
- Test with different story states

---

### Phase 4: AI Storytelling Engine (2 weeks)

**Goal**: Integrate story-aware AI generation

1. **Create IAIStorytellingService Interface**
   - Extend `IAIEncounterService`
   - Add `generateStoryEncounter()` method
   - Add `summarizeHistory()` method

2. **Implement OpenAI Storytelling Service**
   - Extend `OpenAIEncounterService`
   - Use GPT-4 with storytelling prompts
   - Parse response into `StorytellingResponse`
   - Handle story_updates section

3. **Implement Anthropic Storytelling Service**
   - Extend `AnthropicEncounterService` (to be created)
   - Use Claude with storytelling prompts
   - Similar parsing as OpenAI

4. **Create AIStorytellingEngine**
   - Orchestrates story-aware generation
   - Handles service selection and fallback
   - Integrates with StoryStateManager

**Files to Create:**
- `src/main/java/ai/torchlite/randomencounters/ai/IAIStorytellingService.java`
- `src/main/java/ai/torchlite/randomencounters/ai/OpenAIStorytellingService.java`
- `src/main/java/ai/torchlite/randomencounters/ai/AnthropicStorytellingService.java`
- `src/main/java/ai/torchlite/randomencounters/ai/AIStorytellingEngine.java`

**Files to Modify:**
- `AIEncounterGenerator.java` - Delegate to AIStorytellingEngine

**Testing:**
- Generate encounters with mock story state
- Verify AI returns valid JSON
- Test story_updates parsing
- Check error handling and fallback

---

### Phase 5: Outcome Tracking (1 week)

**Goal**: Capture what actually happened in encounters

1. **Create Outcome Tracker**
   - `EncounterOutcomeTracker` class
   - Listen for entity events (death, damage, target change)
   - Listen for player events (item pickup, chat)
   - Capture player choices (if dialogue system exists)

2. **Generate Outcome Summaries**
   - Determine encounter outcome (victory, fled, negotiated, failed)
   - Extract key events
   - Create `EncounterSummary` object

**Files to Create:**
- `src/main/java/ai/torchlite/randomencounters/story/EncounterOutcomeTracker.java`

**Files to Modify:**
- `JsonEncounter.java` - Integrate outcome tracking
- `EncounterManager.java` - Save summaries after encounters

**Testing:**
- Run test encounters
- Verify outcomes are correctly detected
- Check summaries are accurate

---

### Phase 6: Integration & Flow (1 week)

**Goal**: Connect all pieces into complete flow

1. **Update Encounter Trigger Flow**
   - Modify `EncounterManager.triggerEncounter()`
   - Load story state
   - Build enriched context
   - Select story thread (if applicable)
   - Generate via AIStorytellingEngine
   - Execute encounter
   - Track outcome
   - Update story state

2. **Add Story Commands**
   - `/encounter story` - View player's story state
   - `/encounter threads` - List active story threads
   - `/encounter history` - View encounter history
   - `/encounter thread <id>` - View specific thread details

**Files to Modify:**
- `EncounterManager.java` - Complete flow integration
- `EncounterCommand.java` - Add story subcommands

**Testing:**
- End-to-end encounter flow
- Verify state persistence
- Test thread progression
- Validate continuity across encounters

---

### Phase 7: Polish & Tuning (1-2 weeks)

**Goal**: Optimize performance and tune storytelling

1. **Performance Optimization**
   - Profile context enrichment overhead
   - Optimize JSON serialization
   - Add caching where appropriate
   - Tune auto-save intervals

2. **Storytelling Tuning**
   - Adjust prompt templates based on results
   - Fine-tune thread priority logic
   - Balance emergent vs. thread-driven encounters
   - Add variety to AI personalities/tones

3. **Configuration Options**
   - Add config for story system enable/disable
   - Max active threads per player
   - Thread dormancy thresholds
   - Summarization strategies

**Files to Modify:**
- `ConfigHandler.java` - Add story system config
- `NarrativePromptBuilder.java` - Refine prompts
- All story classes - Performance tweaks

**Testing:**
- Long-term playtest (10+ hours)
- Multiple players concurrently
- Server restart recovery
- Memory usage profiling

---

### Phase 8: Advanced Features (Future)

**Optional enhancements for v3.0:**

1. **Multi-Player Story Arcs**
   - Threads that involve multiple players
   - Party-based encounters
   - Player vs. player narrative conflicts

2. **Faction System**
   - Complex reputation with multiple factions
   - Faction-driven story threads
   - Dynamic faction relationships (alliances, wars)

3. **World Events**
   - Server-wide story events
   - Scheduled narrative campaigns
   - Persistent world changes from player actions

4. **Dialogue System**
   - NPC conversations via chat interface
   - AI-generated dialogue responses
   - Branching conversation trees

5. **Quest System**
   - Formal quest tracking
   - Multi-stage objectives
   - Quest journal UI

---

## Leveraging Existing Code

### What to Keep

| Component | Status | Usage |
|-----------|--------|-------|
| **JsonEncounter** | ✅ Keep as-is | Perfect for executing AI-generated encounters |
| **EncounterValidator** | ✅ Keep as-is | Essential for AI safety |
| **EncounterConfig** | ✅ Keep as-is | Standard data format |
| **ActionExecutor** | ✅ Keep as-is | Handles all encounter actions |
| **EncounterContext** | ✅ Keep as-is | Runtime state tracking |
| **IAIEncounterService** | ✅ Extend | Good interface, add story methods |
| **OpenAIEncounterService** | ✅ Extend | Solid implementation, enhance prompts |
| **EncounterCache** | ✅ Modify | Adapt for story-based cache keys |

### What to Deprecate

| Component | Status | Reason |
|-----------|--------|--------|
| **Static JSON encounters** | ⚠️ Deprecate | Move to AI-only generation |
| **JsonEncounterLoader** | ⚠️ Deprecate | No longer need to load static encounters |
| **FallbackEncounterService** | ⚠️ Keep for emergencies | Only use if all AI services fail |

### What to Modify

| Component | Changes Needed |
|-----------|----------------|
| **EncounterManager** | Add story state loading, use enriched context, track outcomes |
| **AIEncounterGenerator** | Delegate to AIStorytellingEngine, remove simple history tracking |
| **AIEncounterRequest** | Extend to StorytellingRequest with story fields |
| **EncounterCommand** | Add story-related subcommands |
| **ConfigHandler** | Add story system configuration options |

---

## Configuration & Tuning

### Config Options

Add to `ConfigHandler.java`:

```java
// Story System
public static boolean enableStorySystem = true;
public static int maxActiveThreadsPerPlayer = 10;
public static int maxEncounterHistorySize = 100;
public static int threadDormancyDays = 7;
public static boolean enableAutomaticSummarization = false; // Expensive, uses AI
public static String summarizationStrategy = "rule_based"; // "rule_based" or "ai_generated"

// Story Generation
public static float emergentEncounterChance = 0.3f; // 30% chance for new thread vs continuing existing
public static boolean enableMultiPlayerStories = false; // Future feature
public static boolean enableFactionSystem = true;
public static int recentEncountersInContext = 10; // How many recent encounters to include

// Narrative Style
public static String defaultNarrativeTone = "epic"; // epic, horror, whimsical, grimdark
public static boolean allowAICreativity = true; // Allow AI to create new factions, NPCs
public static int maxNewThreadsPerEncounter = 2;

// Performance
public static int storyStateSaveIntervalSeconds = 300; // Auto-save every 5 minutes
public static boolean cacheEnrichedContext = true;
public static int contextCacheDurationSeconds = 60;
```

### Tuning Prompts

The quality of storytelling depends heavily on prompt engineering. Key areas to tune:

1. **System Message**: Sets AI personality and constraints
2. **Context Format**: How story information is presented
3. **Task Instructions**: What the AI should focus on
4. **Examples**: Provide few-shot examples of good encounters
5. **Constraints**: Enforce Minecraft lore, balance, safety

**Prompt Engineering Tips:**
- Be specific about JSON format expectations
- Provide examples of desired encounter complexity
- Set boundaries (e.g., "max 5 entities", "stay lore-friendly")
- Encourage referencing story context explicitly
- Request reasoning/rationale for debugging

---

## Migration Path

### From Current System to Story System

**Option 1: Hard Cutover (Recommended for development)**
1. Deploy new story system
2. All new encounters are AI-generated with story tracking
3. Existing players start with empty story state
4. Static JSON encounters disabled

**Option 2: Gradual Migration (Recommended for production)**
1. Deploy story system alongside static system
2. Config option: `storySystemMode = "hybrid"` / "story_only" / "static_only"
3. Hybrid mode: 50% AI story encounters, 50% static encounters
4. Over time, increase AI percentage as system stabilizes
5. Eventually flip to story_only

**Option 3: Opt-In (For cautious rollout)**
1. Story system disabled by default
2. Players opt-in via command: `/encounter story enable`
3. Allows testing with willing players
4. Gather feedback before making default

---

## Success Metrics

How to measure if the story system is working:

### Quantitative Metrics
- **Encounter Variety**: Unique encounter types generated
- **Thread Completion Rate**: % of threads that reach RESOLVED
- **Player Engagement**: Average encounters per player session
- **AI Success Rate**: % of AI calls that return valid encounters
- **Cache Hit Rate**: How often cached encounters are reused
- **Average Thread Lifespan**: How long threads stay active

### Qualitative Metrics
- **Narrative Coherence**: Do encounters feel connected?
- **Player Agency**: Do choices matter and carry forward?
- **Emergent Stories**: Are interesting stories emerging organically?
- **AI Creativity**: Is the AI generating surprising, creative encounters?
- **Lore Consistency**: Does it feel like Minecraft?

### Debugging Tools

Add admin commands for troubleshooting:

```
/encounter debug context [player] - Dump enriched context
/encounter debug prompt [player] - Show AI prompt that would be sent
/encounter debug state [player] - View full story state
/encounter debug thread <id> - Inspect specific thread
/encounter debug validate <json> - Test encounter JSON validation
/encounter debug simulate - Generate encounter without executing
```

---

## Risks & Mitigations

### Risk 1: AI Generated Invalid Content
**Mitigation**:
- Strict validation via `EncounterValidator`
- Whitelist approach for entities, items, actions
- Fallback to template-based generation
- Comprehensive testing suite

### Risk 2: Story State Corruption
**Mitigation**:
- Backup story states before saves
- Validation on load (reject corrupt states)
- Graceful degradation (start fresh if unrecoverable)
- Version story state format for migrations

### Risk 3: AI Costs Too High
**Mitigation**:
- Aggressive caching (context-based)
- Configurable encounter frequency
- Option to use cheaper models (GPT-3.5)
- Local AI option (Ollama)
- Per-player budget limits

### Risk 4: Poor Narrative Quality
**Mitigation**:
- Extensive prompt engineering
- Few-shot examples in prompts
- Feedback loop for bad generations
- Manual review mode for testing
- Player feedback commands

### Risk 5: Performance Issues
**Mitigation**:
- Async AI calls (non-blocking)
- Context enrichment optimization
- Efficient JSON serialization
- Lazy loading of story states
- Periodic cache cleanup

### Risk 6: Story Threads Forgotten
**Mitigation**:
- Thread priority system
- Dormancy detection and revival
- Periodic "thread health" checks
- AI can reference old threads
- Player can manually trigger threads

---

## Example: Complete Story Flow

### Scenario Walkthrough

**Initial State:**
- Player "Steve" joins server, fresh story state
- No encounter history, no story threads

**Encounter 1: The Lost Travelers**
1. **Trigger**: Player walks for 5 minutes in forest biome
2. **Context**: New player, no history, forest biome, day time
3. **AI Prompt**: "Generate an introductory encounter for a new adventurer..."
4. **AI Response**:
   - Encounter: "Lost Travelers" - Friendly NPCs asking for directions
   - Story Update: Creates thread "thread_lost_travelers_001"
5. **Execution**: Two villager NPCs spawn, broadcast message
6. **Outcome**: Player gives them food, they thank him
7. **State Update**:
   - History: "Helped lost travelers in forest"
   - Thread created: "The Grateful Travelers" (progress: 1/10, priority: LOW)
   - Faction: "Wandering Folk" +10 reputation

**Encounter 2: Bandit Ambush**
1. **Trigger**: 10 minutes later, player in plains
2. **Context**: Recent help of travelers, plains biome, dusk
3. **AI Prompt**: Includes history of helping travelers
4. **AI Response**:
   - Encounter: "Bandit Ambush" - Bandits attack
   - Story Update: Creates thread "thread_bandit_conflict_001", updates "thread_lost_travelers_001" (+1 progress: "Bandits were following the travelers")
5. **Execution**: 3 zombie bandits spawn with weapons
6. **Outcome**: Player kills 2, 1 flees
7. **State Update**:
   - History: "Defeated bandit ambush in plains, one escaped"
   - Thread created: "Bandit Revenge" (progress: 2/10, priority: MEDIUM)
   - Thread updated: "The Grateful Travelers" (progress: 2/10) - "Saved travelers from their pursuers"
   - Faction: "Bandit Clan" -25 reputation

**Encounter 3: Merchant Caravan**
1. **Trigger**: 15 minutes later, near village
2. **Context**: History of helping travelers and fighting bandits, positive reputation with Wandering Folk
3. **AI Prompt**: Includes both threads, faction reputation
4. **AI Response**:
   - Encounter: "Grateful Merchant" - Merchant offers discount
   - Story Update: Updates "thread_lost_travelers_001" (+2 progress: "Word of your kindness has spread")
5. **Execution**: Merchant NPC spawns, offers trade
6. **Outcome**: Player trades, merchant gives bonus item
7. **State Update**:
   - History: "Traded with grateful merchant near village"
   - Thread updated: "The Grateful Travelers" (progress: 4/10, priority: MEDIUM)
   - Faction: "Wandering Folk" +15 reputation (total: +25)

**Encounter 4: Bandit Leader**
1. **Trigger**: 20 minutes later, player in forest (near initial encounter location)
2. **Context**: "Bandit Revenge" thread at priority MEDIUM, player in good combat gear now
3. **AI Prompt**: Focuses on "Bandit Revenge" thread
4. **AI Response**:
   - Encounter: "Bandit Leader's Challenge" - Boss-level bandit seeks revenge
   - Story Update: Advances "thread_bandit_conflict_001" (+3 progress: "The leader wants revenge")
5. **Execution**: Powerful skeleton with enchanted gear spawns
6. **Outcome**: Player defeats leader after tough fight
7. **State Update**:
   - History: "Defeated the Bandit Leader in epic duel"
   - Thread updated: "Bandit Revenge" (progress: 8/10, priority: URGENT) - "Leader defeated, but bandits may retaliate"
   - Faction: "Bandit Clan" -50 reputation (total: -75, now HOSTILE)
   - Faction: "Wandering Folk" +20 reputation (total: +45, now FRIENDLY)

**Encounter 5: Bandit Clan Dissolved**
1. **Trigger**: 5 minutes later, player near bandit camp
2. **Context**: "Bandit Revenge" thread at URGENT priority (progress 8/10)
3. **AI Prompt**: Thread nearing resolution
4. **AI Response**:
   - Encounter: "Bandit Surrender" - Remaining bandits surrender
   - Story Update: Resolves "thread_bandit_conflict_001" (progress: 10/10, status: RESOLVED)
5. **Execution**: 2 unarmed bandits spawn, offer peace
6. **Outcome**: Player accepts surrender (or rejects and fights)
7. **State Update**:
   - History: "Bandits surrendered, conflict resolved"
   - Thread resolved: "Bandit Revenge" (status: RESOLVED)
   - New thread created: "Rebuilding the Region" (if player showed mercy)
   - Faction: "Bandit Clan" disbanded or becomes "Reformed Bandits" +10 reputation

**Result**: A 5-encounter story arc emerged naturally from AI decisions, creating a coherent narrative about helping travelers, fighting bandits, and ultimately resolving the conflict. Each encounter built on previous ones, referenced past events, and created lasting consequences.

---

## Conclusion

This system functions as a dynamic AI storyteller that creates emergent narratives unique to each player. By maintaining persistent story state, enriching context, and using sophisticated prompting, the AI can generate encounters that feel like part of an ongoing journey rather than random isolated events.

The system leverages the existing robust execution and validation infrastructure while adding the story layer on top. The implementation plan is modular and incremental, allowing for testing and refinement at each phase.

The ultimate goal: **Every player has their own unique story that unfolds dynamically, driven by their choices, shaped by consequences, and narrated by AI.**

---

## Appendix A: File Structure

```
src/main/java/ai/torchlite/randomencounters/
├── ai/
│   ├── AIEncounterGenerator.java (MODIFY - delegate to story engine)
│   ├── AIStorytellingEngine.java (NEW - orchestrates story-aware generation)
│   ├── IAIStorytellingService.java (NEW - extended service interface)
│   ├── OpenAIStorytellingService.java (NEW - GPT-4 with story context)
│   ├── AnthropicStorytellingService.java (NEW - Claude with story context)
│   ├── NarrativePromptBuilder.java (NEW - builds story prompts)
│   ├── StorytellingRequest.java (NEW - enriched context)
│   └── StorytellingResponse.java (NEW - AI response with story updates)
├── story/
│   ├── PlayerStoryState.java (NEW - player's story data)
│   ├── EncounterSummary.java (NEW - compressed encounter record)
│   ├── StoryThread.java (NEW - narrative arc)
│   ├── StoryThreadUpdate.java (NEW - thread progression)
│   ├── StoryStateManager.java (NEW - persistence and caching)
│   ├── StoryThreadManager.java (NEW - thread lifecycle)
│   ├── EncounterHistorySummarizer.java (NEW - compress history)
│   ├── EncounterOutcomeTracker.java (NEW - track encounter results)
│   └── RelationshipState.java (NEW - NPC relationship data)
├── context/
│   └── ContextEnrichmentEngine.java (NEW - gather rich context)
├── encounters/
│   ├── EncounterManager.java (MODIFY - integrate story flow)
│   ├── types/
│   │   └── JsonEncounter.java (KEEP - executes AI-generated encounters)
│   └── EncounterContext.java (KEEP - runtime state)
├── commands/
│   └── EncounterCommand.java (MODIFY - add story subcommands)
├── config/
│   └── ConfigHandler.java (MODIFY - add story config)
└── validation/
    └── EncounterValidator.java (KEEP - AI safety)
```

---

## Appendix B: JSON Format Examples

### Story State File

`world/data/randomencounters/players/550e8400-e29b-41d4-a716-446655440000.json`

```json
{
  "playerUUID": "550e8400-e29b-41d4-a716-446655440000",
  "playerName": "Steve",
  "firstSeenTimestamp": 1703000000000,
  "lastSeenTimestamp": 1703123456789,
  "narrativeSummary": "Steve began as a lone wanderer helping lost travelers. After defending them from bandits, he became embroiled in a conflict with the Bandit Clan. Through determination and combat prowess, he defeated their leader and brought peace to the region.",
  "encounterHistory": [
    {
      "encounterId": "ai_generated_lost_travelers_001",
      "timestamp": 1703000300000,
      "encounterType": "social",
      "briefDescription": "Helped lost travelers find their way",
      "outcome": "success",
      "keyEntities": ["Traveler John", "Traveler Mary"],
      "keyChoices": ["Gave them food", "Provided directions"],
      "storyImpact": {
        "thread_lost_travelers_001": "Created new relationship with Wandering Folk"
      }
    }
  ],
  "activeThreads": {
    "thread_lost_travelers_001": {
      "threadId": "thread_lost_travelers_001",
      "title": "The Grateful Travelers",
      "description": "After helping travelers, your reputation spread",
      "status": "ACTIVE",
      "progressLevel": 4,
      "createdTimestamp": 1703000300000,
      "lastUpdateTimestamp": 1703123456789,
      "keyNPCs": ["Traveler John", "Merchant Guild Master"],
      "keyLocations": ["Forest Road", "Village Square"],
      "currentObjective": "The Merchant Guild wants to thank you personally",
      "threadState": {
        "travelers_saved": "true",
        "merchant_met": "true"
      },
      "priority": "MEDIUM",
      "minEncountersUntilNext": null,
      "triggerCondition": "near_village",
      "narrativeContext": "Word of your kindness has reached the merchant guild. They appreciate your help protecting their travelers from bandits."
    }
  },
  "factionReputation": {
    "Wandering Folk": 45,
    "Bandit Clan": -75,
    "Village Merchants": 30
  },
  "npcRelationships": {
    "traveler_john": {
      "npcId": "traveler_john",
      "npcName": "Traveler John",
      "relationshipLevel": 60,
      "relationshipType": "grateful",
      "firstMet": 1703000300000,
      "lastInteraction": 1703000300000,
      "notes": "You saved him from bandits"
    }
  },
  "playerTraits": {
    "playstyle": "heroic_helper",
    "alignment": "lawful_good",
    "preferredApproach": "direct_combat",
    "riskTolerance": "high"
  },
  "causedEvents": [
    {
      "eventId": "bandit_clan_defeated",
      "eventType": "faction_defeated",
      "description": "Defeated the Bandit Clan leader, causing the clan to disband",
      "timestamp": 1703123456789
    }
  ],
  "itemsCollected": {
    "minecraft:diamond_sword": 1,
    "minecraft:enchanted_book": 3
  },
  "encounterTypePreferences": {
    "combat": 0.8,
    "social": 0.6,
    "mystery": 0.3,
    "trade": 0.5
  }
}
```

---

## Appendix C: API Call Example

### Request to OpenAI

```json
{
  "model": "gpt-4",
  "messages": [
    {
      "role": "system",
      "content": "You are a creative dungeon master for a Minecraft adventure..."
    },
    {
      "role": "user",
      "content": "=== NARRATIVE CONTEXT ===\n\nPLAYER JOURNEY SUMMARY:\nSteve began as a lone wanderer helping lost travelers...\n\n=== CURRENT SITUATION ===\n..."
    }
  ],
  "temperature": 0.8,
  "max_tokens": 2000
}
```

### Response from OpenAI

```json
{
  "encounter": {
    "id": "ai_generated_merchant_thanks_042",
    "enabled": true,
    "weight": 10,
    "description": "The Merchant Guild Master personally thanks you",
    "entities": [
      {
        "label": "guild_master",
        "type": "minecraft:villager",
        "customName": "§6Guild Master Aldric",
        "spawnLocation": {
          "mode": "nearPlayer",
          "radius": { "min": 5, "max": 10 }
        }
      }
    ],
    "onStart": [
      {
        "type": "broadcast",
        "message": "§6Guild Master Aldric approaches you with a grateful smile"
      }
    ]
  },
  "story_updates": {
    "encounter_summary": "Met Guild Master Aldric who thanked me for protecting merchants",
    "thread_updates": [
      {
        "thread_id": "thread_lost_travelers_001",
        "progress_change": 2,
        "narrative_update": "The guild master personally expressed gratitude and offered future cooperation"
      }
    ],
    "new_threads": [],
    "key_choices": ["Accept reward", "Decline reward", "Ask for information"]
  }
}
```

---

**End of Design Document**
