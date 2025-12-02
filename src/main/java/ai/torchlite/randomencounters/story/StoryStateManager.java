package ai.torchlite.randomencounters.story;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages persistent story state for all players
 * Handles loading, saving, and caching of PlayerStoryState objects
 */
public class StoryStateManager {

    private static StoryStateManager instance;

    private final File storyDataDirectory;
    private final Map<UUID, PlayerStoryState> stateCache;
    private final Gson gson;

    private StoryStateManager(File worldDirectory) {
        this.storyDataDirectory = new File(worldDirectory, "data/story_encounters/players");
        if (!storyDataDirectory.exists()) {
            storyDataDirectory.mkdirs();
        }

        this.stateCache = new ConcurrentHashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("[StoryEncounters] Story state directory: " + storyDataDirectory.getAbsolutePath());
    }

    /**
     * Initialize the manager with world directory
     */
    public static void initialize(File worldDirectory) {
        instance = new StoryStateManager(worldDirectory);
    }

    /**
     * Get the singleton instance
     */
    public static StoryStateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("StoryStateManager not initialized! Call initialize() first.");
        }
        return instance;
    }

    /**
     * Get or create story state for a player
     */
    public PlayerStoryState getOrCreateState(UUID playerUUID, String playerName) {
        // Check cache first
        PlayerStoryState state = stateCache.get(playerUUID);
        if (state != null) {
            state.setLastSeenTimestamp(System.currentTimeMillis());
            return state;
        }

        // Try to load from disk
        state = loadState(playerUUID);
        if (state != null) {
            state.setLastSeenTimestamp(System.currentTimeMillis());
            stateCache.put(playerUUID, state);
            return state;
        }

        // Create new state
        state = new PlayerStoryState(playerUUID, playerName);
        stateCache.put(playerUUID, state);
        saveState(state);

        System.out.println("[StoryEncounters] Created new story state for player: " + playerName);
        return state;
    }

    /**
     * Get story state for a player (convenience method)
     */
    public PlayerStoryState getOrCreateState(EntityPlayer player) {
        return getOrCreateState(player.getUniqueID(), player.getName());
    }

    /**
     * Load story state from disk
     */
    private PlayerStoryState loadState(UUID playerUUID) {
        File stateFile = getStateFile(playerUUID);
        if (!stateFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(stateFile)) {
            PlayerStoryState state = gson.fromJson(reader, PlayerStoryState.class);
            System.out.println("[StoryEncounters] Loaded story state for player: " + playerUUID);
            return state;
        } catch (Exception e) {
            System.err.println("[StoryEncounters] Error loading story state for " + playerUUID + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save story state to disk
     */
    public void saveState(PlayerStoryState state) {
        File stateFile = getStateFile(state.getPlayerUUID());

        try (FileWriter writer = new FileWriter(stateFile)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            System.err.println("[StoryEncounters] Error saving story state for " +
                state.getPlayerUUID() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save all cached states
     */
    public void saveAllStates() {
        System.out.println("[StoryEncounters] Saving all player story states...");
        int count = 0;
        for (PlayerStoryState state : stateCache.values()) {
            saveState(state);
            count++;
        }
        System.out.println("[StoryEncounters] Saved " + count + " story states");
    }

    /**
     * Remove a player from cache (call when player logs out)
     */
    public void unloadState(UUID playerUUID) {
        PlayerStoryState state = stateCache.remove(playerUUID);
        if (state != null) {
            saveState(state); // Save before unloading
        }
    }

    /**
     * Get the file path for a player's state
     */
    private File getStateFile(UUID playerUUID) {
        return new File(storyDataDirectory, playerUUID.toString() + ".json");
    }

    /**
     * Clear cache (useful for testing)
     */
    public void clearCache() {
        saveAllStates();
        stateCache.clear();
    }

    /**
     * Get cache size
     */
    public int getCacheSize() {
        return stateCache.size();
    }
}
