package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.story.EncounterSummary;
import ai.torchlite.randomencounters.story.StoryThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extended AI request with full story context for Dregora
 */
public class StorytellingRequest extends AIEncounterRequest {

    // Story Context
    private String narrativeSummary; // Compressed player journey
    private List<EncounterSummary> recentEncounters; // Last 5-10 encounters
    private List<StoryThread> activeThreads; // Current story arcs
    private Map<String, Integer> factionReputation;
    private Map<String, String> playerTraits;

    // Inventory & Equipment Context
    private List<String> notableItems; // Unique/rare items
    private Map<String, String> equipment; // Worn equipment

    // Social Context
    private List<String> nearbyPlayerNames;
    private String currentLocation; // Named location if near structure

    // Generation Guidance
    private String narrativeTone; // "epic", "horror", "whimsical", "grimdark"
    private Float desiredDifficulty; // 0.0-1.0
    private String preferredType; // null = surprise me
    private StoryThread focusThread; // Specific thread to advance

    public StorytellingRequest() {
        super();
        this.recentEncounters = new ArrayList<>();
        this.activeThreads = new ArrayList<>();
        this.factionReputation = new HashMap<>();
        this.playerTraits = new HashMap<>();
        this.notableItems = new ArrayList<>();
        this.equipment = new HashMap<>();
        this.nearbyPlayerNames = new ArrayList<>();
    }

    // Getters and setters
    public String getNarrativeSummary() { return narrativeSummary; }
    public void setNarrativeSummary(String narrativeSummary) { this.narrativeSummary = narrativeSummary; }

    public List<EncounterSummary> getRecentEncounters() { return recentEncounters; }
    public void setRecentEncounters(List<EncounterSummary> recentEncounters) { this.recentEncounters = recentEncounters; }

    public List<StoryThread> getActiveThreads() { return activeThreads; }
    public void setActiveThreads(List<StoryThread> activeThreads) { this.activeThreads = activeThreads; }

    public Map<String, Integer> getFactionReputation() { return factionReputation; }
    public void setFactionReputation(Map<String, Integer> factionReputation) { this.factionReputation = factionReputation; }

    public Map<String, String> getPlayerTraits() { return playerTraits; }
    public void setPlayerTraits(Map<String, String> playerTraits) { this.playerTraits = playerTraits; }

    public List<String> getNotableItems() { return notableItems; }
    public void setNotableItems(List<String> notableItems) { this.notableItems = notableItems; }

    public Map<String, String> getEquipment() { return equipment; }
    public void setEquipment(Map<String, String> equipment) { this.equipment = equipment; }

    public List<String> getNearbyPlayerNames() { return nearbyPlayerNames; }
    public void setNearbyPlayerNames(List<String> nearbyPlayerNames) { this.nearbyPlayerNames = nearbyPlayerNames; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getNarrativeTone() { return narrativeTone; }
    public void setNarrativeTone(String narrativeTone) { this.narrativeTone = narrativeTone; }

    public Float getDesiredDifficulty() { return desiredDifficulty; }
    public void setDesiredDifficulty(Float desiredDifficulty) { this.desiredDifficulty = desiredDifficulty; }

    public String getPreferredType() { return preferredType; }
    public void setPreferredType(String preferredType) { this.preferredType = preferredType; }

    public StoryThread getFocusThread() { return focusThread; }
    public void setFocusThread(StoryThread focusThread) { this.focusThread = focusThread; }
}
