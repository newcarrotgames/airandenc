# Encounter Clearing System

## Overview

This document explains how encounters are tracked and cleared in the Random Encounters mod.

## How Encounters Are Tracked

When an encounter is generated and spawned, it's stored in `EncounterExecutor.activeEncounters` as an `ActiveEncounter` object containing:
- The encounter data (narrative, entities, outcomes)
- List of spawned entities
- Reference to the player
- Start timestamp
- Outcome (once determined)

## Encounter Completion Triggers

Encounters are automatically completed when any of these conditions occur:

### 1. All Entities Killed (Victory)
**Handler:** `EncounterOutcomeTracker.onEntityDeath()`
- Monitors all entity death events
- Checks if dead entity belongs to an active encounter
- Counts remaining alive hostile entities
- If all entities are dead → completes encounter with "victory" outcome
- Displays victory text to player

### 2. Player Dies (Defeat)
**Handler:** `EncounterOutcomeTracker.onPlayerDeath()`
- Monitors player death events
- If player has active encounter → completes with "defeat" outcome
- Displays defeat text to player
- Clears encounter from active list

### 3. Player Logs Out (Fled)
**Handler:** `EncounterOutcomeTracker.onPlayerLogout()`
- Monitors player logout events
- Auto-completes any active encounter with "fled" outcome
- Prevents encounters from persisting across sessions

### 4. Manual Completion
**Method:** `EncounterOutcomeTracker.manualCompleteEncounter()`
- Can be triggered by commands
- Allows testing or admin intervention
- Accepts custom outcome string

### 5. Stale Encounter Cleanup
**Method:** `EncounterExecutor.cleanupStaleEncounters()`
- Runs periodically (should be called from a tick handler)
- Removes encounters older than 30 minutes
- Despawns any remaining entities
- Prevents memory leaks from stuck encounters

## Clearing Process

When an encounter is completed:

1. **Set Outcome** - `encounter.setOutcome(outcome)`
2. **Display Message** - Show victory/defeat/flee text to player
3. **Remove from Active Map** - `executor.completeEncounter(playerUUID, outcome)`
4. **Generate Summary** - Create `EncounterSummary` for story history
5. **Log Completion** - Record outcome in logs

## Commands

### Clear Current Encounter
```
/encounter clear
```
Immediately cancels the player's active encounter and despawns all entities.

### Check Encounter Status
```
/encounter status
```
Shows whether the player has an active encounter.

## Technical Details

### Entity Death Tracking

The system iterates through all active encounters when an entity dies:

```java
@SubscribeEvent
public void onEntityDeath(LivingDeathEvent event) {
    Entity deadEntity = event.getEntity();

    for (UUID playerUUID : getAllActiveEncounterPlayers()) {
        ActiveEncounter encounter = executor.getActiveEncounter(playerUUID);

        if (encounter != null && encounter.getSpawnedEntities().contains(deadEntity)) {
            checkEncounterCompletion(encounter, playerUUID);
        }
    }
}
```

### Player Death Handling

```java
@SubscribeEvent
public void onPlayerDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) event.getEntity();
        UUID playerUUID = player.getUniqueID();

        if (executor.hasActiveEncounter(playerUUID)) {
            completeEncounterWithOutcome(encounter, playerUUID, "defeat");
        }
    }
}
```

### Getting Active Players

The system maintains a live view of all players with active encounters:

```java
public Set<UUID> getActiveEncounterPlayers() {
    return executor.getActiveEncounterPlayers();
}
```

This returns a snapshot of the `activeEncounters.keySet()` to avoid concurrent modification issues.

## Troubleshooting

### "Encounters are not clearing"

**Possible causes:**

1. **EncounterOutcomeTracker not registered** - Check that the tracker is registered with Forge's event bus:
   ```java
   MinecraftForge.EVENT_BUS.register(encounterOutcomeTracker);
   ```

2. **Entity death not detected** - Verify that spawned entities are properly tracked in the ActiveEncounter's entity list

3. **Player UUID mismatch** - Ensure the same UUID is used for tracking and clearing

4. **Thread safety issues** - All event handlers run on the main server thread, so this shouldn't be an issue

### "Player death doesn't clear encounter"

**Check:**
1. Event is firing: Add logging to `onPlayerDeath` handler
2. Encounter is active: Verify `hasActiveEncounter()` returns true
3. Outcome text is configured: Check `defeat_text` in encounter JSON

### "Entities remain after encounter clears"

**Solution:**
The `completeEncounter()` method doesn't despawn entities automatically. To despawn:
```java
executor.cancelEncounter(playerUUID); // This despawns entities
```

Consider updating `completeEncounter()` to also call `spawner.despawnEncounter()` if needed.

## Future Improvements

1. **Distance-based clearing** - Clear encounter if player moves too far away
2. **Time-based progression** - Encounters evolve or escalate over time
3. **Partial completion** - Reward partial success (killed some but not all entities)
4. **Negotiation outcomes** - Add dialogue-based resolution paths
5. **Encounter chains** - One encounter leads to another
6. **Reputation effects** - Clearing encounters affects faction standing
