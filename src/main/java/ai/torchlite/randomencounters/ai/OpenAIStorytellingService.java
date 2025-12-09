package ai.torchlite.randomencounters.ai;

import ai.torchlite.randomencounters.RandomEncounters;
import ai.torchlite.randomencounters.ai.models.ChatRequestBuilder;
import ai.torchlite.randomencounters.config.ConfigHandler;
import ai.torchlite.randomencounters.story.StorytellingResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

        // Log full prompt if verbose logging enabled
        if (ConfigHandler.logAIRequests) {
            RandomEncounters.LOGGER.info("Full Story Generation Prompt:\n" + prompt);
        }

        String narrative = generateNarrative(prompt, creativeModel, apiKey);
        RandomEncounters.LOGGER.info("Generated narrative: " + narrative.substring(0, Math.min(200, narrative.length())) + "...");

        // Step 2: Convert narrative to JSON
        RandomEncounters.LOGGER.info("Step 2: Converting to JSON with " + conversionModel);
        NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
        String conversionPrompt = promptBuilder.buildConversionPrompt(narrative);

        // Log full conversion prompt if verbose logging enabled
        if (ConfigHandler.logAIRequests) {
            RandomEncounters.LOGGER.info("Full Conversion Prompt:\n" + conversionPrompt);
        }

        return convertNarrativeToJson(conversionPrompt, conversionModel, apiKey);
    }

    /**
     * Generate creative narrative story (Step 1)
     */
    private String generateNarrative(String prompt, String model, String apiKey) throws Exception {

        // Build request using model-aware builder
        ChatRequestBuilder builder = new ChatRequestBuilder(model)
            .setCreativeTask(true)
            .setMaxTokens(2000)
            .addSystemMessage(
                "You are an expert storyteller for RLCraft Dregora, a post-apocalyptic Minecraft modpack. " +
                "Generate immersive, atmospheric encounter narratives with rich details, tension, and meaningful choices.")
            .addUserMessage(prompt);

        JsonObject request = builder.build();
        JsonArray messages = builder.getMessages();

        // Make API call
        String requestBody = gson.toJson(request);

        // Log request metadata (always safe)
        RandomEncounters.LOGGER.info("OpenAI API Request - Model: " + model +
            ", Messages: " + messages.size() +
            ", Prompt length: ~" + prompt.length() + " chars");

        // Verbose logging (message content only - API key is NEVER logged)
        if (ConfigHandler.logAIRequests) {
            RandomEncounters.LOGGER.info("Request Parameters: temperature=" +
                (request.has("temperature") ? request.get("temperature").getAsString() : "default") +
                ", max_tokens=" + (request.has("max_completion_tokens") ?
                    request.get("max_completion_tokens").getAsString() :
                    request.get("max_tokens").getAsString()));
            RandomEncounters.LOGGER.info("Request Messages:");
            for (int i = 0; i < messages.size(); i++) {
                JsonObject msg = messages.get(i).getAsJsonObject();
                String role = msg.get("role").getAsString();
                String content = msg.get("content").getAsString();
                RandomEncounters.LOGGER.info("  [" + role + "]: " + content);
            }
        }

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
        RandomEncounters.LOGGER.info("OpenAI API Response - Code: " + responseCode +
            ", Body length: ~" + responseBody.length() + " chars");

        // Verbose logging (full response body - only if explicitly enabled)
        if (ConfigHandler.logAIResponses) {
            RandomEncounters.LOGGER.info("OpenAI Full Response Body: " + responseBody);
        }

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

        RandomEncounters.LOGGER.info("OpenAI Generated Narrative - Length: " + content.length() + " chars");
        RandomEncounters.LOGGER.info("Narrative preview: " +
            content.substring(0, Math.min(200, content.length())).replace("\n", " ") + "...");

        // Return the narrative text (Step 1 complete)
        return content;
    }

    /**
     * Convert narrative story to JSON format (Step 2)
     */
    private StorytellingResponse convertNarrativeToJson(String conversionPrompt, String model, String apiKey) throws Exception {
        // Build request using model-aware builder
        ChatRequestBuilder builder = new ChatRequestBuilder(model)
            .setCreativeTask(false)  // Low temperature for structured output
            .setMaxTokens(2000)
            .addSystemMessage(
                "You are a precise data converter. Convert narrative text into structured JSON. " +
                "Return ONLY valid JSON with no additional text or markdown.")
            .addUserMessage(conversionPrompt);

        JsonObject request = builder.build();
        JsonArray messages = builder.getMessages();

        // Make API call
        String requestBody = gson.toJson(request);

        // Log request metadata (always safe)
        RandomEncounters.LOGGER.info("OpenAI Conversion Request - Model: " + model +
            ", Messages: " + messages.size() +
            ", Conversion prompt length: ~" + conversionPrompt.length() + " chars");

        // Verbose logging (message content only - API key is NEVER logged)
        if (ConfigHandler.logAIRequests) {
            RandomEncounters.LOGGER.info("Request Parameters: temperature=" +
                (request.has("temperature") ? request.get("temperature").getAsString() : "default") +
                ", max_tokens=" + (request.has("max_completion_tokens") ?
                    request.get("max_completion_tokens").getAsString() :
                    request.get("max_tokens").getAsString()));
            RandomEncounters.LOGGER.info("Conversion Request Messages:");
            for (int i = 0; i < messages.size(); i++) {
                JsonObject msg = messages.get(i).getAsJsonObject();
                String role = msg.get("role").getAsString();
                String content = msg.get("content").getAsString();
                RandomEncounters.LOGGER.info("  [" + role + "]: " + content);
            }
        }

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
        RandomEncounters.LOGGER.info("OpenAI Conversion Response - Code: " + responseCode +
            ", Body length: ~" + responseBody.length() + " chars");

        // Verbose logging (full response body - only if explicitly enabled)
        if (ConfigHandler.logAIResponses) {
            RandomEncounters.LOGGER.info("OpenAI Full Conversion Response Body: " + responseBody);
        }

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

        RandomEncounters.LOGGER.info("OpenAI Converted JSON - Length: " + content.length() + " chars");

        // Verbose logging (full JSON - only if explicitly enabled)
        if (ConfigHandler.logAIResponses) {
            RandomEncounters.LOGGER.info("Full Converted JSON:\n" + content);
        }

        // Strip markdown code fences if present (AI sometimes adds ```json ... ```)
        String cleanedContent = content.trim();
        if (cleanedContent.startsWith("```json")) {
            cleanedContent = cleanedContent.substring("```json".length()).trim();
        } else if (cleanedContent.startsWith("```")) {
            cleanedContent = cleanedContent.substring("```".length()).trim();
        }
        if (cleanedContent.endsWith("```")) {
            cleanedContent = cleanedContent.substring(0, cleanedContent.length() - 3).trim();
        }

        // Parse the AI's JSON response into StorytellingResponse
        try {
            StorytellingResponse result = gson.fromJson(cleanedContent, StorytellingResponse.class);

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
            RandomEncounters.LOGGER.error("Failed to parse AI content as StorytellingResponse");
            RandomEncounters.LOGGER.error("Original content: " + content);
            RandomEncounters.LOGGER.error("Cleaned content: " + cleanedContent);
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
