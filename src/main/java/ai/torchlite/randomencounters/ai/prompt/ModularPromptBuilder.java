package ai.torchlite.randomencounters.ai.prompt;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.sections.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Builds prompts from modular, composable sections
 */
public class ModularPromptBuilder {

    private final List<PromptSection> sections;

    public ModularPromptBuilder() {
        this.sections = new ArrayList<>();
        registerDefaultSections();
    }

    /**
     * Register all default prompt sections
     */
    private void registerDefaultSections() {
        sections.add(new WorldContextSection());
        sections.add(new BiomeContextSection());
        sections.add(new PlayerContextSection());
        sections.add(new StoryHistorySection());
        sections.add(new DifficultySection());
        sections.add(new TaskInstructionsSection());
    }

    /**
     * Add a custom section to the builder
     */
    public void addSection(PromptSection section) {
        sections.add(section);
    }

    /**
     * Remove a section by class type
     */
    public void removeSection(Class<? extends PromptSection> sectionClass) {
        sections.removeIf(s -> s.getClass().equals(sectionClass));
    }

    /**
     * Build the complete prompt for story generation (Step 1)
     */
    public String buildStoryPrompt(StorytellingRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Generate Random Encounter\n\n");

        // Sort sections by priority
        List<PromptSection> applicableSections = new ArrayList<>();
        for (PromptSection section : sections) {
            if (section.isApplicable(request)) {
                applicableSections.add(section);
            }
        }
        applicableSections.sort(Comparator.comparingInt(PromptSection::getPriority));

        // Build each applicable section
        for (PromptSection section : applicableSections) {
            prompt.append(section.buildSection(request));
        }

        return prompt.toString();
    }

    /**
     * Build the conversion prompt for transforming story to JSON (Step 2)
     */
    public String buildConversionPrompt(String narrativeStory) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Task: Convert Encounter Story to Game JSON\n\n");
        prompt.append("Convert the following encounter narrative into structured JSON format for a Minecraft mod.\n\n");
        prompt.append("## Original Story:\n\n");
        prompt.append(narrativeStory);
        prompt.append("\n\n");

        prompt.append("## Required JSON Structure:\n\n");
        prompt.append("Return ONLY valid JSON (no markdown, no explanations) with this structure:\n\n");
        prompt.append("{\n");
        prompt.append("  \"encounter_json\": \"{...}\",\n");
        prompt.append("  \"story_updates\": {...}\n");
        prompt.append("}\n\n");

        prompt.append("### encounter_json field (must be an ESCAPED JSON string):\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"Encounter Title\",\n");
        prompt.append("  \"type\": \"combat|exploration|social|trade|mystery\",\n");
        prompt.append("  \"description\": \"Brief one-sentence summary\",\n");
        prompt.append("  \"narrative_text\": \"Full narrative for the player\",\n");
        prompt.append("  \"entities\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"entity_type\": \"zombie|skeleton|spider|creeper|enderman|witch|villager|etc\",\n");
        prompt.append("      \"name\": \"Custom name\",\n");
        prompt.append("      \"count\": 1,\n");
        prompt.append("      \"hostile\": true,\n");
        prompt.append("      \"health_modifier\": 1.0,\n");
        prompt.append("      \"damage_modifier\": 1.0,\n");
        prompt.append("      \"equipment\": [\"item1\", \"item2\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"dialogue\": [\n");
        prompt.append("    {\"speaker\": \"NPC Name\", \"text\": \"What they say\"}\n");
        prompt.append("  ],\n");
        prompt.append("  \"outcomes\": {\n");
        prompt.append("    \"victory_text\": \"Success message\",\n");
        prompt.append("    \"defeat_text\": \"Failure message\",\n");
        prompt.append("    \"flee_text\": \"Escape message\"\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");

        prompt.append("### story_updates field:\n");
        prompt.append("{\n");
        prompt.append("  \"encounter_summary\": \"One-line summary\",\n");
        prompt.append("  \"thread_updates\": [],\n");
        prompt.append("  \"new_threads\": [],\n");
        prompt.append("  \"key_choices\": []\n");
        prompt.append("}\n\n");

        prompt.append("CRITICAL:\n");
        prompt.append("- encounter_json must be a STRING with escaped quotes (\\\")\n");
        prompt.append("- Use only valid Minecraft entity types (zombie, skeleton, spider, creeper, villager, etc.)\n");
        prompt.append("- Return ONLY the JSON, nothing else\n");

        return prompt.toString();
    }
}
