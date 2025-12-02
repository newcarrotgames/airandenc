package ai.torchlite.randomencounters;

import ai.torchlite.randomencounters.ai.NarrativePromptBuilder;
import ai.torchlite.randomencounters.ai.OpenAIStorytellingService;
import ai.torchlite.randomencounters.ai.AnthropicStorytellingService;
import ai.torchlite.randomencounters.ai.StorytellingRequest;
import ai.torchlite.randomencounters.story.StorytellingResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Standalone test program for AI encounter generation
 * Run this to test AI prompts without starting Minecraft
 */
public class AIGenerationTest {

    public static void main(String[] args) {
        System.out.println("=== Random Encounters AI Test ===\n");

        // Read API configuration from environment or user input
        // String provider = getInput("Which AI provider? (default openai): ");
		// just set provider to openai for now
		String provider = "openai";

        // String apiKey = getInput("Enter API key (or press Enter to use OPENAI_API_KEY env var): ");
		// use env for now
		String apiKey = "";

		if (apiKey.isEmpty()) {
			// get from env
			apiKey = System.getenv("OPENAI_API_KEY");
			if (apiKey != null && !apiKey.isEmpty()) {
				System.out.println("Using API key from OPENAI_API_KEY environment variable");
			}
		}

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("API key is required!");
			System.out.println("Either:");
			System.out.println("  1. Enter it when prompted");
			System.out.println("  2. Set OPENAI_API_KEY environment variable");
			System.out.println("  3. Run test-ai.bat which sets it automatically");
            return;
        }

        // Create a sample storytelling request
        StorytellingRequest request = createSampleRequest();

        // Build the prompt
        NarrativePromptBuilder promptBuilder = new NarrativePromptBuilder();
        String prompt = promptBuilder.buildPrompt(request);

        System.out.println("\n=== Generated Prompt ===");
        System.out.println(prompt);
        System.out.println("\n=== End Prompt ===\n");

        // String savePrompt = getInput("Save prompt to file? (y/n): ");
		String savePrompt = "y";
        if (savePrompt.equalsIgnoreCase("y")) {
            try {
                java.nio.file.Files.write(
                    java.nio.file.Paths.get("test_prompt.txt"),
                    prompt.getBytes()
                );
                System.out.println("Prompt saved to test_prompt.txt");
            } catch (Exception e) {
                System.err.println("Failed to save prompt: " + e.getMessage());
            }
        }

        // String runTest = getInput("\nRun AI generation test? (y/n): ");
		// String runTest = "y";
        // if (!runTest.equalsIgnoreCase("y")) {
        //     return;
        // }

        try {
            StorytellingResponse response = null;

            if (provider.equalsIgnoreCase("openai")) {
                System.out.println("\nTesting with OpenAI...");
                String model = getInput("Model for story generation (default: gpt-4o): ");
                if (model.isEmpty()) model = "gpt-4o";
                String conversionModel = getInput("Model for JSON conversion (default: gpt-4o): ");
                if (conversionModel.isEmpty()) conversionModel = "gpt-4o";

                response = testOpenAI(prompt, apiKey, model, conversionModel);

            } else if (provider.equalsIgnoreCase("anthropic")) {
                System.out.println("\nTesting with Anthropic...");
                String model = getInput("Model for story generation (default: claude-3-5-sonnet-20241022): ");
                if (model.isEmpty()) model = "claude-3-5-sonnet-20241022";
                String conversionModel = getInput("Model for JSON conversion (default: claude-3-5-sonnet-20241022): ");
                if (conversionModel.isEmpty()) conversionModel = "claude-3-5-sonnet-20241022";

                response = testAnthropic(prompt, apiKey, model, conversionModel);

            } else {
                System.out.println("Invalid provider!");
                return;
            }

            if (response != null) {
                System.out.println("\n=== SUCCESS ===");
                System.out.println("Encounter JSON generated successfully!");
                System.out.println("\nEncounter JSON preview:");
                System.out.println(response.getEncounterJson().substring(0,
                    Math.min(500, response.getEncounterJson().length())) + "...");

                String saveJson = getInput("\nSave full JSON to file? (y/n): ");
                if (saveJson.equalsIgnoreCase("y")) {
                    try {
                        java.nio.file.Files.write(
                            java.nio.file.Paths.get("test_encounter.json"),
                            response.getEncounterJson().getBytes()
                        );
                        System.out.println("JSON saved to test_encounter.json");
                    } catch (Exception e) {
                        System.err.println("Failed to save JSON: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("\n=== ERROR ===");
            System.err.println("Failed to generate encounter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static StorytellingResponse testOpenAI(String prompt, String apiKey, String model, String conversionModel) throws Exception {
        // Create a mock service that uses the provided credentials
        OpenAIStorytellingService service = new OpenAIStorytellingService();

        // Temporarily set config values
        ai.torchlite.randomencounters.config.ConfigHandler.enableOpenAI = true;
        ai.torchlite.randomencounters.config.ConfigHandler.openaiApiKey = apiKey;
        ai.torchlite.randomencounters.config.ConfigHandler.openaiModel = model;
        ai.torchlite.randomencounters.config.ConfigHandler.openaiConversionModel = conversionModel;

        System.out.println("Step 1: Generating narrative with " + model + "...");
        System.out.println("Step 2: Converting to JSON with " + conversionModel + "...");

        return service.generateEncounter(prompt);
    }

    private static StorytellingResponse testAnthropic(String prompt, String apiKey, String model, String conversionModel) throws Exception {
        // Create a mock service that uses the provided credentials
        AnthropicStorytellingService service = new AnthropicStorytellingService();

        // Temporarily set config values
        ai.torchlite.randomencounters.config.ConfigHandler.enableAnthropic = true;
        ai.torchlite.randomencounters.config.ConfigHandler.anthropicApiKey = apiKey;
        ai.torchlite.randomencounters.config.ConfigHandler.anthropicModel = model;
        ai.torchlite.randomencounters.config.ConfigHandler.anthropicConversionModel = conversionModel;

        System.out.println("Step 1: Generating narrative with " + model + "...");
        System.out.println("Step 2: Converting to JSON with " + conversionModel + "...");

        return service.generateEncounter(prompt);
    }

    private static StorytellingRequest createSampleRequest() {
        StorytellingRequest request = new StorytellingRequest();

        // Player info
        request.setPlayerName("TestPlayer");
        request.setPlayerLevel(15);
        request.setPlayerHealth(18.0f);
        request.setPlayerMaxHealth(20.0f);

        // Location
        request.setBiome("Wasteland");
        request.setCurrentLocation("ruined skyscraper");
        request.setPosX(-124);
        request.setPosY(65);
        request.setPosZ(-1675);
        request.setTimeOfDay("dusk");
        request.setWeather("foggy");

        // Equipment
        Map<String, String> equipment = new HashMap<>();
        equipment.put("mainhand", "iron_sword");
        equipment.put("head", "iron_helmet");
        equipment.put("chest", "iron_chestplate");
        request.setEquipment(equipment);

        // Context
        request.setNarrativeSummary("TestPlayer is a scavenger exploring the wasteland ruins.");
        request.setLocalDifficultyRating(0.7f);

        return request;
    }

    private static String getInput(String prompt) {
        System.out.print(prompt);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input != null ? input.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
