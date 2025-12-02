package ai.torchlite.randomencounters.proxy;

/**
 * Client-side proxy for client-specific logic
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        // Client pre-initialization
    }

    @Override
    public void init() {
        super.init();
        // Client initialization
    }

    @Override
    public void postInit() {
        super.postInit();
        // Client post-initialization
    }
}
