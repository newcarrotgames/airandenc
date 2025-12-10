package ai.torchlite.randomencounters.command;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.ai.AIStorytellingEngine;
import ai.torchlite.randomencounters.story.PlayerStoryState;
import ai.torchlite.randomencounters.story.StoryStateManager;
import ai.torchlite.randomencounters.story.StoryThread;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Command for managing encounters and story state
 */
public class EncounterCommand extends CommandBase {

    private final AIStorytellingEngine aiEngine = new AIStorytellingEngine();

    @Override
    public String getName() {
        return "encounter";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/encounter <story|threads|history|generate|clear|services|reload|reputation|context>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // All players can use basic commands
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("This command must be used by a player"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "story":
                showStoryState(player);
                break;
            case "threads":
                showThreads(player);
                break;
            case "history":
                showHistory(player, args);
                break;
            case "generate":
                generateEncounter(player);
                break;
            case "clear":
                clearEncounter(player);
                break;
            case "services":
                showServices(player);
                break;
            case "reload":
                reloadConfig(player);
                break;
            case "reputation":
            case "rep":
                showReputation(player);
                break;
            case "context":
            case "ctx":
                showContext(player);
                break;
            default:
                sendHelp(player);
                break;
        }
    }

    private void sendHelp(EntityPlayer player) {
        player.sendMessage(new TextComponentString(TextFormatting.GOLD + "=== Random Encounters Commands ==="));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter story" +
            TextFormatting.WHITE + " - View your story state"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter threads" +
            TextFormatting.WHITE + " - List active story threads"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter history [count]" +
            TextFormatting.WHITE + " - View encounter history"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter reputation" +
            TextFormatting.WHITE + " - View faction reputations"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter context" +
            TextFormatting.WHITE + " - View AI context (biome, faction, etc.)"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter generate" +
            TextFormatting.WHITE + " - Generate an AI encounter"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter clear" +
            TextFormatting.WHITE + " - Clear active encounter"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter services" +
            TextFormatting.WHITE + " - List available AI services"));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "/encounter reload" +
            TextFormatting.WHITE + " - Reload configuration (op only)"));
    }

    private void showStoryState(EntityPlayer player) {
        StoryStateManager manager = StoryStateManager.getInstance();
        if (manager == null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Story system not initialized"));
            return;
        }

        PlayerStoryState state = manager.getOrCreateState(player);

        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== Your Story State ==="));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Player: " +
            TextFormatting.WHITE + state.getPlayerName()));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Total Encounters: " +
            TextFormatting.WHITE + state.getEncounterHistory().size()));
        player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Active Threads: " +
            TextFormatting.WHITE + state.getActiveThreadsList().size()));

        // Show faction reputations
        player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Faction Standings:"));
        state.getFactionReputation().forEach((faction, rep) -> {
            TextFormatting color = getReputationColor(rep);
            player.sendMessage(new TextComponentString(
                color + "  " + faction + ": " + rep + " (" + getReputationText(rep) + ")"));
        });
    }

    private void showThreads(EntityPlayer player) {
        StoryStateManager manager = StoryStateManager.getInstance();
        if (manager == null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Story system not initialized"));
            return;
        }

        PlayerStoryState state = manager.getOrCreateState(player);
        List<StoryThread> threads = state.getActiveThreadsList();

        if (threads.isEmpty()) {
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                "You have no active story threads"));
            return;
        }

        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== Active Story Threads ==="));

        for (StoryThread thread : threads) {
            TextFormatting color = getThreadColor(thread);
            player.sendMessage(new TextComponentString(
                color + thread.getTitle() +
                TextFormatting.WHITE + " [" + thread.getPriority() + "]"));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Progress: " + thread.getProgressLevel() + "/10 | " +
                "Status: " + thread.getStatus()));
            if (thread.getDescription() != null && !thread.getDescription().isEmpty()) {
                player.sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "  " + thread.getDescription()));
            }
        }
    }

    private void showHistory(EntityPlayer player, String[] args) {
        StoryStateManager manager = StoryStateManager.getInstance();
        if (manager == null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Story system not initialized"));
            return;
        }

        int count = 5; // Default to last 5
        if (args.length > 1) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(new TextComponentString(TextFormatting.RED +
                    "Invalid number: " + args[1]));
                return;
            }
        }

        PlayerStoryState state = manager.getOrCreateState(player);
        List<ai.torchlite.randomencounters.story.EncounterSummary> history = state.getEncounterHistory();

        if (history.isEmpty()) {
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                "No encounter history"));
            return;
        }

        count = Math.min(count, history.size());
        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== Last " + count + " Encounters ==="));

        for (int i = history.size() - count; i < history.size(); i++) {
            ai.torchlite.randomencounters.story.EncounterSummary encounter = history.get(i);
            TextFormatting color = getOutcomeColor(encounter.getOutcome());
            player.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "[" + encounter.getEncounterType() + "] " +
                TextFormatting.WHITE + encounter.getBriefDescription()));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "  Outcome: " + color + encounter.getOutcome()));
        }
    }

    private void clearEncounter(EntityPlayer player) {
        java.util.UUID playerUUID = player.getUniqueID();
        boolean hadEncounter = RandomEncounters.getEncounterExecutor()
            .hasActiveEncounter(playerUUID);

        if (hadEncounter) {
            RandomEncounters.getEncounterExecutor().completeEncounter(playerUUID, "abandoned");
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                "Active encounter cleared. You can now generate a new encounter."));
        } else {
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                "You don't have an active encounter."));
        }
    }

    private void generateEncounter(EntityPlayer player) {
        if (!aiEngine.hasAvailableService()) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "No AI services configured. Use /encounter services for details"));
            return;
        }

        player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
            "Generating encounter..."));

        // Get the server instance
        final net.minecraft.server.MinecraftServer server = player.getServer();

        // Run async to avoid blocking the game thread
        new Thread(() -> {
            try {
                ai.torchlite.randomencounters.story.StorytellingResponse response =
                    aiEngine.generateEncounter(player, player.world);

                if (response != null) {
                    // Schedule encounter execution on the main server thread
                    // (Entity spawning must happen on the main thread to avoid ConcurrentModificationException)
                    server.addScheduledTask(() -> {
                        try {
                            boolean success = RandomEncounters.getEncounterExecutor()
                                .executeEncounter(response, player, player.world);

                            if (success) {
                                player.sendMessage(new TextComponentString(TextFormatting.GREEN +
                                    "Encounter spawned successfully!"));
                            } else {
                                player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                                    "Encounter generated but spawning failed"));
                            }
                        } catch (Exception e) {
                            RandomEncounters.LOGGER.error("Failed to execute encounter", e);
                            player.sendMessage(new TextComponentString(TextFormatting.RED +
                                "Failed to spawn encounter: " + e.getMessage()));
                        }
                    });
                } else {
                    player.sendMessage(new TextComponentString(TextFormatting.RED +
                        "Failed to generate encounter. Check server logs"));
                }
            } catch (Exception e) {
                RandomEncounters.LOGGER.error("Error generating encounter", e);
                player.sendMessage(new TextComponentString(TextFormatting.RED +
                    "Error generating encounter: " + e.getMessage()));
            }
        }).start();
    }

    private void showServices(EntityPlayer player) {
        List<String> services = aiEngine.getAvailableServices();

        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== Available AI Services ==="));

        if (services.isEmpty()) {
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW +
                "No AI services configured"));
            player.sendMessage(new TextComponentString(TextFormatting.GRAY +
                "Configure in config/randomencounters.cfg"));
            player.sendMessage(new TextComponentString(TextFormatting.GRAY +
                "Set enableOpenAI or enableAnthropic to true"));
            player.sendMessage(new TextComponentString(TextFormatting.GRAY +
                "Add your API key to the config file"));
        } else {
            for (String service : services) {
                player.sendMessage(new TextComponentString(TextFormatting.GREEN +
                    "✓ " + service));
            }
        }
    }

    private void reloadConfig(EntityPlayer player) {
        if (!player.canUseCommand(2, "encounter.reload")) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "You don't have permission to reload the config"));
            return;
        }

        ai.torchlite.randomencounters.config.ConfigHandler.reload();
        player.sendMessage(new TextComponentString(TextFormatting.GREEN +
            "Configuration reloaded"));
    }

    // Helper methods for colors
    private TextFormatting getReputationColor(int rep) {
        if (rep >= 50) return TextFormatting.GREEN;
        if (rep >= 25) return TextFormatting.DARK_GREEN;
        if (rep >= 0) return TextFormatting.YELLOW;
        if (rep >= -25) return TextFormatting.GOLD;
        if (rep >= -50) return TextFormatting.RED;
        return TextFormatting.DARK_RED;
    }

    private String getReputationText(int rep) {
        if (rep >= 75) return "Revered";
        if (rep >= 50) return "Honored";
        if (rep >= 25) return "Friendly";
        if (rep >= 0) return "Neutral";
        if (rep >= -25) return "Unfriendly";
        if (rep >= -50) return "Hostile";
        return "Hated";
    }

    private TextFormatting getThreadColor(StoryThread thread) {
        switch (thread.getPriority()) {
            case URGENT: return TextFormatting.RED;
            case HIGH: return TextFormatting.GOLD;
            case MEDIUM: return TextFormatting.YELLOW;
            case LOW: return TextFormatting.GRAY;
            default: return TextFormatting.WHITE;
        }
    }

    private TextFormatting getOutcomeColor(String outcome) {
        if (outcome == null) return TextFormatting.GRAY;
        switch (outcome.toLowerCase()) {
            case "victory":
            case "success":
                return TextFormatting.GREEN;
            case "fled":
            case "escaped":
                return TextFormatting.YELLOW;
            case "failed":
            case "defeat":
                return TextFormatting.RED;
            default:
                return TextFormatting.GRAY;
        }
    }

    /**
     * Show faction reputations
     */
    private void showReputation(EntityPlayer player) {
        StoryStateManager manager = StoryStateManager.getInstance();
        if (manager == null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Story system not initialized"));
            return;
        }

        PlayerStoryState state = manager.getOrCreateState(player);

        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== Faction Reputations ==="));

        if (state.getFactionReputation().isEmpty()) {
            player.sendMessage(new TextComponentString(TextFormatting.GRAY +
                "You haven't encountered any factions yet."));
            return;
        }

        // Sort factions by reputation (highest first)
        state.getFactionReputation().entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                String faction = entry.getKey();
                int rep = entry.getValue();
                TextFormatting color = getReputationColor(rep);
                String standing = getReputationText(rep);

                player.sendMessage(new TextComponentString(
                    color + "  " + faction + ": " +
                    TextFormatting.WHITE + rep + " " +
                    color + "(" + standing + ")"));
            });
    }

    /**
     * Show AI context that would be used for encounter generation
     */
    private void showContext(EntityPlayer player) {
        player.sendMessage(new TextComponentString(TextFormatting.GOLD +
            "=== AI Context Preview ==="));
        player.sendMessage(new TextComponentString(TextFormatting.GRAY +
            "This is the context that would be sent to the AI:"));
        player.sendMessage(new TextComponentString(""));

        try {
            // Build the context using the same engine as encounter generation
            ai.torchlite.randomencounters.context.ContextEnrichmentEngine contextEngine =
                new ai.torchlite.randomencounters.context.ContextEnrichmentEngine();
            ai.torchlite.randomencounters.ai.StorytellingRequest request =
                contextEngine.buildStorytellingRequest(player, player.world);

            // Display location context
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Location:"));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Biome: " +
                TextFormatting.AQUA + request.getBiome()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Dimension: " +
                TextFormatting.AQUA + request.getDimension()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Position: " +
                TextFormatting.AQUA + request.getPosX() + ", " + request.getPosY() + ", " + request.getPosZ()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Time: " +
                TextFormatting.AQUA + request.getTimeOfDay()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Weather: " +
                TextFormatting.AQUA + request.getWeather()));

            if (request.getCurrentLocation() != null && !request.getCurrentLocation().isEmpty()) {
                player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Location: " +
                    TextFormatting.AQUA + request.getCurrentLocation()));
            }

            player.sendMessage(new TextComponentString(""));

            // Display player context
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Player:"));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Level: " +
                TextFormatting.AQUA + request.getPlayerLevel()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Health: " +
                TextFormatting.AQUA + String.format("%.1f/%.1f",
                    request.getPlayerHealth(), request.getPlayerMaxHealth())));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Difficulty Rating: " +
                TextFormatting.AQUA + String.format("%.2f", request.getLocalDifficultyRating())));

            player.sendMessage(new TextComponentString(""));

            // Show faction reputations
            StoryStateManager manager = StoryStateManager.getInstance();
            if (manager != null) {
                PlayerStoryState state = manager.getOrCreateState(player);
                if (!state.getFactionReputation().isEmpty()) {
                    player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Faction Reputations:"));
                    state.getFactionReputation().forEach((faction, rep) -> {
                        TextFormatting color = getReputationColor(rep);
                        player.sendMessage(new TextComponentString(
                            color + "  " + faction + ": " + rep + " (" + getReputationText(rep) + ")"));
                    });
                }
            }

            player.sendMessage(new TextComponentString(""));

            // Display story context
            player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Story State:"));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Total Encounters: " +
                TextFormatting.AQUA + request.getRecentEncounters().size()));
            player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Active Threads: " +
                TextFormatting.AQUA + request.getActiveThreads().size()));

            // Show recent encounters
            if (!request.getRecentEncounters().isEmpty()) {
                player.sendMessage(new TextComponentString(TextFormatting.WHITE + "  Recent:"));
                int count = Math.min(3, request.getRecentEncounters().size());
                for (int i = 0; i < count; i++) {
                    ai.torchlite.randomencounters.story.EncounterSummary summary =
                        request.getRecentEncounters().get(i);
                    player.sendMessage(new TextComponentString(TextFormatting.GRAY + "    - " +
                        summary.getEncounterType() + ": " + summary.getBriefDescription()));
                }
            }

            player.sendMessage(new TextComponentString(""));

            // Display inventory context
            if (request.getNotableItems() != null && !request.getNotableItems().isEmpty()) {
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Notable Items:"));
                request.getNotableItems().forEach(item ->
                    player.sendMessage(new TextComponentString(TextFormatting.GRAY + "  - " + item)));
            }

            player.sendMessage(new TextComponentString(""));
            player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Tip: This context influences" +
                " what encounters the AI generates."));

        } catch (Exception e) {
            player.sendMessage(new TextComponentString(TextFormatting.RED +
                "Error building context: " + e.getMessage()));
            RandomEncounters.LOGGER.error("Error showing context", e);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("story");
            completions.add("threads");
            completions.add("history");
            completions.add("reputation");
            completions.add("context");
            completions.add("generate");
            completions.add("clear");
            completions.add("services");
            completions.add("reload");
        }
        return getListOfStringsMatchingLastWord(args, completions);
    }
}
