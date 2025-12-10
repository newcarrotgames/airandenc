package ai.torchlite.randomencounters.entity;

import ai.torchlite.randomencounters.RandomEncounters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;

/**
 * Discovers and categorizes all available entities for encounter generation
 */
public class EntityRegistry {

    private final List<String> allEntities = new ArrayList<>();
    private final List<String> hostileEntities = new ArrayList<>();
    private final List<String> neutralEntities = new ArrayList<>();
    private final List<String> passiveEntities = new ArrayList<>();
    private final List<String> bossEntities = new ArrayList<>();
    private final Map<String, String> entityDescriptions = new HashMap<>();

    private static EntityRegistry instance;

    private EntityRegistry() {
        discoverEntities();
    }

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }
        return instance;
    }

    /**
     * Discover all registered entities in the game
     */
    private void discoverEntities() {
        RandomEncounters.LOGGER.info("Discovering available entities...");

        Set<ResourceLocation> entityKeys = EntityList.getEntityNameList();

        for (ResourceLocation entityId : entityKeys) {
            String entityName = entityId.toString();

            // Try to determine entity category by class type
            Class<? extends Entity> entityClass = EntityList.getClass(entityId);

            if (entityClass == null) {
                continue;
            }

            // Add to main list
            allEntities.add(entityName);

            // Categorize entity
            categorizeEntity(entityName, entityClass);
        }

        // Sort all lists alphabetically
        Collections.sort(allEntities);
        Collections.sort(hostileEntities);
        Collections.sort(neutralEntities);
        Collections.sort(passiveEntities);
        Collections.sort(bossEntities);

        RandomEncounters.LOGGER.info("Entity discovery complete:");
        RandomEncounters.LOGGER.info("  Total entities: " + allEntities.size());
        RandomEncounters.LOGGER.info("  Hostile: " + hostileEntities.size());
        RandomEncounters.LOGGER.info("  Neutral: " + neutralEntities.size());
        RandomEncounters.LOGGER.info("  Passive: " + passiveEntities.size());
        RandomEncounters.LOGGER.info("  Boss: " + bossEntities.size());
    }

    /**
     * Categorize an entity based on its class hierarchy
     */
    private void categorizeEntity(String entityName, Class<? extends Entity> entityClass) {
        // Boss entities
        if (EntityDragon.class.isAssignableFrom(entityClass) ||
            EntityWither.class.isAssignableFrom(entityClass) ||
            entityName.contains("dragon") || entityName.contains("wither")) {
            bossEntities.add(entityName);
            entityDescriptions.put(entityName, "Boss creature - extremely dangerous");
            return;
        }

        // Hostile entities
        if (EntityMob.class.isAssignableFrom(entityClass) ||
            EntitySlime.class.isAssignableFrom(entityClass) ||
            EntityGhast.class.isAssignableFrom(entityClass) ||
            EntityBlaze.class.isAssignableFrom(entityClass)) {
            hostileEntities.add(entityName);

            // Add helpful descriptions
            if (entityClass == EntityZombie.class) {
                entityDescriptions.put(entityName, "Melee attacker - good for raiders, scavengers");
            } else if (entityClass == EntitySkeleton.class) {
                entityDescriptions.put(entityName, "Ranged attacker - good for scouts, soldiers");
            } else if (entityClass == EntitySpider.class || entityClass == EntityCaveSpider.class) {
                entityDescriptions.put(entityName, "Fast melee - good for swarms, mutants");
            } else if (entityClass == EntityCreeper.class) {
                entityDescriptions.put(entityName, "Explosive - good for suicide troops");
            } else if (entityClass == EntityWitch.class) {
                entityDescriptions.put(entityName, "Potion user - good for cultists, shamans");
            } else if (entityClass == EntityEnderman.class) {
                entityDescriptions.put(entityName, "Teleporting - good for mysterious encounters");
            } else {
                entityDescriptions.put(entityName, "Hostile creature");
            }
            return;
        }

        // Passive/friendly entities
        if (EntityAnimal.class.isAssignableFrom(entityClass) ||
            EntityVillager.class.isAssignableFrom(entityClass) ||
            EntityGolem.class.isAssignableFrom(entityClass)) {
            passiveEntities.add(entityName);

            if (entityClass == EntityVillager.class) {
                entityDescriptions.put(entityName, "NPC - perfect for traders, quest givers");
            } else if (EntityGolem.class.isAssignableFrom(entityClass)) {
                entityDescriptions.put(entityName, "Guardian - good for protectors, guards");
            } else if (entityClass == EntityWolf.class) {
                entityDescriptions.put(entityName, "Tameable - good for guard animals");
            } else {
                entityDescriptions.put(entityName, "Passive creature");
            }
            return;
        }

        // Neutral entities (everything else that extends EntityLiving)
        if (EntityLiving.class.isAssignableFrom(entityClass)) {
            neutralEntities.add(entityName);
            entityDescriptions.put(entityName, "Neutral creature - can be hostile or passive");
        }
    }

    /**
     * Get all available entities
     */
    public List<String> getAllEntities() {
        return new ArrayList<>(allEntities);
    }

    /**
     * Get hostile entities suitable for combat encounters
     */
    public List<String> getHostileEntities() {
        return new ArrayList<>(hostileEntities);
    }

    /**
     * Get neutral entities
     */
    public List<String> getNeutralEntities() {
        return new ArrayList<>(neutralEntities);
    }

    /**
     * Get passive entities suitable for friendly NPCs
     */
    public List<String> getPassiveEntities() {
        return new ArrayList<>(passiveEntities);
    }

    /**
     * Get boss entities (use sparingly!)
     */
    public List<String> getBossEntities() {
        return new ArrayList<>(bossEntities);
    }

    /**
     * Get a description for an entity type
     */
    public String getEntityDescription(String entityName) {
        return entityDescriptions.getOrDefault(entityName, "Unknown entity");
    }

    /**
     * Build a formatted list of entities for AI prompts
     */
    public String buildEntityListForPrompt(boolean includeDescriptions, int maxEntitiesPerCategory) {
        StringBuilder sb = new StringBuilder();

        sb.append("**HOSTILE ENTITIES (for combat encounters):**\n");
        appendEntityList(sb, hostileEntities, includeDescriptions, maxEntitiesPerCategory);

        sb.append("\n**PASSIVE ENTITIES (for friendly NPCs, traders, guards):**\n");
        appendEntityList(sb, passiveEntities, includeDescriptions, maxEntitiesPerCategory);

        sb.append("\n**NEUTRAL ENTITIES (can be used either way):**\n");
        appendEntityList(sb, neutralEntities, includeDescriptions, maxEntitiesPerCategory);

        if (!bossEntities.isEmpty()) {
            sb.append("\n**BOSS ENTITIES (use sparingly for special encounters):**\n");
            appendEntityList(sb, bossEntities, includeDescriptions, maxEntitiesPerCategory);
        }

        return sb.toString();
    }

    /**
     * Append a list of entities to the string builder
     */
    private void appendEntityList(StringBuilder sb, List<String> entities,
                                  boolean includeDescriptions, int maxEntities) {
        int count = 0;
        for (String entity : entities) {
            if (maxEntities > 0 && count >= maxEntities) {
                sb.append("  ... and ").append(entities.size() - maxEntities).append(" more\n");
                break;
            }

            sb.append("  - `").append(entity).append("`");

            if (includeDescriptions && entityDescriptions.containsKey(entity)) {
                sb.append(" - ").append(entityDescriptions.get(entity));
            }

            sb.append("\n");
            count++;
        }
    }

    /**
     * Build a compact list for JSON conversion prompt
     */
    public String buildCompactEntityList() {
        StringBuilder sb = new StringBuilder();

        // Show a good sample of entities
        List<String> sampleEntities = new ArrayList<>();

        // Always include these common vanilla entities
        sampleEntities.add("minecraft:zombie");
        sampleEntities.add("minecraft:skeleton");
        sampleEntities.add("minecraft:spider");
        sampleEntities.add("minecraft:creeper");
        sampleEntities.add("minecraft:witch");
        sampleEntities.add("minecraft:enderman");
        sampleEntities.add("minecraft:villager");
        sampleEntities.add("minecraft:wolf");
        sampleEntities.add("minecraft:iron_golem");

        // Add some modded entities if available
        for (String entity : allEntities) {
            if (!entity.startsWith("minecraft:") && sampleEntities.size() < 30) {
                sampleEntities.add(entity);
            }
        }

        sb.append("Valid entity types: ");
        sb.append(String.join(", ", sampleEntities));

        if (allEntities.size() > sampleEntities.size()) {
            sb.append(", plus ").append(allEntities.size() - sampleEntities.size()).append(" more");
        }

        return sb.toString();
    }

    /**
     * Check if an entity exists
     */
    public boolean isValidEntity(String entityName) {
        return allEntities.contains(entityName);
    }

    /**
     * Get entity count
     */
    public int getEntityCount() {
        return allEntities.size();
    }
}
