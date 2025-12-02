package ai.torchlite.randomencounters.encounter;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the structure of an AI-generated encounter
 */
public class EncounterData {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type; // "combat", "social", "mystery", "trade", "environmental"

    @SerializedName("difficulty")
    private float difficulty; // 0.0-1.0

    @SerializedName("narrative_text")
    private String narrativeText;

    @SerializedName("entities")
    private List<EncounterEntity> entities;

    @SerializedName("dialogue")
    private List<DialogueOption> dialogue;

    @SerializedName("rewards")
    private EncounterRewards rewards;

    @SerializedName("outcomes")
    private EncounterOutcomes outcomes;

    public EncounterData() {
        this.entities = new ArrayList<>();
        this.dialogue = new ArrayList<>();
    }

    // Nested classes for structure
    public static class EncounterEntity {
        @SerializedName("entity_type")
        private String entityType; // "zombie", "skeleton", "villager", etc.

        @SerializedName("name")
        private String name;

        @SerializedName("count")
        private int count = 1;

        @SerializedName("equipment")
        private List<String> equipment;

        @SerializedName("hostile")
        private boolean hostile = true;

        @SerializedName("health_modifier")
        private float healthModifier = 1.0f;

        @SerializedName("damage_modifier")
        private float damageModifier = 1.0f;

        public EncounterEntity() {
            this.equipment = new ArrayList<>();
        }

        // Getters
        public String getEntityType() { return entityType; }
        public String getName() { return name; }
        public int getCount() { return count; }
        public List<String> getEquipment() { return equipment; }
        public boolean isHostile() { return hostile; }
        public float getHealthModifier() { return healthModifier; }
        public float getDamageModifier() { return damageModifier; }
    }

    public static class DialogueOption {
        @SerializedName("speaker")
        private String speaker;

        @SerializedName("text")
        private String text;

        @SerializedName("choices")
        private List<String> choices;

        public DialogueOption() {
            this.choices = new ArrayList<>();
        }

        // Getters
        public String getSpeaker() { return speaker; }
        public String getText() { return text; }
        public List<String> getChoices() { return choices; }
    }

    public static class EncounterRewards {
        @SerializedName("experience")
        private int experience;

        @SerializedName("items")
        private List<String> items;

        @SerializedName("faction_changes")
        private java.util.Map<String, Integer> factionChanges;

        public EncounterRewards() {
            this.items = new ArrayList<>();
            this.factionChanges = new java.util.HashMap<>();
        }

        // Getters
        public int getExperience() { return experience; }
        public List<String> getItems() { return items; }
        public java.util.Map<String, Integer> getFactionChanges() { return factionChanges; }
    }

    public static class EncounterOutcomes {
        @SerializedName("victory_text")
        private String victoryText;

        @SerializedName("defeat_text")
        private String defeatText;

        @SerializedName("flee_text")
        private String fleeText;

        @SerializedName("negotiate_text")
        private String negotiateText;

        // Getters
        public String getVictoryText() { return victoryText; }
        public String getDefeatText() { return defeatText; }
        public String getFleeText() { return fleeText; }
        public String getNegotiateText() { return negotiateText; }
    }

    // Main getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public float getDifficulty() { return difficulty; }
    public void setDifficulty(float difficulty) { this.difficulty = difficulty; }

    public String getNarrativeText() { return narrativeText; }
    public void setNarrativeText(String narrativeText) { this.narrativeText = narrativeText; }

    public List<EncounterEntity> getEntities() { return entities; }
    public void setEntities(List<EncounterEntity> entities) { this.entities = entities; }

    public List<DialogueOption> getDialogue() { return dialogue; }
    public void setDialogue(List<DialogueOption> dialogue) { this.dialogue = dialogue; }

    public EncounterRewards getRewards() { return rewards; }
    public void setRewards(EncounterRewards rewards) { this.rewards = rewards; }

    public EncounterOutcomes getOutcomes() { return outcomes; }
    public void setOutcomes(EncounterOutcomes outcomes) { this.outcomes = outcomes; }
}
