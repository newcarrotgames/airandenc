package ai.torchlite.randomencounters.config;

import ai.torchlite.randomencounters.RandomEncounters;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Configuration handler for Random Encounters mod
 */
public class ConfigHandler {

    private static Configuration config;

    // Story System Settings
    public static boolean enableStorySystem = true;
    public static int maxActiveThreadsPerPlayer = 10;
    public static int maxEncounterHistorySize = 100;
    public static boolean autoSaveOnLogout = true;

    // Dregora Lore Settings
    public static boolean enableDregoraLore = true;
    public static String defaultTone = "grimdark_with_hope";
    public static boolean emphasizeBlight = true;

    // OpenAI Settings
    public static boolean enableOpenAI = false;
    public static String openaiApiKey = "";
    public static String openaiModel = "gpt-4";
    public static String openaiConversionModel = "o1-mini";

    // Anthropic Settings
    public static boolean enableAnthropic = false;
    public static String anthropicApiKey = "";
    public static String anthropicModel = "claude-3-5-sonnet-20241022";
    public static String anthropicConversionModel = "claude-3-5-sonnet-20241022";

    // Encounter Generation Settings
    public static boolean enableAIGeneration = true;
    public static int encounterCooldownMinutes = 5;
    public static float baseEncounterChance = 0.1f;
    public static boolean requireExplicitTrigger = false;

    // Thread Management
    public static int threadDormancyDays = 7;
    public static int threadCleanupResolvedDays = 30;
    public static int threadCleanupFailedDays = 14;

    // Debug Settings
    public static boolean debugMode = false;
    public static boolean logAIRequests = false;
    public static boolean logAIResponses = false;

    /**
     * Initialize configuration
     */
    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    /**
     * Load configuration from file
     */
    private static void loadConfig() {
        try {
            config.load();

            // Story System
            enableStorySystem = config.getBoolean(
                "enableStorySystem",
                "story_system",
                true,
                "Enable the AI storytelling and story state tracking system"
            );

            maxActiveThreadsPerPlayer = config.getInt(
                "maxActiveThreadsPerPlayer",
                "story_system",
                10,
                1, 50,
                "Maximum number of active story threads per player"
            );

            maxEncounterHistorySize = config.getInt(
                "maxEncounterHistorySize",
                "story_system",
                100,
                10, 1000,
                "Maximum number of encounters to keep in player history"
            );

            autoSaveOnLogout = config.getBoolean(
                "autoSaveOnLogout",
                "story_system",
                true,
                "Automatically save player story state when they log out"
            );

            // Dregora Lore
            enableDregoraLore = config.getBoolean(
                "enableDregoraLore",
                "lore",
                true,
                "Include Dregora-specific lore in AI prompts"
            );

            defaultTone = config.getString(
                "defaultTone",
                "lore",
                "grimdark_with_hope",
                "Default narrative tone for encounters (grimdark_with_hope, horror, epic, whimsical)"
            );

            emphasizeBlight = config.getBoolean(
                "emphasizeBlight",
                "lore",
                true,
                "Emphasize Blight corruption themes in encounters"
            );

            // OpenAI
            enableOpenAI = config.getBoolean(
                "enableOpenAI",
                "ai_services.openai",
                false,
                "Enable OpenAI GPT-4 for encounter generation"
            );

            openaiApiKey = config.getString(
                "apiKey",
                "ai_services.openai",
                "",
                "OpenAI API key (get from https://platform.openai.com/api-keys)"
            );

            openaiModel = config.getString(
                "model",
                "ai_services.openai",
                "gpt-4",
                "OpenAI model to use for story generation (gpt-4, gpt-4-turbo, gpt-3.5-turbo)"
            );

            openaiConversionModel = config.getString(
                "conversionModel",
                "ai_services.openai",
                "o1-mini",
                "OpenAI model to use for converting story to JSON (o1-mini, o1-preview, gpt-4)"
            );

            // Anthropic
            enableAnthropic = config.getBoolean(
                "enableAnthropic",
                "ai_services.anthropic",
                false,
                "Enable Anthropic Claude for encounter generation"
            );

            anthropicApiKey = config.getString(
                "apiKey",
                "ai_services.anthropic",
                "",
                "Anthropic API key (get from https://console.anthropic.com/)"
            );

            anthropicModel = config.getString(
                "model",
                "ai_services.anthropic",
                "claude-3-5-sonnet-20241022",
                "Anthropic model to use for story generation (claude-3-5-sonnet-20241022, claude-3-opus-20240229)"
            );

            anthropicConversionModel = config.getString(
                "conversionModel",
                "ai_services.anthropic",
                "claude-3-5-sonnet-20241022",
                "Anthropic model to use for converting story to JSON (claude-3-5-sonnet-20241022)"
            );

            // Encounter Generation
            enableAIGeneration = config.getBoolean(
                "enableAIGeneration",
                "encounters",
                true,
                "Enable AI-driven encounter generation"
            );

            encounterCooldownMinutes = config.getInt(
                "cooldownMinutes",
                "encounters",
                5,
                0, 60,
                "Minimum minutes between AI-generated encounters per player"
            );

            baseEncounterChance = config.getFloat(
                "baseChance",
                "encounters",
                0.1f,
                0.0f, 1.0f,
                "Base chance for an encounter to trigger (0.0 = never, 1.0 = always)"
            );

            requireExplicitTrigger = config.getBoolean(
                "requireExplicitTrigger",
                "encounters",
                false,
                "Require explicit trigger (command) for encounters (disable random encounters)"
            );

            // Thread Management
            threadDormancyDays = config.getInt(
                "dormancyDays",
                "thread_management",
                7,
                1, 30,
                "Days of inactivity before a thread becomes dormant"
            );

            threadCleanupResolvedDays = config.getInt(
                "cleanupResolvedDays",
                "thread_management",
                30,
                1, 365,
                "Days before resolved threads are removed from history"
            );

            threadCleanupFailedDays = config.getInt(
                "cleanupFailedDays",
                "thread_management",
                14,
                1, 365,
                "Days before failed threads are removed from history"
            );

            // Debug
            debugMode = config.getBoolean(
                "debugMode",
                "debug",
                false,
                "Enable debug logging"
            );

            logAIRequests = config.getBoolean(
                "logRequests",
                "debug",
                false,
                "Log full AI prompts and request parameters (API keys are never logged)"
            );

            logAIResponses = config.getBoolean(
                "logResponses",
                "debug",
                false,
                "Log full AI responses including complete narratives and JSON (verbose output)"
            );

        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to load configuration", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    /**
     * Reload configuration from file
     */
    public static void reload() {
        if (config != null) {
            loadConfig();
            RandomEncounters.LOGGER.info("Configuration reloaded");
        }
    }

    /**
     * Save current configuration
     */
    public static void save() {
        if (config != null && config.hasChanged()) {
            config.save();
        }
    }
}
