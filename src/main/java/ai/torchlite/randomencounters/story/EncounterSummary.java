package ai.torchlite.randomencounters.story;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compressed record of a past encounter for player history
 */
public class EncounterSummary {

    @SerializedName("encounter_id")
    private String encounterId;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("encounter_type")
    private String encounterType; // "combat", "social", "mystery", "trade"

    @SerializedName("brief_description")
    private String briefDescription; // 1-2 sentences

    @SerializedName("outcome")
    private String outcome; // "victory", "fled", "negotiated", "failed"

    @SerializedName("key_entities")
    private List<String> keyEntities; // Notable NPCs or entities

    @SerializedName("key_choices")
    private List<String> keyChoices; // Important decisions made

    @SerializedName("story_impact")
    private Map<String, String> storyImpact; // threadId -> impact description

    public EncounterSummary() {
        this.keyEntities = new ArrayList<>();
        this.keyChoices = new ArrayList<>();
        this.storyImpact = new HashMap<>();
    }

    public EncounterSummary(String encounterId, String encounterType, String briefDescription, String outcome) {
        this();
        this.encounterId = encounterId;
        this.timestamp = System.currentTimeMillis();
        this.encounterType = encounterType;
        this.briefDescription = briefDescription;
        this.outcome = outcome;
    }

    // Getters and setters
    public String getEncounterId() { return encounterId; }
    public void setEncounterId(String encounterId) { this.encounterId = encounterId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getEncounterType() { return encounterType; }
    public void setEncounterType(String encounterType) { this.encounterType = encounterType; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public List<String> getKeyEntities() { return keyEntities; }
    public void setKeyEntities(List<String> keyEntities) { this.keyEntities = keyEntities; }

    public List<String> getKeyChoices() { return keyChoices; }
    public void setKeyChoices(List<String> keyChoices) { this.keyChoices = keyChoices; }

    public Map<String, String> getStoryImpact() { return storyImpact; }
    public void setStoryImpact(Map<String, String> storyImpact) { this.storyImpact = storyImpact; }
}
