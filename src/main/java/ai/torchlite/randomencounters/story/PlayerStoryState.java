package ai.torchlite.randomencounters.story;

import com.google.gson.annotations.SerializedName;
import java.util.*;

/**
 * Complete persistent story state for a single player
 */
public class PlayerStoryState {

    // Identity
    @SerializedName("player_uuid")
    private UUID playerUUID;

    @SerializedName("player_name")
    private String playerName;

    @SerializedName("first_seen_timestamp")
    private long firstSeenTimestamp;

    @SerializedName("last_seen_timestamp")
    private long lastSeenTimestamp;

    // Encounter History (compressed summaries)
    @SerializedName("encounter_history")
    private List<EncounterSummary> encounterHistory; // Last 100 encounters

    @SerializedName("narrative_summary")
    private String narrativeSummary; // AI-generated summary of player's journey

    // Active Story Threads
    @SerializedName("active_threads")
    private Map<String, StoryThread> activeThreads; // threadId -> StoryThread

    // Relationships & Reputation
    @SerializedName("faction_reputation")
    private Map<String, Integer> factionReputation; // faction -> score (-100 to 100)

    // Player Characteristics (AI-inferred)
    @SerializedName("player_traits")
    private Map<String, String> playerTraits; // "playstyle" -> "aggressive", etc.

    // Encounter Preferences (learned from behavior)
    @SerializedName("encounter_type_preferences")
    private Map<String, Float> encounterTypePreferences; // "combat" -> 0.8, etc.

    public PlayerStoryState(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.firstSeenTimestamp = System.currentTimeMillis();
        this.lastSeenTimestamp = this.firstSeenTimestamp;
        this.encounterHistory = new ArrayList<>();
        this.activeThreads = new HashMap<>();
        this.factionReputation = new HashMap<>();
        this.playerTraits = new HashMap<>();
        this.encounterTypePreferences = new HashMap<>();

        // Initialize Dregora faction reputation
        initializeDregoraFactions();
    }

    /**
     * Initialize default reputation for Dregora factions
     */
    private void initializeDregoraFactions() {
        factionReputation.put("Dregorian Colonists", 50);      // Your people - Friendly
        factionReputation.put("Wasteland Nomads", 0);          // Neutral
        factionReputation.put("Kingdom of Daeroc", 0);         // Neutral
        factionReputation.put("Scavenger Guilds", 0);          // Neutral
        factionReputation.put("Eldritch Cults", -25);          // Unfriendly
        factionReputation.put("Nature's Guardians", 10);       // Slightly positive
        factionReputation.put("The Corrupted", -75);           // Hostile
        factionReputation.put("Underground Cities", -10);      // Suspicious
        factionReputation.put("Merchant League", 25);          // Friendly (traders)
    }

    /**
     * Add an encounter summary to history, maintaining max size
     */
    public void addEncounterSummary(EncounterSummary summary) {
        encounterHistory.add(0, summary); // Add to beginning (most recent first)

        // Keep only last 100 encounters
        if (encounterHistory.size() > 100) {
            encounterHistory = new ArrayList<>(encounterHistory.subList(0, 100));
        }
    }

    /**
     * Get recent encounters (most recent first)
     */
    public List<EncounterSummary> getRecentEncounters(int count) {
        if (encounterHistory.size() <= count) {
            return new ArrayList<>(encounterHistory);
        }
        return new ArrayList<>(encounterHistory.subList(0, count));
    }

    /**
     * Add or update a story thread
     */
    public void addThread(StoryThread thread) {
        activeThreads.put(thread.getThreadId(), thread);
    }

    /**
     * Update an existing thread
     */
    public void updateThread(StoryThreadUpdate update) {
        StoryThread thread = activeThreads.get(update.getThreadId());
        if (thread == null) {
            return; // Thread doesn't exist
        }

        // Apply progress change
        thread.setProgressLevel(thread.getProgressLevel() + update.getProgressChange());

        // Update narrative context
        if (update.getNarrativeUpdate() != null && !update.getNarrativeUpdate().isEmpty()) {
            thread.appendNarrativeContext(update.getNarrativeUpdate());
        }

        // Update status if specified
        if (update.getStatusChange() != null) {
            thread.setStatus(update.getStatusChange());
        }

        // Update priority if specified
        if (update.getPriorityChange() != null) {
            thread.setPriority(update.getPriorityChange());
        }

        // Check for auto-resolution
        if (thread.getProgressLevel() >= 10 && thread.getStatus() == ThreadStatus.ACTIVE) {
            thread.setStatus(ThreadStatus.RESOLVED);
        }

        thread.setLastUpdateTimestamp(System.currentTimeMillis());
    }

    /**
     * Get all active threads (not dormant, resolved, or failed)
     */
    public List<StoryThread> getActiveThreadsList() {
        List<StoryThread> result = new ArrayList<>();
        for (StoryThread thread : activeThreads.values()) {
            if (thread.getStatus() == ThreadStatus.ACTIVE) {
                result.add(thread);
            }
        }
        return result;
    }

    /**
     * Get urgent thread if one exists
     */
    public StoryThread getUrgentThread() {
        for (StoryThread thread : activeThreads.values()) {
            if (thread.getStatus() == ThreadStatus.ACTIVE &&
                thread.getPriority() == ThreadPriority.URGENT) {
                return thread;
            }
        }
        return null;
    }

    /**
     * Modify faction reputation
     */
    public void modifyFactionReputation(String faction, int change) {
        int current = factionReputation.getOrDefault(faction, 0);
        int newValue = Math.max(-100, Math.min(100, current + change));
        factionReputation.put(faction, newValue);
    }

    // Getters and setters
    public UUID getPlayerUUID() { return playerUUID; }
    public void setPlayerUUID(UUID playerUUID) { this.playerUUID = playerUUID; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public long getFirstSeenTimestamp() { return firstSeenTimestamp; }
    public void setFirstSeenTimestamp(long firstSeenTimestamp) { this.firstSeenTimestamp = firstSeenTimestamp; }

    public long getLastSeenTimestamp() { return lastSeenTimestamp; }
    public void setLastSeenTimestamp(long lastSeenTimestamp) { this.lastSeenTimestamp = lastSeenTimestamp; }

    public List<EncounterSummary> getEncounterHistory() { return encounterHistory; }
    public void setEncounterHistory(List<EncounterSummary> encounterHistory) { this.encounterHistory = encounterHistory; }

    public String getNarrativeSummary() { return narrativeSummary; }
    public void setNarrativeSummary(String narrativeSummary) { this.narrativeSummary = narrativeSummary; }

    public Map<String, StoryThread> getActiveThreads() { return activeThreads; }
    public void setActiveThreads(Map<String, StoryThread> activeThreads) { this.activeThreads = activeThreads; }

    public Map<String, Integer> getFactionReputation() { return factionReputation; }
    public void setFactionReputation(Map<String, Integer> factionReputation) { this.factionReputation = factionReputation; }

    public Map<String, String> getPlayerTraits() { return playerTraits; }
    public void setPlayerTraits(Map<String, String> playerTraits) { this.playerTraits = playerTraits; }

    public Map<String, Float> getEncounterTypePreferences() { return encounterTypePreferences; }
    public void setEncounterTypePreferences(Map<String, Float> encounterTypePreferences) {
        this.encounterTypePreferences = encounterTypePreferences;
    }
}
