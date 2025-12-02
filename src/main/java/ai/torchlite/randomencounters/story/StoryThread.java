package ai.torchlite.randomencounters.story;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an ongoing narrative arc that spans multiple encounters
 */
public class StoryThread {

    @SerializedName("thread_id")
    private String threadId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    // Thread State
    @SerializedName("status")
    private ThreadStatus status;

    @SerializedName("progress_level")
    private int progressLevel; // 0-10, how far along this arc is

    @SerializedName("created_timestamp")
    private long createdTimestamp;

    @SerializedName("last_update_timestamp")
    private long lastUpdateTimestamp;

    // Narrative Elements
    @SerializedName("key_npcs")
    private List<String> keyNPCs;

    @SerializedName("key_locations")
    private List<String> keyLocations;

    @SerializedName("current_objective")
    private String currentObjective;

    @SerializedName("thread_state")
    private Map<String, String> threadState; // Arbitrary key-value state

    // Scheduling
    @SerializedName("priority")
    private ThreadPriority priority;

    @SerializedName("min_encounters_until_next")
    private Integer minEncountersUntilNext;

    @SerializedName("trigger_condition")
    private String triggerCondition; // e.g., "near_village", "night", etc.

    // AI Context
    @SerializedName("narrative_context")
    private String narrativeContext;

    public StoryThread() {
        this.keyNPCs = new ArrayList<>();
        this.keyLocations = new ArrayList<>();
        this.threadState = new HashMap<>();
        this.status = ThreadStatus.ACTIVE;
        this.priority = ThreadPriority.MEDIUM;
        this.progressLevel = 0;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUpdateTimestamp = this.createdTimestamp;
    }

    public StoryThread(String threadId, String title, String description) {
        this();
        this.threadId = threadId;
        this.title = title;
        this.description = description;
    }

    /**
     * Append new narrative context
     */
    public void appendNarrativeContext(String addition) {
        if (this.narrativeContext == null || this.narrativeContext.isEmpty()) {
            this.narrativeContext = addition;
        } else {
            this.narrativeContext = this.narrativeContext + " " + addition;
        }
    }

    // Getters and setters
    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ThreadStatus getStatus() { return status; }
    public void setStatus(ThreadStatus status) { this.status = status; }

    public int getProgressLevel() { return progressLevel; }
    public void setProgressLevel(int progressLevel) {
        this.progressLevel = Math.max(0, Math.min(10, progressLevel));
    }

    public long getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(long createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public long getLastUpdateTimestamp() { return lastUpdateTimestamp; }
    public void setLastUpdateTimestamp(long lastUpdateTimestamp) { this.lastUpdateTimestamp = lastUpdateTimestamp; }

    public List<String> getKeyNPCs() { return keyNPCs; }
    public void setKeyNPCs(List<String> keyNPCs) { this.keyNPCs = keyNPCs; }

    public List<String> getKeyLocations() { return keyLocations; }
    public void setKeyLocations(List<String> keyLocations) { this.keyLocations = keyLocations; }

    public String getCurrentObjective() { return currentObjective; }
    public void setCurrentObjective(String currentObjective) { this.currentObjective = currentObjective; }

    public Map<String, String> getThreadState() { return threadState; }
    public void setThreadState(Map<String, String> threadState) { this.threadState = threadState; }

    public ThreadPriority getPriority() { return priority; }
    public void setPriority(ThreadPriority priority) { this.priority = priority; }

    public Integer getMinEncountersUntilNext() { return minEncountersUntilNext; }
    public void setMinEncountersUntilNext(Integer minEncountersUntilNext) {
        this.minEncountersUntilNext = minEncountersUntilNext;
    }

    public String getTriggerCondition() { return triggerCondition; }
    public void setTriggerCondition(String triggerCondition) { this.triggerCondition = triggerCondition; }

    public String getNarrativeContext() { return narrativeContext; }
    public void setNarrativeContext(String narrativeContext) { this.narrativeContext = narrativeContext; }
}
