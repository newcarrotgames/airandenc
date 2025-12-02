# AI Generation Testing

Test AI encounter generation without starting Minecraft!

## Quick Start

### Windows
```bash
test-ai.bat
```

### Linux/Mac
```bash
./gradlew testAI
```

## What It Does

This test program allows you to:

1. **Generate the full prompt** that gets sent to the AI
2. **Save the prompt** to a file for inspection
3. **Test the two-step AI generation** process
4. **See the generated narrative** (Step 1)
5. **See the final JSON** (Step 2)
6. **Save the results** for debugging

## Usage

When you run the test, you'll be prompted for:

1. **AI Provider**: `openai` or `anthropic`
2. **API Key**: Your API key
3. **Models**: Which models to use for each step
4. **Prompt Review**: Option to save the prompt
5. **Test Execution**: Option to actually run the AI generation

## Example Session

```
=== Random Encounters AI Test ===

Which AI provider? (openai/anthropic): openai
Enter API key: sk-...

=== Generated Prompt ===
[Full prompt displayed here]
=== End Prompt ===

Save prompt to file? (y/n): y
Prompt saved to test_prompt.txt

Run AI generation test? (y/n): y

Model for story generation (default: gpt-4): gpt-4
Model for JSON conversion (default: o1-mini): o1-mini

Step 1: Generating narrative with gpt-4...
Generated narrative: ## Encounter: "The Scavenger's Choice"...

Step 2: Converting to JSON with o1-mini...

=== SUCCESS ===
Encounter JSON generated successfully!

Save full JSON to file? (y/n): y
JSON saved to test_encounter.json
```

## Files Generated

- `test_prompt.txt` - The full prompt sent to the AI
- `test_encounter.json` - The final encounter JSON

## Benefits

- **Fast iteration**: No need to restart Minecraft
- **Prompt debugging**: See exactly what the AI receives
- **Model comparison**: Easily test different model combinations
- **Cost effective**: Test without spawning the game
- **Debugging**: See both narrative and JSON conversion steps

## Tips

1. **Save the prompts** to see how they evolve
2. **Test different models** to find the best combination
3. **Compare outputs** from different providers
4. **Check JSON validity** before deploying to the game
5. **Iterate quickly** on prompt improvements

## Customizing the Test

Edit `src/test/java/ai/torchlite/randomencounters/AIGenerationTest.java` to:

- Change the sample player data
- Modify location/biome settings
- Add story threads to test
- Test different encounter types

## Troubleshooting

**Problem**: "ClassNotFoundException"
- Run `./gradlew build` first

**Problem**: "API key invalid"
- Check your API key is correct
- Ensure you have credits/access

**Problem**: "Network error"
- Check internet connection
- Verify API endpoints are accessible
