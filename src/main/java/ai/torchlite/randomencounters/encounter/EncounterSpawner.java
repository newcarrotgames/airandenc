package ai.torchlite.randomencounters.encounter;

import ai.torchlite.randomencounters.RandomEncounters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles spawning of encounter entities in the world
 */
public class EncounterSpawner {

    private static final int SPAWN_RADIUS_MIN = 8;
    private static final int SPAWN_RADIUS_MAX = 16;
    private static final int MAX_SPAWN_ATTEMPTS = 10;

    private final Random random = new Random();

    /**
     * Spawn all entities for an encounter near a player
     *
     * @param encounter The encounter data
     * @param player The player to spawn near
     * @param world The world to spawn in
     * @return List of spawned entities
     */
    public List<Entity> spawnEncounter(EncounterData encounter, EntityPlayer player, World world) {
        List<Entity> spawnedEntities = new ArrayList<>();

        if (encounter.getEntities() == null || encounter.getEntities().isEmpty()) {
            RandomEncounters.LOGGER.warn("No entities to spawn for encounter: " + encounter.getTitle());
            return spawnedEntities;
        }

        BlockPos playerPos = player.getPosition();

        for (EncounterData.EncounterEntity entityData : encounter.getEntities()) {
            for (int i = 0; i < entityData.getCount(); i++) {
                Entity entity = spawnEntity(entityData, playerPos, world);
                if (entity != null) {
                    spawnedEntities.add(entity);
                    RandomEncounters.LOGGER.info("Spawned entity: " + entityData.getEntityType() +
                        " at " + entity.getPosition());
                } else {
                    RandomEncounters.LOGGER.warn("Failed to spawn entity: " + entityData.getEntityType());
                }
            }
        }

        return spawnedEntities;
    }

    /**
     * Spawn a single entity
     */
    private Entity spawnEntity(EncounterData.EncounterEntity entityData, BlockPos nearPos, World world) {
        // Try to find a valid spawn location
        BlockPos spawnPos = findSpawnLocation(nearPos, world);
        if (spawnPos == null) {
            return null;
        }

        // Create the entity
        Entity entity = createEntity(entityData.getEntityType(), world);
        if (entity == null) {
            RandomEncounters.LOGGER.error("Unknown entity type: " + entityData.getEntityType());
            return null;
        }

        // Set position
        entity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

        // Configure entity if it's a living entity
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;

            // Set custom name if provided
            if (entityData.getName() != null && !entityData.getName().isEmpty()) {
                living.setCustomNameTag(entityData.getName());
                living.setAlwaysRenderNameTag(true);
            }

            // Apply health modifier
            if (entityData.getHealthModifier() != 1.0f) {
                float maxHealth = living.getMaxHealth();
                living.setHealth(maxHealth * entityData.getHealthModifier());
            }

            // Equip items
            if (entityData.getEquipment() != null && !entityData.getEquipment().isEmpty()) {
                equipEntity(living, entityData.getEquipment());
            }

            // Set persistence
            living.enablePersistence();
        }

        // Spawn the entity
        world.spawnEntity(entity);

        return entity;
    }

    /**
     * Find a valid spawn location near the target position
     */
    private BlockPos findSpawnLocation(BlockPos target, World world) {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            int distance = SPAWN_RADIUS_MIN + random.nextInt(SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN);
            double angle = random.nextDouble() * Math.PI * 2;

            int x = target.getX() + (int) (Math.cos(angle) * distance);
            int z = target.getZ() + (int) (Math.sin(angle) * distance);
            int y = world.getHeight(x, z);

            BlockPos pos = new BlockPos(x, y, z);

            // Check if position is valid (solid block below, air above)
            if (world.getBlockState(pos.down()).isFullBlock() &&
                world.isAirBlock(pos) &&
                world.isAirBlock(pos.up())) {
                return pos;
            }
        }

        RandomEncounters.LOGGER.warn("Could not find valid spawn location near " + target);
        return null;
    }

    /**
     * Create an entity by type name
     */
    private Entity createEntity(String entityType, World world) {
        // Normalize entity type (add minecraft: prefix if needed)
        String normalizedType = entityType;
        if (!entityType.contains(":")) {
            normalizedType = "minecraft:" + entityType.toLowerCase();
        }

        ResourceLocation entityId = new ResourceLocation(normalizedType);

        try {
            return EntityList.createEntityByIDFromName(entityId, world);
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to create entity: " + entityType, e);
            return null;
        }
    }

    /**
     * Equip an entity with items
     */
    private void equipEntity(EntityLiving entity, List<String> equipmentNames) {
        for (String itemName : equipmentNames) {
            ItemStack stack = createItemStack(itemName);
            if (stack != null && !stack.isEmpty()) {
                // Try to determine appropriate slot
                EntityEquipmentSlot slot = determineEquipmentSlot(stack);
                entity.setItemStackToSlot(slot, stack);
                entity.setDropChance(slot, 0.5f); // 50% chance to drop on death
            }
        }
    }

    /**
     * Create an ItemStack from a string name
     */
    private ItemStack createItemStack(String itemName) {
        try {
            // Normalize item name
            String normalizedName = itemName;
            if (!itemName.contains(":")) {
                normalizedName = "minecraft:" + itemName.toLowerCase().replace(" ", "_");
            }

            Item item = Item.getByNameOrId(normalizedName);
            if (item != null) {
                return new ItemStack(item);
            }
        } catch (Exception e) {
            RandomEncounters.LOGGER.warn("Failed to create item: " + itemName, e);
        }
        return null;
    }

    /**
     * Determine which equipment slot an item should go in
     */
    private EntityEquipmentSlot determineEquipmentSlot(ItemStack stack) {
        Item item = stack.getItem();
        String itemName = item.getRegistryName().toString().toLowerCase();

        // Armor detection
        if (itemName.contains("helmet") || itemName.contains("_helmet")) {
            return EntityEquipmentSlot.HEAD;
        }
        if (itemName.contains("chestplate") || itemName.contains("_chestplate")) {
            return EntityEquipmentSlot.CHEST;
        }
        if (itemName.contains("leggings") || itemName.contains("_leggings")) {
            return EntityEquipmentSlot.LEGS;
        }
        if (itemName.contains("boots") || itemName.contains("_boots")) {
            return EntityEquipmentSlot.FEET;
        }

        // Default to main hand for weapons/tools
        return EntityEquipmentSlot.MAINHAND;
    }

    /**
     * Clear/remove all entities from an encounter
     */
    public void despawnEncounter(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity != null && !entity.isDead) {
                entity.setDead();
            }
        }
    }
}
