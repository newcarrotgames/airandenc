package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;

import java.util.Map;

/**
 * Provides player character information
 */
public class PlayerContextSection implements PromptSection {

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return request.getPlayerName() != null;
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();

        section.append("## Player Character\n\n");
        section.append("**Name:** ").append(request.getPlayerName()).append("\n");
        section.append("**Level:** ").append(request.getPlayerLevel()).append("\n");

        // Health status
        float healthPercent = (float) ((request.getPlayerHealth() / request.getPlayerMaxHealth()) * 100.0);
        section.append("**Health:** ").append(String.format("%.0f%%", healthPercent));
        if (healthPercent < 30) {
            section.append(" (critically injured)");
        } else if (healthPercent < 60) {
            section.append(" (wounded)");
        }
        section.append("\n");

        // Equipment
        Map<String, String> equipment = request.getEquipment();
        if (equipment != null && !equipment.isEmpty()) {
            section.append("**Equipment:**\n");
            if (equipment.containsKey("mainhand")) {
                section.append("- Weapon: ").append(formatItem(equipment.get("mainhand"))).append("\n");
            }
            if (equipment.containsKey("head") || equipment.containsKey("chest") ||
                equipment.containsKey("legs") || equipment.containsKey("feet")) {
                section.append("- Armor: ");
                boolean first = true;
                for (String slot : new String[]{"head", "chest", "legs", "feet"}) {
                    if (equipment.containsKey(slot)) {
                        if (!first) section.append(", ");
                        section.append(formatItem(equipment.get(slot)));
                        first = false;
                    }
                }
                section.append("\n");
            }
        }

        // Current location
        section.append("**Location:** ").append(request.getCurrentLocation())
               .append(" (").append(request.getPosX()).append(", ")
               .append(request.getPosY()).append(", ")
               .append(request.getPosZ()).append(")\n");

        section.append("**Time:** ").append(request.getTimeOfDay())
               .append(", Weather: ").append(request.getWeather()).append("\n\n");

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 30; // Show after world/biome context
    }

    private String formatItem(String itemId) {
        // Convert "iron_sword" to "Iron Sword"
        String[] parts = itemId.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(part.substring(0, 1).toUpperCase())
                     .append(part.substring(1).toLowerCase());
        }
        return formatted.toString();
    }
}
