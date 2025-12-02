package ai.torchlite.randomencounters.context;

import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.story.PlayerStoryState;
import ai.torchlite.randomencounters.story.StoryStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enriches encounter requests with detailed game context
 */
public class ContextEnrichmentEngine {

    private static final int NOTABLE_ITEM_RARITY_THRESHOLD = 2; // Uncommon and above
    private static final int MAX_NOTABLE_ITEMS = 10;
    private static final double NEARBY_PLAYER_RADIUS = 32.0;

    /**
     * Build a complete StorytellingRequest from player and world context
     */
    public StorytellingRequest buildStorytellingRequest(EntityPlayer player, World world) {
        StorytellingRequest request = new StorytellingRequest();

        // Basic player context
        enrichPlayerContext(request, player);

        // Location context
        enrichLocationContext(request, player, world);

        // Story state context
        enrichStoryContext(request, player);

        // Inventory context
        enrichInventoryContext(request, player);

        // Social context
        enrichSocialContext(request, player, world);

        return request;
    }

    /**
     * Enrich with basic player information
     */
    private void enrichPlayerContext(StorytellingRequest request, EntityPlayer player) {
        request.setPlayerName(player.getName());
        request.setPlayerUUID(player.getUniqueID().toString());
        request.setPlayerLevel(player.experienceLevel);
        request.setPlayerHealth(player.getHealth());
        request.setPlayerMaxHealth(player.getMaxHealth());
    }

    /**
     * Enrich with location and environmental context
     */
    private void enrichLocationContext(StorytellingRequest request, EntityPlayer player, World world) {
        BlockPos pos = player.getPosition();
        Biome biome = world.getBiome(pos);

        request.setBiome(biome.getBiomeName());
        request.setPosX(pos.getX());
        request.setPosY(pos.getY());
        request.setPosZ(pos.getZ());
        request.setDimension(getDimensionName(world.provider.getDimension()));

        // Time of day
        request.setTimeOfDay(getTimeOfDay(world.getWorldTime()));

        // Weather
        request.setWeather(getWeatherDescription(world));

        // Check for nearby structures
        request.setNearStructure(isNearStructure(player, world));
        request.setCurrentLocation(detectNamedLocation(player, world));

        // Local difficulty
        request.setLocalDifficultyRating(calculateLocalDifficulty(player, world));
    }

    /**
     * Enrich with story state information
     */
    private void enrichStoryContext(StorytellingRequest request, EntityPlayer player) {
        StoryStateManager manager = StoryStateManager.getInstance();
        if (manager == null) {
            return;
        }

        PlayerStoryState state = manager.getOrCreateState(player);

        // Add recent encounters (last 5-10)
        int recentCount = Math.min(10, state.getEncounterHistory().size());
        if (recentCount > 0) {
            List<ai.torchlite.randomencounters.story.EncounterSummary> recent =
                state.getEncounterHistory().subList(
                    state.getEncounterHistory().size() - recentCount,
                    state.getEncounterHistory().size()
                );
            request.setRecentEncounters(new ArrayList<>(recent));
        }

        // Add active story threads
        request.setActiveThreads(state.getActiveThreadsList());

        // Add faction reputation
        request.setFactionReputation(new HashMap<>(state.getFactionReputation()));

        // Add player traits
        request.setPlayerTraits(new HashMap<>(state.getPlayerTraits()));

        // Generate narrative summary
        request.setNarrativeSummary(generateNarrativeSummary(state));
    }

    /**
     * Enrich with inventory and equipment context
     */
    private void enrichInventoryContext(StorytellingRequest request, EntityPlayer player) {
        List<String> notableItems = new ArrayList<>();
        Map<String, String> equipment = new HashMap<>();

        // Scan main inventory for notable items
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && isNotableItem(stack)) {
                notableItems.add(getItemDescription(stack));
                if (notableItems.size() >= MAX_NOTABLE_ITEMS) {
                    break;
                }
            }
        }

        // Get equipped items
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty()) {
                String slot = getArmorSlot(player.inventory.armorInventory.indexOf(stack));
                equipment.put(slot, getItemDescription(stack));
            }
        }

        // Main hand and offhand
        ItemStack mainHand = player.getHeldItemMainhand();
        if (!mainHand.isEmpty()) {
            equipment.put("mainhand", getItemDescription(mainHand));
        }

        ItemStack offHand = player.getHeldItemOffhand();
        if (!offHand.isEmpty()) {
            equipment.put("offhand", getItemDescription(offHand));
        }

        request.setNotableItems(notableItems);
        request.setEquipment(equipment);
    }

    /**
     * Enrich with social context (nearby players)
     */
    private void enrichSocialContext(StorytellingRequest request, EntityPlayer player, World world) {
        List<String> nearbyPlayers = new ArrayList<>();

        for (EntityPlayer other : world.playerEntities) {
            if (other != player && player.getDistanceToEntity(other) <= NEARBY_PLAYER_RADIUS) {
                nearbyPlayers.add(other.getName());
            }
        }

        request.setNearbyPlayerNames(nearbyPlayers);
    }

    /**
     * Generate a compressed narrative summary from player history
     */
    private String generateNarrativeSummary(PlayerStoryState state) {
        if (state.getEncounterHistory().isEmpty()) {
            return "A newcomer to the wasteland, their story yet to be written.";
        }

        // TODO: Implement smarter summarization
        // For now, return a basic summary
        int totalEncounters = state.getEncounterHistory().size();
        int activeThreads = state.getActiveThreadsList().size();

        return String.format(
            "Survivor with %d recorded encounters. Currently pursuing %d story threads.",
            totalEncounters,
            activeThreads
        );
    }

    /**
     * Check if item is notable (rare, unique, or quest-related)
     */
    private boolean isNotableItem(ItemStack stack) {
        // Check rarity
        if (stack.getRarity().ordinal() >= NOTABLE_ITEM_RARITY_THRESHOLD) {
            return true;
        }

        // Check for enchantments
        if (stack.isItemEnchanted()) {
            return true;
        }

        // Check for custom name
        if (stack.hasDisplayName()) {
            return true;
        }

        return false;
    }

    /**
     * Get a description of an item
     */
    private String getItemDescription(ItemStack stack) {
        String name = stack.getDisplayName();
        int count = stack.getCount();

        if (count > 1) {
            return name + " x" + count;
        }
        return name;
    }

    /**
     * Get armor slot name
     */
    private String getArmorSlot(int index) {
        switch (index) {
            case 0: return "feet";
            case 1: return "legs";
            case 2: return "chest";
            case 3: return "head";
            default: return "armor_" + index;
        }
    }

    /**
     * Get time of day description
     */
    private String getTimeOfDay(long worldTime) {
        long time = worldTime % 24000;
        if (time < 6000) return "dawn";
        if (time < 12000) return "day";
        if (time < 18000) return "dusk";
        return "night";
    }

    /**
     * Get weather description
     */
    private String getWeatherDescription(World world) {
        if (world.isThundering()) return "storm";
        if (world.isRaining()) return "rain";
        return "clear";
    }

    /**
     * Get dimension name
     */
    private String getDimensionName(int dimension) {
        switch (dimension) {
            case 0: return "overworld";
            case -1: return "nether";
            case 1: return "end";
            default: return "dimension_" + dimension;
        }
    }

    /**
     * Check if player is near a structure
     * TODO: Implement proper structure detection
     */
    private boolean isNearStructure(EntityPlayer player, World world) {
        // Placeholder - would need to check for nearby structures
        return false;
    }

    /**
     * Detect named location if near one
     * TODO: Implement location detection based on structures or custom data
     */
    private String detectNamedLocation(EntityPlayer player, World world) {
        // Placeholder - would integrate with world data
        return null;
    }

    /**
     * Calculate local difficulty rating (0.0-1.0)
     */
    private float calculateLocalDifficulty(EntityPlayer player, World world) {
        // Base on world difficulty and player distance from spawn
        float base = world.getDifficulty().getDifficultyId() / 3.0f;

        // Increase with distance from spawn
        BlockPos spawn = world.getSpawnPoint();
        double distance = player.getDistance(spawn.getX(), spawn.getY(), spawn.getZ());
        float distanceFactor = Math.min(1.0f, (float) (distance / 10000.0)); // Max at 10k blocks

        // Combine factors
        return Math.min(1.0f, base * 0.5f + distanceFactor * 0.5f);
    }
}
