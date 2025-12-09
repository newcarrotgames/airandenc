package ai.torchlite.randomencounters.ai.models;

/**
 * Configuration for a specific AI model
 */
public class ModelConfig {
    private final String modelId;
    private final ModelFamily family;
    private final boolean supportsTemperature;
    private final boolean supportsSystemMessages;
    private final String tokenParameter; // "max_tokens" or "max_completion_tokens"
    private final Double defaultTemperature;
    private final Integer defaultMaxTokens;

    public ModelConfig(String modelId, ModelFamily family) {
        this.modelId = modelId;
        this.family = family;

        // Set capabilities based on model family
        switch (family) {
            case O1:
                // o1 models don't support temperature or system messages
                this.supportsTemperature = false;
                this.supportsSystemMessages = false;
                this.tokenParameter = "max_completion_tokens";
                this.defaultTemperature = null;
                this.defaultMaxTokens = 2000;
                break;

            case GPT5:
                // gpt-5 models only support default temperature (1.0)
                this.supportsTemperature = false;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_completion_tokens";
                this.defaultTemperature = 1.0;
                this.defaultMaxTokens = 2000;
                break;

            case GPT4O:
                // gpt-4o models support all features
                this.supportsTemperature = true;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_completion_tokens";
                this.defaultTemperature = 0.9;
                this.defaultMaxTokens = 2000;
                break;

            case GPT4_TURBO:
            case GPT4:
                // gpt-4 and gpt-4-turbo support all features
                this.supportsTemperature = true;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_tokens";
                this.defaultTemperature = 0.9;
                this.defaultMaxTokens = 2000;
                break;

            case GPT3_5:
                // gpt-3.5-turbo supports all features
                this.supportsTemperature = true;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_tokens";
                this.defaultTemperature = 0.9;
                this.defaultMaxTokens = 2000;
                break;

            case CLAUDE:
                // Claude models support all features
                this.supportsTemperature = true;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_tokens";
                this.defaultTemperature = 0.9;
                this.defaultMaxTokens = 2000;
                break;

            default:
                // Conservative defaults for unknown models
                this.supportsTemperature = false;
                this.supportsSystemMessages = true;
                this.tokenParameter = "max_tokens";
                this.defaultTemperature = null;
                this.defaultMaxTokens = 2000;
                break;
        }
    }

    /**
     * Detect model family from model ID string
     */
    public static ModelConfig fromModelId(String modelId) {
        ModelFamily family;

        if (modelId.startsWith("o1-")) {
            family = ModelFamily.O1;
        } else if (modelId.startsWith("gpt-5")) {
            family = ModelFamily.GPT5;
        } else if (modelId.startsWith("gpt-4o")) {
            family = ModelFamily.GPT4O;
        } else if (modelId.startsWith("gpt-4-turbo") || modelId.startsWith("gpt-4-1106") || modelId.startsWith("gpt-4-0125")) {
            family = ModelFamily.GPT4_TURBO;
        } else if (modelId.startsWith("gpt-4")) {
            family = ModelFamily.GPT4;
        } else if (modelId.startsWith("gpt-3.5")) {
            family = ModelFamily.GPT3_5;
        } else if (modelId.startsWith("claude")) {
            family = ModelFamily.CLAUDE;
        } else {
            family = ModelFamily.UNKNOWN;
        }

        return new ModelConfig(modelId, family);
    }

    // Getters
    public String getModelId() { return modelId; }
    public ModelFamily getFamily() { return family; }
    public boolean supportsTemperature() { return supportsTemperature; }
    public boolean supportsSystemMessages() { return supportsSystemMessages; }
    public String getTokenParameter() { return tokenParameter; }
    public Double getDefaultTemperature() { return defaultTemperature; }
    public Integer getDefaultMaxTokens() { return defaultMaxTokens; }

    /**
     * Get the appropriate temperature for this model
     * Returns null if temperature should not be set
     */
    public Double getTemperature(boolean isCreative) {
        if (!supportsTemperature) {
            return null;
        }

        // Creative tasks use higher temperature, conversion uses lower
        return isCreative ? 0.9 : 0.2;
    }
}
