package ai.torchlite.randomencounters.encounter;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.story.EncounterSummary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks encounter outcomes by monitoring entity deaths and player actions
 */
public class EncounterOutcomeTracker {

    private final EncounterExecutor executor;

    public EncounterOutcomeTracker(EncounterExecutor executor) {
        this.executor = executor;
    }

    /**
     * Handle entity death events to track encounter outcomes
     */
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Check if this death is related to an active encounter
        Entity deadEntity = event.getEntity();

        // Find if any player has an active encounter involving this entity
        for (UUID playerUUID : new HashSet<>(getAllActiveEncounterPlayers())) {
            EncounterExecutor.ActiveEncounter encounter = executor.getActiveEncounter(playerUUID);

            if (encounter != null && encounter.getSpawnedEntities().contains(deadEntity)) {
                // Entity from encounter was killed
                checkEncounterCompletion(encounter, playerUUID);
            }
        }
    }

    /**
     * Handle player logout to auto-complete or cancel encounters
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        UUID playerUUID = player.getUniqueID();

        if (executor.hasActiveEncounter(playerUUID)) {
            // Auto-complete encounter with "fled" outcome on logout
            executor.completeEncounter(playerUUID, "fled");
            RandomEncounters.LOGGER.info("Auto-completed encounter for logging out player: " + player.getName());
        }
    }

    /**
     * Handle player death to complete encounters with defeat outcome
     */
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            UUID playerUUID = player.getUniqueID();

            if (executor.hasActiveEncounter(playerUUID)) {
                // Player died during encounter - mark as defeat
                EncounterExecutor.ActiveEncounter encounter = executor.getActiveEncounter(playerUUID);
                if (encounter != null) {
                    completeEncounterWithOutcome(encounter, playerUUID, "defeat");
                    RandomEncounters.LOGGER.info("Player died during encounter: " + player.getName());
                } else {
                    // Fallback: just clear it
                    executor.completeEncounter(playerUUID, "defeat");
                }
            }
        }
    }

    /**
     * Check if an encounter should be completed based on entity deaths
     */
    private void checkEncounterCompletion(EncounterExecutor.ActiveEncounter encounter, UUID playerUUID) {
        // Count alive hostile entities
        int aliveHostileCount = 0;
        for (Entity entity : encounter.getSpawnedEntities()) {
            if (entity != null && !entity.isDead) {
                aliveHostileCount++;
            }
        }

        // If all entities are dead, encounter is victorious
        if (aliveHostileCount == 0) {
            completeEncounterWithOutcome(encounter, playerUUID, "victory");
        }
    }

    /**
     * Complete an encounter with a specific outcome
     */
    private void completeEncounterWithOutcome(EncounterExecutor.ActiveEncounter encounter,
                                             UUID playerUUID, String outcome) {
        encounter.setOutcome(outcome);
        executor.completeEncounter(playerUUID, outcome);

        // Send outcome message to player
        EntityPlayer player = encounter.getPlayer();
        if (player != null) {
            displayOutcome(player, encounter.getEncounterData(), outcome);
        }

        RandomEncounters.LOGGER.info("Encounter completed with outcome: " + outcome +
            " for player " + playerUUID);
    }

    /**
     * Display the encounter outcome to the player
     */
    private void displayOutcome(EntityPlayer player, EncounterData encounter, String outcome) {
        if (encounter.getOutcomes() == null) {
            return;
        }

        String outcomeText = null;
        net.minecraft.util.text.TextFormatting color = net.minecraft.util.text.TextFormatting.WHITE;

        switch (outcome.toLowerCase()) {
            case "victory":
                outcomeText = encounter.getOutcomes().getVictoryText();
                color = net.minecraft.util.text.TextFormatting.GREEN;
                break;
            case "defeat":
                outcomeText = encounter.getOutcomes().getDefeatText();
                color = net.minecraft.util.text.TextFormatting.RED;
                break;
            case "fled":
                outcomeText = encounter.getOutcomes().getFleeText();
                color = net.minecraft.util.text.TextFormatting.YELLOW;
                break;
            case "negotiated":
                outcomeText = encounter.getOutcomes().getNegotiateText();
                color = net.minecraft.util.text.TextFormatting.AQUA;
                break;
        }

        if (outcomeText != null && !outcomeText.isEmpty()) {
            player.sendMessage(new net.minecraft.util.text.TextComponentString(""));
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                color + "━━━ Encounter Complete ━━━"));
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                net.minecraft.util.text.TextFormatting.WHITE + outcomeText));
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                color + "━━━━━━━━━━━━━━━━━━━━━━━━"));
            player.sendMessage(new net.minecraft.util.text.TextComponentString(""));
        }
    }

    /**
     * Generate an EncounterSummary from a completed encounter
     */
    public EncounterSummary generateSummary(EncounterExecutor.ActiveEncounter encounter) {
        EncounterData data = encounter.getEncounterData();
        String outcome = encounter.getOutcome() != null ? encounter.getOutcome() : "incomplete";

        EncounterSummary summary = new EncounterSummary(
            UUID.randomUUID().toString(),
            data.getType() != null ? data.getType() : "unknown",
            data.getDescription() != null ? data.getDescription() : data.getTitle(),
            outcome
        );

        // Add entity names as key entities
        if (data.getEntities() != null) {
            for (EncounterData.EncounterEntity entityData : data.getEntities()) {
                if (entityData.getName() != null && !entityData.getName().isEmpty()) {
                    summary.getKeyEntities().add(entityData.getName());
                }
            }
        }

        return summary;
    }

    /**
     * Get all player UUIDs with active encounters
     */
    private Set<UUID> getAllActiveEncounterPlayers() {
        return executor.getActiveEncounterPlayers();
    }

    /**
     * Manually trigger encounter completion (for testing or commands)
     */
    public void manualCompleteEncounter(EntityPlayer player, String outcome) {
        UUID playerUUID = player.getUniqueID();
        EncounterExecutor.ActiveEncounter encounter = executor.getActiveEncounter(playerUUID);

        if (encounter != null) {
            completeEncounterWithOutcome(encounter, playerUUID, outcome);
        }
    }
}
