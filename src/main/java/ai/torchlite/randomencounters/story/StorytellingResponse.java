package ai.torchlite.randomencounters.story;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI response containing both encounter JSON and story updates
 */
public class StorytellingResponse {

    // The generated encounter (standard JSON format)
    @SerializedName("encounter_json")
    private String encounterJson;

    // Story Updates
    @SerializedName("story_updates")
    private StoryUpdates storyUpdates;

    public StorytellingResponse() {
        this.storyUpdates = new StoryUpdates();
    }

    public static class StoryUpdates {
        @SerializedName("encounter_summary")
        private String encounterSummary;

        @SerializedName("thread_updates")
        private List<StoryThreadUpdate> threadUpdates;

        @SerializedName("new_threads")
        private List<StoryThread> newThreads;

        @SerializedName("key_choices")
        private List<String> keyChoices;

        public StoryUpdates() {
            this.threadUpdates = new ArrayList<>();
            this.newThreads = new ArrayList<>();
            this.keyChoices = new ArrayList<>();
        }

        public String getEncounterSummary() { return encounterSummary; }
        public void setEncounterSummary(String encounterSummary) { this.encounterSummary = encounterSummary; }

        public List<StoryThreadUpdate> getThreadUpdates() { return threadUpdates; }
        public void setThreadUpdates(List<StoryThreadUpdate> threadUpdates) { this.threadUpdates = threadUpdates; }

        public List<StoryThread> getNewThreads() { return newThreads; }
        public void setNewThreads(List<StoryThread> newThreads) { this.newThreads = newThreads; }

        public List<String> getKeyChoices() { return keyChoices; }
        public void setKeyChoices(List<String> keyChoices) { this.keyChoices = keyChoices; }
    }

    // Getters and setters
    public String getEncounterJson() { return encounterJson; }
    public void setEncounterJson(String encounterJson) { this.encounterJson = encounterJson; }

    public StoryUpdates getStoryUpdates() { return storyUpdates; }
    public void setStoryUpdates(StoryUpdates storyUpdates) { this.storyUpdates = storyUpdates; }
}
