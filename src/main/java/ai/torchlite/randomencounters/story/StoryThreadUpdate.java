package ai.torchlite.randomencounters.story;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an update to a story thread from an AI-generated encounter
 */
public class StoryThreadUpdate {

    @SerializedName("thread_id")
    private String threadId;

    @SerializedName("progress_change")
    private int progressChange; // -10 to +10

    @SerializedName("narrative_update")
    private String narrativeUpdate; // What happened to advance/affect this thread

    @SerializedName("status_change")
    private ThreadStatus statusChange; // Optional: change thread status

    @SerializedName("priority_change")
    private ThreadPriority priorityChange; // Optional: change thread priority

    public StoryThreadUpdate() {}

    public StoryThreadUpdate(String threadId, int progressChange, String narrativeUpdate) {
        this.threadId = threadId;
        this.progressChange = progressChange;
        this.narrativeUpdate = narrativeUpdate;
    }

    // Getters and setters
    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public int getProgressChange() { return progressChange; }
    public void setProgressChange(int progressChange) { this.progressChange = progressChange; }

    public String getNarrativeUpdate() { return narrativeUpdate; }
    public void setNarrativeUpdate(String narrativeUpdate) { this.narrativeUpdate = narrativeUpdate; }

    public ThreadStatus getStatusChange() { return statusChange; }
    public void setStatusChange(ThreadStatus statusChange) { this.statusChange = statusChange; }

    public ThreadPriority getPriorityChange() { return priorityChange; }
    public void setPriorityChange(ThreadPriority priorityChange) { this.priorityChange = priorityChange; }
}
