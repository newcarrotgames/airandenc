package ai.torchlite.randomencounters.ai.prompt.sections;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.ai.prompt.PromptSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides biome-specific context including factions, threats, and atmosphere
 */
public class BiomeContextSection implements PromptSection {

    private static final Map<String, BiomeInfo> BIOME_DATA = new HashMap<>();

    static {
        // Wasteland biomes
        BIOME_DATA.put("Wasteland", new BiomeInfo(
            "A desolate expanse of cracked earth and toxic fog",
            new String[]{"Wasteland Raiders", "Blight Cultists", "Nomad Traders"},
            new String[]{"Irradiated zones", "Toxic gas pockets", "Collapsing structures"},
            "grimdark, survival horror"
        ));

        BIOME_DATA.put("Blighted Forest", new BiomeInfo(
            "Twisted trees ooze with Blight, mutated creatures lurk in shadows",
            new String[]{"Forest Mutants", "Desperate Survivors", "Research Remnants"},
            new String[]{"Blight spores", "Mutated wildlife", "Unstable ground"},
            "eerie, oppressive"
        ));

        BIOME_DATA.put("Ruined City", new BiomeInfo(
            "Crumbling skyscrapers and flooded streets, echoes of the old world",
            new String[]{"Urban Scavengers", "Military Remnants", "Underground Network"},
            new String[]{"Structural collapse", "Flooded areas", "Hostile territories"},
            "melancholic, tense"
        ));

        BIOME_DATA.put("Ashen Plains", new BiomeInfo(
            "Endless grey dust, occasional lightning storms, minimal shelter",
            new String[]{"Storm Chasers", "Ash Walkers", "Desperate Exiles"},
            new String[]{"Lightning storms", "Ash storms", "Extreme exposure"},
            "harsh, unforgiving"
        ));

        BIOME_DATA.put("Frozen Wastes", new BiomeInfo(
            "Perpetual winter, ice-covered ruins, scarce resources",
            new String[]{"Ice Hunters", "Bunker Survivors", "Frost Nomads"},
            new String[]{"Extreme cold", "Ice collapse", "Resource scarcity"},
            "isolating, desperate"
        ));

        BIOME_DATA.put("Toxic Swamp", new BiomeInfo(
            "Bubbling pools of chemicals, mutated flora, heavy mist",
            new String[]{"Swamp Dwellers", "Mutation Cults", "Reclusive Healers"},
            new String[]{"Toxic water", "Poisonous plants", "Poor visibility"},
            "oppressive, dangerous"
        ));
    }

    @Override
    public boolean isApplicable(StorytellingRequest request) {
        return request.getBiome() != null && !request.getBiome().isEmpty();
    }

    @Override
    public String buildSection(StorytellingRequest request) {
        StringBuilder section = new StringBuilder();
        String biome = request.getBiome();

        section.append("## Biome Context: ").append(biome).append("\n\n");

        BiomeInfo info = BIOME_DATA.get(biome);
        if (info != null) {
            section.append("**Environment:** ").append(info.description).append("\n\n");

            section.append("**Local Factions:**\n");
            for (String faction : info.factions) {
                section.append("- ").append(faction).append("\n");
            }
            section.append("\n");

            section.append("**Environmental Hazards:**\n");
            for (String hazard : info.hazards) {
                section.append("- ").append(hazard).append("\n");
            }
            section.append("\n");

            section.append("**Atmosphere:** ").append(info.atmosphere).append("\n\n");
        } else {
            // Generic biome info
            section.append("**Environment:** ").append(biome)
                   .append(" - A region shaped by the Blight's corruption\n\n");
        }

        return section.toString();
    }

    @Override
    public int getPriority() {
        return 20; // Show early, after world context but before player info
    }

    /**
     * Data container for biome information
     */
    private static class BiomeInfo {
        final String description;
        final String[] factions;
        final String[] hazards;
        final String atmosphere;

        BiomeInfo(String description, String[] factions, String[] hazards, String atmosphere) {
            this.description = description;
            this.factions = factions;
            this.hazards = hazards;
            this.atmosphere = atmosphere;
        }
    }
}
