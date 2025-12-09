package ai.torchlite.randomencounters.ai.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Builder for creating model-specific chat completion requests
 */
public class ChatRequestBuilder {
    private final ModelConfig modelConfig;
    private final JsonObject request;
    private final JsonArray messages;
    private boolean isCreativeTask = false;
    private Integer maxTokens = null;
    private Double temperature = null;

    public ChatRequestBuilder(String modelId) {
        this.modelConfig = ModelConfig.fromModelId(modelId);
        this.request = new JsonObject();
        this.messages = new JsonArray();

        // Set model
        request.addProperty("model", modelId);
    }

    /**
     * Set whether this is a creative task (high temperature) or structured task (low temperature)
     */
    public ChatRequestBuilder setCreativeTask(boolean creative) {
        this.isCreativeTask = creative;
        return this;
    }

    /**
     * Set custom max tokens (overrides default)
     */
    public ChatRequestBuilder setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    /**
     * Set custom temperature (overrides default, only used if model supports it)
     */
    public ChatRequestBuilder setTemperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    /**
     * Add a system message (only if model supports it)
     */
    public ChatRequestBuilder addSystemMessage(String content) {
        if (modelConfig.supportsSystemMessages()) {
            JsonObject message = new JsonObject();
            message.addProperty("role", "system");
            message.addProperty("content", content);
            messages.add(message);
        }
        return this;
    }

    /**
     * Add a user message
     */
    public ChatRequestBuilder addUserMessage(String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", content);
        messages.add(message);
        return this;
    }

    /**
     * Add an assistant message (for conversation history)
     */
    public ChatRequestBuilder addAssistantMessage(String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "assistant");
        message.addProperty("content", content);
        messages.add(message);
        return this;
    }

    /**
     * Build the final request JSON
     */
    public JsonObject build() {
        // Add messages
        request.add("messages", messages);

        // Add temperature if supported
        if (modelConfig.supportsTemperature()) {
            Double temp = temperature != null ? temperature : modelConfig.getTemperature(isCreativeTask);
            if (temp != null) {
                request.addProperty("temperature", temp);
            }
        }

        // Add max tokens parameter (model-specific naming)
        int tokens = maxTokens != null ? maxTokens : modelConfig.getDefaultMaxTokens();
        request.addProperty(modelConfig.getTokenParameter(), tokens);

        return request;
    }

    /**
     * Get the model configuration
     */
    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    /**
     * Get the messages array (for logging purposes)
     */
    public JsonArray getMessages() {
        return messages;
    }
}
