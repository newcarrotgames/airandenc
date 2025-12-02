package ai.torchlite.randomencounters.proxy;

/**
 * Server-side proxy for server-specific logic
 */
public class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        // Server pre-initialization
    }

    @Override
    public void init() {
        super.init();
        // Server initialization
    }

    @Override
    public void postInit() {
        super.postInit();
        // Server post-initialization
    }
}
