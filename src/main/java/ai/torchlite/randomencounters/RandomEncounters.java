package ai.torchlite.randomencounters;

import ai.torchlite.randomencounters.command.EncounterCommand;
import ai.torchlite.randomencounters.config.ConfigHandler;
import ai.torchlite.randomencounters.encounter.EncounterExecutor;
import ai.torchlite.randomencounters.encounter.EncounterOutcomeTracker;
import ai.torchlite.randomencounters.proxy.CommonProxy;
import ai.torchlite.randomencounters.story.StoryStateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
    modid = RandomEncounters.MODID,
    name = RandomEncounters.NAME,
    version = RandomEncounters.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class RandomEncounters {

    public static final String MODID = "randomencounters";
    public static final String NAME = "Random Encounters";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static RandomEncounters instance;

    @SidedProxy(
        clientSide = "ai.torchlite.randomencounters.proxy.ClientProxy",
        serverSide = "ai.torchlite.randomencounters.proxy.ServerProxy"
    )
    public static CommonProxy proxy;

    // Encounter system
    private static EncounterExecutor encounterExecutor;
    private static EncounterOutcomeTracker outcomeTracker;

    public static EncounterExecutor getEncounterExecutor() {
        return encounterExecutor;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Random Encounters - Pre-initialization");

        // Initialize configuration
        File configFile = new File(event.getModConfigurationDirectory(), "randomencounters.cfg");
        ConfigHandler.init(configFile);
        LOGGER.info("Configuration loaded");

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Random Encounters - Initialization");
        proxy.init();

        // Initialize encounter system
        encounterExecutor = new EncounterExecutor();
        outcomeTracker = new EncounterOutcomeTracker(encounterExecutor);
        MinecraftForge.EVENT_BUS.register(outcomeTracker);
        LOGGER.info("Encounter system initialized");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        LOGGER.info("Random Encounters - Server starting");

        // Initialize story state manager (works for both dedicated and integrated servers)
        MinecraftServer server = event.getServer();
        if (server != null) {
            File worldDir = server.getWorld(0).getSaveHandler().getWorldDirectory();
            StoryStateManager.initialize(worldDir);
            LOGGER.info("Story State Manager initialized with world directory: " + worldDir.getAbsolutePath());
        }

        // Register commands
        event.registerServerCommand(new EncounterCommand());
        LOGGER.info("Registered /encounter command");
    }
}
