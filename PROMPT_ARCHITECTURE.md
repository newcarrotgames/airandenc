# Modular Prompt Architecture

## Overview

The encounter generation system uses a modular, composable prompt architecture that makes it easy to add, remove, or customize contextual information without managing large string concatenations.

## Core Components

### PromptSection Interface
All prompt sections implement this interface:
```java
public interface PromptSection {
    boolean isApplicable(StorytellingRequest request);
    String buildSection(StorytellingRequest request);
    int getPriority(); // Lower = earlier in prompt
}
```

### ModularPromptBuilder
Orchestrates all sections and builds the final prompt:
```java
ModularPromptBuilder builder = new ModularPromptBuilder();
String prompt = builder.buildStoryPrompt(request);
```

## Built-in Sections

### 1. WorldContextSection (Priority: 10)
- Dregora world lore and setting
- The Blight corruption background
- Tone and atmosphere
- Controlled by `ConfigHandler.enableDregoraLore`

### 2. BiomeContextSection (Priority: 20)
- **Biome-specific context** including factions, hazards, atmosphere
- **Faction filtering**: Only shows factions that exist in the current biome
- Supports: Wasteland, Blighted Forest, Ruined City, Ashen Plains, Frozen Wastes, Toxic Swamp
- Falls back to generic description for unknown biomes

Example output:
```markdown
## Biome Context: Wasteland

**Environment:** A desolate expanse of cracked earth and toxic fog

**Local Factions:**
- Wasteland Raiders
- Blight Cultists
- Nomad Traders

**Environmental Hazards:**
- Irradiated zones
- Toxic gas pockets
- Collapsing structures

**Atmosphere:** grimdark, survival horror
```

### 3. PlayerContextSection (Priority: 30)
- Player name, level, health status
- Equipment (weapon, armor)
- Current location and coordinates
- Time of day and weather

### 4. StoryHistorySection (Priority: 40)
- Player's narrative summary
- Only included if narrative exists

### 5. DifficultySection (Priority: 50)
- Local difficulty rating
- Balance guidelines for entity count and strength
- Scales recommendations based on difficulty level

### 6. TaskInstructionsSection (Priority: 90)
- Core AI task instructions
- Response format requirements
- Always included

## Adding Custom Sections

### Example: Time-of-Day Section
```java
public class TimeOfDaySection implements PromptSection {
    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return request.getTimeOfDay() != null;
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();
        String timeOfDay = request.getTimeOfDay();

        section.append("## Time Context\n\n");
        section.append("**Current Time:** ").append(timeOfDay).append("\n");

        if (timeOfDay.equals("night")) {
            section.append("- Visibility is limited\n");
            section.append("- Hostile creatures more active\n");
        }

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 35; // After player context
    }
}
```

### Register Custom Section
```java
NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
ModularPromptBuilder modular = promptBuilder.getModularBuilder();
modular.addSection(new TimeOfDaySection());
```

## Extending Biome Data

To add a new biome with faction information:

```java
// In BiomeContextSection.java static initializer:
BIOME_DATA.put("Crimson Desert", new BiomeInfo(
    "Blood-red sands hide ancient secrets beneath",
    new String[]{"Desert Nomads", "Sand Cultists", "Ruin Seekers"},
    new String[]{"Sandstorms", "Heat exhaustion", "Buried traps"},
    "mysterious, oppressive heat"
));
```

## Usage

### Default (Modular) Mode
```java
NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
String prompt = promptBuilder.buildPrompt(request);
// Uses modular architecture by default
```

### Legacy Mode
```java
NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
promptBuilder.setUseModularBuilder(false);
String prompt = promptBuilder.buildPrompt(request);
// Uses original monolithic builder
```

### Customization
```java
NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
ModularPromptBuilder modular = promptBuilder.getModularBuilder();

// Add custom sections
modular.addSection(new WeatherEffectsSection());
modular.addSection(new FactionReputationSection());

// Remove unwanted sections
modular.removeSection(StoryHistorySection.class);

String prompt = promptBuilder.buildPrompt(request);
```

## Benefits

1. **Maintainability**: Each section is self-contained and testable
2. **Flexibility**: Easy to add/remove context without touching other sections
3. **Faction Control**: Biomes automatically limit which factions can appear
4. **Conditional Logic**: Sections only appear when applicable
5. **Priority Ordering**: Sections appear in logical order
6. **No String Concatenation Hell**: Clean, modular code

## Future Extensions

Consider adding sections for:
- Player reputation with factions
- Active quests or objectives
- Weather effects and seasonal variations
- Player's inventory highlights
- Nearby points of interest
- Recent deaths or failures
- Party members (multiplayer)
- Custom lore from data packs
