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
 * OpenAI GPT-4 implementation for storytelling
 */
public class OpenAIStorytellingService implements IAIStorytellingService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int PRIORITY = 1; // Try OpenAI first

    private final Gson gson = new Gson();
    private final JsonParser jsonParser = new JsonParser();

    @Override
    public StorytellingResponse generateEncounter(String prompt) throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI service is not configured");
        }

        String apiKey = ConfigHandler.openaiApiKey;
        String creativeModel = ConfigHandler.openaiModel;
        String conversionModel = ConfigHandler.openaiConversionModel;

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

        // o1 models don't support temperature, gpt-5 models only support default temperature (1.0)
        // Other models can use custom temperature
        boolean isO1Model = model.startsWith("o1");
        boolean isGpt5Model = model.startsWith("gpt-5");

        if (!isO1Model && !isGpt5Model) {
            request.addProperty("temperature", 0.9);  // Higher temperature for creativity
        }

        // Newer models (gpt-4o, gpt-5, o1, etc.) use max_completion_tokens
        // Older models (gpt-4, gpt-3.5-turbo) use max_tokens
        if (model.startsWith("gpt-4o") || model.startsWith("gpt-5") || model.startsWith("o1")) {
            request.addProperty("max_completion_tokens", 2000);
        } else {
            request.addProperty("max_tokens", 2000);
        }

        // Add messages
        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
            "You are an expert storyteller for RLCraft Dregora, a post-apocalyptic Minecraft modpack. " +
            "Generate immersive, atmospheric encounter narratives with rich details, tension, and meaningful choices.");
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);
        messages.add(userMessage);

        request.add("messages", messages);

        // Make API call
        String requestBody = gson.toJson(request);
        RandomEncounters.LOGGER.debug("OpenAI Request: " + requestBody);

        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
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
        RandomEncounters.LOGGER.debug("OpenAI Response Code: " + responseCode);
        RandomEncounters.LOGGER.debug("OpenAI Response Body: " + responseBody);

        if (responseCode != 200) {
            // Try to parse error details
            try {
                JsonObject errorJson = jsonParser.parse(responseBody).getAsJsonObject();
                if (errorJson.has("error")) {
                    JsonObject error = errorJson.getAsJsonObject("error");
                    String errorMessage = error.has("message")
                        ? error.get("message").getAsString()
                        : "Unknown error";
                    throw new Exception("OpenAI API error: " + errorMessage);
                }
            } catch (Exception e) {
                // If we can't parse the error, just throw with what we have
                throw new Exception("OpenAI API returned error code " + responseCode + ": " + responseBody);
            }
            throw new Exception("OpenAI API returned error code: " + responseCode);
        }

        // Parse response
        JsonObject responseJson;
        try {
            responseJson = jsonParser.parse(responseBody).getAsJsonObject();
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse OpenAI response as JSON: " + responseBody);
            throw new Exception("Invalid JSON response from OpenAI: " + e.getMessage());
        }

        if (!responseJson.has("choices") || responseJson.getAsJsonArray("choices").size() == 0) {
            throw new Exception("OpenAI response has no choices: " + responseBody);
        }

        String content = responseJson.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .get("message").getAsJsonObject()
            .get("content").getAsString();

        RandomEncounters.LOGGER.debug("OpenAI Generated Content: " + content);

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

        // Newer models (gpt-4o, gpt-5, o1, etc.) use max_completion_tokens
        // Older models (gpt-4, gpt-3.5-turbo) use max_tokens
        if (model.startsWith("gpt-4o") || model.startsWith("gpt-5") || model.startsWith("o1")) {
            request.addProperty("max_completion_tokens", 2000);
        } else {
            request.addProperty("max_tokens", 2000);
        }

        // o1 models don't support temperature or system messages
        // gpt-5 models only support default temperature (1.0)
        // Other models can use custom temperature
        boolean isO1Model = model.startsWith("o1");
        boolean isGpt5Model = model.startsWith("gpt-5");

        if (!isO1Model && !isGpt5Model) {
            request.addProperty("temperature", 0.2);  // Low temperature for structured output
        }

        // Add messages
        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();

        if (!isO1Model) {
            // Regular models can use system messages
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content",
                "You are a precise data converter. Convert narrative text into structured JSON. " +
                "Return ONLY valid JSON with no additional text or markdown.");
            messages.add(systemMessage);
        }

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", conversionPrompt);
        messages.add(userMessage);

        request.add("messages", messages);

        // Make API call
        String requestBody = gson.toJson(request);
        RandomEncounters.LOGGER.debug("OpenAI Conversion Request: " + requestBody);

        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        // Read response (from either success or error stream)
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream(),
                    StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }

        String responseBody = responseBuilder.toString();
        RandomEncounters.LOGGER.debug("OpenAI Conversion Response Code: " + responseCode);
        RandomEncounters.LOGGER.debug("OpenAI Conversion Response Body: " + responseBody);

        if (responseCode != 200) {
            // Try to parse error details
            try {
                JsonObject errorJson = jsonParser.parse(responseBody).getAsJsonObject();
                if (errorJson.has("error")) {
                    JsonObject error = errorJson.getAsJsonObject("error");
                    String errorMessage = error.has("message")
                        ? error.get("message").getAsString()
                        : "Unknown error";
                    throw new Exception("OpenAI API error: " + errorMessage);
                }
            } catch (Exception e) {
                // If we can't parse the error, just throw with what we have
                throw new Exception("OpenAI API returned error code " + responseCode + ": " + responseBody);
            }
            throw new Exception("OpenAI API returned error code: " + responseCode);
        }

        // Parse response
        JsonObject responseJson;
        try {
            responseJson = jsonParser.parse(responseBody).getAsJsonObject();
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse OpenAI response as JSON: " + responseBody);
            throw new Exception("Invalid JSON response from OpenAI: " + e.getMessage());
        }

        if (!responseJson.has("choices") || responseJson.getAsJsonArray("choices").size() == 0) {
            throw new Exception("OpenAI response has no choices: " + responseBody);
        }

        String content = responseJson.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .get("message").getAsJsonObject()
            .get("content").getAsString();

        RandomEncounters.LOGGER.debug("OpenAI Converted JSON: " + content);

        // Parse the AI's JSON response into StorytellingResponse
        try {
            StorytellingResponse result = gson.fromJson(content, StorytellingResponse.class);

            // Validate that we got the required fields
            if (result == null) {
                throw new Exception("AI returned null response");
            }
            if (result.getEncounterJson() == null || result.getEncounterJson().isEmpty()) {
                RandomEncounters.LOGGER.error("AI response missing encounter_json field. Full response: " + content);
                throw new Exception("AI response is missing the 'encounter_json' field");
            }

            return result;
        } catch (Exception e) {
            RandomEncounters.LOGGER.error("Failed to parse AI content as StorytellingResponse: " + content);
            RandomEncounters.LOGGER.error("Parse error: " + e.getMessage());
            throw new Exception("AI did not return valid encounter JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return ConfigHandler.enableOpenAI &&
               ConfigHandler.openaiApiKey != null &&
               !ConfigHandler.openaiApiKey.isEmpty();
    }

    @Override
    public String getServiceName() {
        return "OpenAI " + ConfigHandler.openaiModel;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
