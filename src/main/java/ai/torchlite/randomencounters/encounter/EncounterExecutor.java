package ai.torchlite.randomencounters.encounter;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.story.StorytellingResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates the full execution of an encounter from AI response to in-game spawn
 */
public class EncounterExecutor {

    private final EncounterSpawner spawner;
    private final Gson gson;

    // Active encounters by player UUID
    private final Map<UUID, ActiveEncounter> activeEncounters;

    public EncounterExecutor() {
        this.spawner = new EncounterSpawner();
        this.gson = new Gson();
        this.activeEncounters = new HashMap<>();
    }

    /**
     * Execute an encounter from an AI storytelling response
     *
     * @param response The AI response containing encounter JSON
     * @param player The player experiencing the encounter
     * @param world The world to spawn in
     * @return true if encounter was successfully executed
     */
    public boolean executeEncounter(StorytellingResponse response, EntityPlayer player, World world) {
        if (response == null) {
            RandomEncounters.LOGGER.error("Cannot execute null encounter response");
            return false;
        }

        // Check if player already has an active encounter
        UUID playerUUID = player.getUniqueID();
        if (activeEncounters.containsKey(playerUUID)) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "You already have an active encounter!"));
            return false;
        }

        try {
            // Parse encounter JSON from response
            String encounterJson = response.getEncounterJson();
            if (encounterJson == null || encounterJson.isEmpty()) {
                RandomEncounters.LOGGER.error("Encounter JSON is null or empty");
                player.sendMessage(new TextComponentString(TextFormatting.RED +
                    "Failed to execute encounter: No encounter data"));
                return false;
            }

            EncounterData encounter = gson.fromJson(encounterJson, EncounterData.class);

            if (encounter == null) {
                RandomEncounters.LOGGER.error("Failed to parse encounter JSON");
                player.sendMessage(new TextComponentString(TextFormatting.RED +
                    "Failed to execute encounter: Invalid encounter data"));
                return false;
            }

            // Display encounter narrative
            displayEncounterNarrative(encounter, player);

            // Spawn entities
            List<Entity> entities = spawner.spawnEncounter(encounter, player, world);

            if (entities.isEmpty()) {
                RandomEncounters.LOGGER.warn("No entities were spawned for encounter");
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                    "Encounter narrative delivered, but no entities spawned"));
            }

            // Create active encounter record
            ActiveEncounter active = new ActiveEncounter(encounter, entities, player, System.currentTimeMillis());
            activeEncounters.put(playerUUID, active);

            RandomEncounters.LOGGER.info("Successfully executed encounter: " + encounter.getTitle() +
                " with " + entities.size() + " entities");

            return true;

        } catch (JsonSyntaxException e) {
            RandomEncounters.LOGGER.error("Failed to parse encounter JSON", e);
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Failed to execute encounter: Invalid JSON format"));
            return false;
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to execute encounter", e);
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Failed to execute encounter: " + e.getMessage()));
            return false;
        }
    }

    /**
     * Display the encounter narrative to the player
     */
    private void displayEncounterNarrative(EncounterData encounter, EntityPlayer player) {
        player.sendMessage(new TextComponentString(""));
        player.sendMessage(new TextComponentString(TextFormatting.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "" + TextFormatting.BOLD +
            encounter.getTitle()));
        player.sendMessage(new TextComponentString(TextFormatting.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        if (encounter.getNarrativeText() != null && !encounter.getNarrativeText().isEmpty()) {
            // Split narrative into lines for better readability
            String[] lines = encounter.getNarrativeText().split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    player.sendMessage(new TextComponentString(TextFormatting.WHITE + line.trim()));
                }
            }
        } else if (encounter.getDescription() != null) {
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + encounter.getDescription()));
        }

        player.sendMessage(new TextComponentString(TextFormatting.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(new TextComponentString(""));

        // Display dialogue if present
        if (encounter.getDialogue() != null && !encounter.getDialogue().isEmpty()) {
            for (EncounterData.DialogueOption dialogue : encounter.getDialogue()) {
                if (dialogue.getSpeaker() != null) {
                    player.sendMessage(new TextComponentString(
                        TextFormatting.AQUA + "[" + dialogue.getSpeaker() + "]: " +
                        TextFormatting.WHITE + dialogue.getText()));
                } else {
                    player.sendMessage(new TextComponentString(TextFormatting.GRAY + dialogue.getText()));
                }
            }
            player.sendMessage(new TextComponentString(""));
        }
    }

    /**
     * Get the active encounter for a player
     */
    public ActiveEncounter getActiveEncounter(UUID playerUUID) {
        return activeEncounters.get(playerUUID);
    }

    /**
     * Complete an encounter for a player
     */
    public void completeEncounter(UUID playerUUID, String outcome) {
        ActiveEncounter encounter = activeEncounters.remove(playerUUID);
        if (encounter != null) {
            RandomEncounters.LOGGER.info("Encounter completed for player " + playerUUID +
                " with outcome: " + outcome);
        }
    }

    /**
     * Cancel/clear an encounter for a player
     */
    public void cancelEncounter(UUID playerUUID) {
        ActiveEncounter encounter = activeEncounters.remove(playerUUID);
        if (encounter != null) {
            spawner.despawnEncounter(encounter.getSpawnedEntities());
            RandomEncounters.LOGGER.info("Encounter cancelled for player " + playerUUID);
        }
    }

    /**
     * Check if a player has an active encounter
     */
    public boolean hasActiveEncounter(UUID playerUUID) {
        return activeEncounters.containsKey(playerUUID);
    }

    /**
     * Get all player UUIDs with active encounters
     */
    public java.util.Set<UUID> getActiveEncounterPlayers() {
        return new java.util.HashSet<>(activeEncounters.keySet());
    }

    /**
     * Clean up stale encounters (older than 30 minutes)
     */
    public void cleanupStaleEncounters() {
        long currentTime = System.currentTimeMillis();
        long thirtyMinutes = 30 * 60 * 1000;

        activeEncounters.entrySet().removeIf(entry -> {
            boolean isStale = (currentTime - entry.getValue().getStartTime()) > thirtyMinutes;
            if (isStale) {
                RandomEncounters.LOGGER.info("Removing stale encounter for player " + entry.getKey());
                spawner.despawnEncounter(entry.getValue().getSpawnedEntities());
            }
            return isStale;
        });
    }

    /**
     * Represents an active ongoing encounter
     */
    public static class ActiveEncounter {
        private final EncounterData encounterData;
        private final List<Entity> spawnedEntities;
        private final EntityPlayer player;
        private final long startTime;
        private String outcome;

        public ActiveEncounter(EncounterData encounterData, List<Entity> spawnedEntities,
                             EntityPlayer player, long startTime) {
            this.encounterData = encounterData;
            this.spawnedEntities = spawnedEntities;
            this.player = player;
            this.startTime = startTime;
        }

        public EncounterData getEncounterData() { return encounterData; }
        public List<Entity> getSpawnedEntities() { return spawnedEntities; }
        public EntityPlayer getPlayer() { return player; }
        public long getStartTime() { return startTime; }
        public String getOutcome() { return outcome; }
        public void setOutcome(String outcome) { this.outcome = outcome; }
    }
}
