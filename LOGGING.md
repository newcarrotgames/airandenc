# AI Request Logging

## Overview

The mod includes a comprehensive logging system for AI API requests and responses. **API keys are NEVER logged under any circumstances.** By default, only metadata is logged to reduce log size. Verbose logging can be enabled for debugging prompts and responses.

## Security Guarantee

🔒 **API keys are never written to logs, even in verbose mode.** The logging system only captures:
- Message content (prompts and responses)
- Request parameters (model, temperature, max tokens)
- HTTP response codes and metadata

The Authorization headers containing API keys are never logged.

## Default Logging (Safe Mode)

When `logRequests` and `logResponses` are **disabled** (default), the following metadata is logged:

### Story Generation (Step 1)
- Model name being used
- Number of messages in the request
- Prompt length in characters
- Response HTTP code
- Response body length
- Generated narrative length and preview (first 200 characters)

### JSON Conversion (Step 2)
- Model name being used
- Number of messages in the request
- Conversion prompt length in characters
- Response HTTP code
- Response body length
- Converted JSON length

**Example Default Log Output:**
```
[INFO] Step 1: Generating narrative with gpt-4o
[INFO] OpenAI API Request - Model: gpt-4o, Messages: 2, Prompt length: ~1543 chars
[INFO] OpenAI API Response - Code: 200, Body length: ~2847 chars
[INFO] OpenAI Generated Narrative - Length: 2156 chars
[INFO] Narrative preview: The wasteland stretches endlessly before you, its cracked earth whispering tales of decay...
[INFO] Step 2: Converting to JSON with o1-mini
[INFO] OpenAI Conversion Request - Model: o1-mini, Messages: 1, Conversion prompt length: ~3892 chars
[INFO] OpenAI Conversion Response - Code: 200, Body length: ~1923 chars
[INFO] OpenAI Converted JSON - Length: 1456 chars
```

## Verbose Logging (Debug Mode)

To enable verbose logging, set these options in your config file:

```
debug {
    B:logRequests=true    # Log full prompts and parameters (API keys never logged)
    B:logResponses=true   # Log full response bodies (large output)
}
```

### What Gets Logged in Verbose Mode

**With `logRequests=true`:**
- Request parameters (temperature, max_tokens)
- Full system and user message content
- Complete story generation prompts (markdown formatted)
- Full conversion prompts with narrative and JSON schema
- **NOT logged:** API keys, Authorization headers, full request JSON

**With `logResponses=true`:**
- Complete API response JSON (choices, usage statistics)
- Full generated narrative text
- Complete converted JSON structure

**Example Verbose Log Output:**
```
[INFO] OpenAI API Request - Model: gpt-4o, Messages: 2, Prompt length: ~1543 chars
[INFO] Request Parameters: temperature=0.9, max_tokens=2000
[INFO] Request Messages:
[INFO]   [system]: You are an expert storyteller for RLCraft Dregora...
[INFO]   [user]: # Generate Random Encounter
## World Setting: RLCraft Dregora
**The Blight:** A mysterious corruption...
```

## Configuration Location

The logging settings are in your Minecraft config folder:

```
config/randomencounters.cfg
```

Look for the `debug` section:

```
debug {
    # Enable debug logging
    B:debugMode=false

    # Log full AI prompts and request parameters (API keys are never logged)
    B:logRequests=false

    # Log full AI responses including complete narratives and JSON (verbose output)
    B:logResponses=false
}
```

## Use Cases

### Normal Operation
Leave both `logRequests` and `logResponses` disabled. You'll get useful metadata without verbose output.

### Debugging Prompts
Enable `logRequests=true` to see exactly what prompts are being sent to the AI. Useful for:
- Verifying modular prompt sections are working correctly
- Understanding what context the AI receives
- Debugging why encounters don't match expectations
- Seeing request parameters (temperature, tokens)

### Debugging Responses
Enable `logResponses=true` to see the complete AI output. Useful for:
- Verifying the AI is generating proper JSON
- Debugging parsing errors
- Understanding narrative quality issues
- Analyzing AI token usage

### Full Debug Session
Enable both for complete visibility into the entire AI generation pipeline.

## Log File Locations

Minecraft logs are typically found at:
- **Windows:** `%appdata%/.minecraft/logs/latest.log`
- **Linux/Mac:** `~/.minecraft/logs/latest.log`

For modded installations (like RLCraft Dregora), check your launcher's logs directory.

## Safe to Share

Since API keys are never logged, you can safely share log files when reporting issues or asking for help. However, be aware that verbose logs may contain:
- Your prompts and AI-generated content
- Details about your game session
- Player names and in-game information

Review logs before sharing publicly if privacy is a concern.
