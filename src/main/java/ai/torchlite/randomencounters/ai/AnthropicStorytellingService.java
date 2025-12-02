package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.config.ConfigHandler;
import ai.torchlite.randomencounters.story.StorytellingResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Anthropic Claude implementation for storytelling
 */
public class AnthropicStorytellingService implements IAIStorytellingService {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final int PRIORITY = 2; // Try Anthropic as fallback

    private final Gson gson = new Gson();
    private final JsonParser jsonParser = new JsonParser();

    @Override
    public StorytellingResponse generateEncounter(String prompt) throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("Anthropic service is not configured");
        }

        String apiKey = ConfigHandler.anthropicApiKey;
        String creativeModel = ConfigHandler.anthropicModel;
        String conversionModel = ConfigHandler.anthropicConversionModel;

        // Step 1: Generate creative narrative story
        RandomEncounters.LOGGER.info("Step 1: Generating narrative with " + creativeModel);
        String narrative = generateNarrative(prompt, creativeModel, apiKey);
        RandomEncounters.LOGGER.info("Generated narrative: " + narrative.substring(0, Math.min(200, narrative.length())) + "...");

        // Step 2: Convert narrative to JSON
        RandomEncounters.LOGGER.info("Step 2: Converting to JSON with " + conversionModel);
        NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
        String conversionPrompt = promptBuilder.buildConversionPrompt(narrative);

        return convertNarrativeToJson(conversionPrompt, conversionModel, apiKey);
    }

    /**
     * Generate creative narrative story (Step 1)
     */
    private String generateNarrative(String prompt, String model, String apiKey) throws Exception {

        // Build request JSON
        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        request.addProperty("max_tokens", 2000);
        request.addProperty("temperature", 0.9);  // Higher temperature for creativity

        // Add system message
        request.addProperty("system",
            "You are an expert storyteller for RLCraft Dregora, a post-apocalyptic Minecraft modpack. " +
            "Generate immersive, atmospheric encounter narratives with rich details, tension, and meaningful choices.");

        // Add messages array
        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);
        messages.add(userMessage);

        request.add("messages", messages);

        // Make API call
        String requestBody = gson.toJson(request);
        RandomEncounters.LOGGER.debug("Anthropic Request: " + requestBody);

        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("x-api-key", apiKey);
        connection.setRequestProperty("anthropic-version", "2023-06-01");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        // Read response (from either success or error stream)
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream(),
                    StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        String responseBody = response.toString();
        RandomEncounters.LOGGER.debug("Anthropic Response Code: " + responseCode);
        RandomEncounters.LOGGER.debug("Anthropic Response Body: " + responseBody);

        if (responseCode != 200) {
            // Try to parse error details
            try {
                JsonObject errorJson = jsonParser.parse(responseBody).getAsJsonObject();
                if (errorJson.has("error")) {
                    JsonObject error = errorJson.getAsJsonObject("error");
                    String errorMessage = error.has("message")
                        ? error.get("message").getAsString()
                        : "Unknown error";
                    throw new Exception("Anthropic API error: " + errorMessage);
                }
            } catch (Exception e) {
                // If we can't parse the error, just throw with what we have
                throw new Exception("Anthropic API returned error code " + responseCode + ": " + responseBody);
            }
            throw new Exception("Anthropic API returned error code: " + responseCode);
        }

        // Parse response
        JsonObject responseJson;
        try {
            responseJson = jsonParser.parse(responseBody).getAsJsonObject();
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse Anthropic response as JSON: " + responseBody);
            throw new Exception("Invalid JSON response from Anthropic: " + e.getMessage());
        }

        if (!responseJson.has("content") || responseJson.getAsJsonArray("content").size() == 0) {
            throw new Exception("Anthropic response has no content: " + responseBody);
        }

        String content = responseJson.getAsJsonArray("content")
            .get(0).getAsJsonObject()
            .get("text").getAsString();

        RandomEncounters.LOGGER.debug("Anthropic Generated Content: " + content);

        // Return the narrative text (Step 1 complete)
        return content;
    }

    /**
     * Convert narrative story to JSON format (Step 2)
     */
    private StorytellingResponse convertNarrativeToJson(String conversionPrompt, String model, String apiKey) throws Exception {
        // Build request JSON
        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        request.addProperty("max_tokens", 2000);
        request.addProperty("temperature", 0.2);  // Low temperature for structured output

        // Add system message
        request.addProperty("system",
            "You are a precise data converter. Convert narrative text into structured JSON. " +
            "Return ONLY valid JSON with no additional text or markdown.");

        // Add messages array
        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", conversionPrompt);
        messages.add(userMessage);

        request.add("messages", messages);

        // Make API call
        String requestBody = gson.toJson(request);
        RandomEncounters.LOGGER.debug("Anthropic Conversion Request: " + requestBody);

        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("x-api-key", apiKey);
        connection.setRequestProperty("anthropic-version", "2023-06-01");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        // Read response (from either success or error stream)
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream(),
                    StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        String responseBody = response.toString();
        RandomEncounters.LOGGER.debug("Anthropic Conversion Response Code: " + responseCode);
        RandomEncounters.LOGGER.debug("Anthropic Conversion Response Body: " + responseBody);

        if (responseCode != 200) {
            // Try to parse error details
            try {
                JsonObject errorJson = jsonParser.parse(responseBody).getAsJsonObject();
                if (errorJson.has("error")) {
                    JsonObject error = errorJson.getAsJsonObject("error");
                    String errorMessage = error.has("message")
                        ? error.get("message").getAsString()
                        : "Unknown error";
                    throw new Exception("Anthropic API error: " + errorMessage);
                }
            } catch (Exception e) {
                // If we can't parse the error, just throw with what we have
                throw new Exception("Anthropic API returned error code " + responseCode + ": " + responseBody);
            }
            throw new Exception("Anthropic API returned error code: " + responseCode);
        }

        // Parse response
        JsonObject responseJson;
        try {
            responseJson = jsonParser.parse(responseBody).getAsJsonObject();
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse Anthropic response as JSON: " + responseBody);
            throw new Exception("Invalid JSON response from Anthropic: " + e.getMessage());
        }

        if (!responseJson.has("content") || responseJson.getAsJsonArray("content").size() == 0) {
            throw new Exception("Anthropic response has no content: " + responseBody);
        }

        String content = responseJson.getAsJsonArray("content")
            .get(0).getAsJsonObject()
            .get("text").getAsString();

        RandomEncounters.LOGGER.debug("Anthropic Converted JSON: " + content);

        // Parse the AI's JSON response into StorytellingResponse
        try {
            return gson.fromJson(content, StorytellingResponse.class);
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse AI content as StorytellingResponse: " + content);
            throw new Exception("AI did not return valid encounter JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return ConfigHandler.enableAnthropic &&
               ConfigHandler.anthropicApiKey != null &&
               !ConfigHandler.anthropicApiKey.isEmpty();
    }

    @Override
    public String getServiceName() {
        return "Anthropic " + ConfigHandler.anthropicModel;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
