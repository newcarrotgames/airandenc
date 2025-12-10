# AI Model Abstraction Layer

## Overview

The model abstraction layer provides a unified interface for working with different AI models that have varying API requirements. This makes it easy to add new models and ensures correct parameter usage for each model family.

## Architecture

### Components

1. **ModelFamily** - Enum of supported model families (GPT-4o, GPT-5, o1, Claude, etc.)
2. **ModelConfig** - Configuration for a specific model including capabilities and defaults
3. **ChatRequestBuilder** - Fluent builder for creating model-specific API requests

## Model Families

### OpenAI o1 Models (`o1-mini`, `o1-preview`)
- ❌ No temperature control
- ❌ No system messages
- ✅ Uses `max_completion_tokens`
- 🎯 Best for: Complex reasoning tasks

**Example:**
```java
ChatRequestBuilder builder = new ChatRequestBuilder("o1-mini")
    .setMaxTokens(2000)
    .addUserMessage(prompt);  // System messages are automatically skipped
```

### OpenAI GPT-5 Models (`gpt-5`, `gpt-5-turbo`)
- ⚠️ Fixed temperature (1.0 only)
- ✅ Supports system messages
- ✅ Uses `max_completion_tokens`
- 🎯 Best for: Latest capabilities

### OpenAI GPT-4o Models (`gpt-4o`, `gpt-4o-mini`)
- ✅ Full temperature control
- ✅ Supports system messages
- ✅ Uses `max_completion_tokens`
- 🎯 Best for: Fast, cost-effective generation

### OpenAI GPT-4 Turbo (`gpt-4-turbo`, `gpt-4-1106-preview`)
- ✅ Full temperature control
- ✅ Supports system messages
- ✅ Uses `max_tokens`
- 🎯 Best for: High-quality outputs

### OpenAI GPT-4 Base (`gpt-4`, `gpt-4-0613`)
- ✅ Full temperature control
- ✅ Supports system messages
- ✅ Uses `max_tokens`
- 🎯 Best for: Stable, reliable outputs

### OpenAI GPT-3.5 (`gpt-3.5-turbo`)
- ✅ Full temperature control
- ✅ Supports system messages
- ✅ Uses `max_tokens`
- 🎯 Best for: Budget-friendly operations

### Anthropic Claude (`claude-3-5-sonnet`, `claude-3-opus`)
- ✅ Full temperature control
- ✅ Supports system messages
- ✅ Uses `max_tokens`
- 🎯 Best for: Long context, nuanced responses

## Usage Examples

### Basic Request
```java
ChatRequestBuilder builder = new ChatRequestBuilder("gpt-4o")
    .addSystemMessage("You are a helpful assistant.")
    .addUserMessage("Tell me about Dregora.");

JsonObject request = builder.build();
String requestBody = gson.toJson(request);
```

### Creative Task (High Temperature)
```java
ChatRequestBuilder builder = new ChatRequestBuilder("gpt-4o")
    .setCreativeTask(true)  // Uses temperature 0.9
    .setMaxTokens(2000)
    .addSystemMessage("You are an expert storyteller.")
    .addUserMessage(prompt);
```

### Structured Task (Low Temperature)
```java
ChatRequestBuilder builder = new ChatRequestBuilder("gpt-4o")
    .setCreativeTask(false)  // Uses temperature 0.2
    .setMaxTokens(2000)
    .addSystemMessage("You are a precise data converter.")
    .addUserMessage(conversionPrompt);
```

### Working with o1 Models
```java
// System messages are automatically skipped for o1 models
ChatRequestBuilder builder = new ChatRequestBuilder("o1-mini")
    .addSystemMessage("This will be ignored")  // Automatically skipped
    .addUserMessage("Only user messages work with o1");
```

### Custom Temperature
```java
ChatRequestBuilder builder = new ChatRequestBuilder("gpt-4o")
    .setTemperature(0.7)  // Override default
    .addUserMessage(prompt);
```

## How It Works

### 1. Model Detection
The `ModelConfig.fromModelId()` method detects the model family from the model string:

```java
ModelConfig config = ModelConfig.fromModelId("gpt-4o");
// Returns ModelFamily.GPT4O with appropriate settings
```

### 2. Capability-Based Building
The builder automatically handles model-specific quirks:

```java
ChatRequestBuilder builder = new ChatRequestBuilder("o1-mini")
    .addSystemMessage("System prompt");  // Silently skipped for o1

JsonObject request = builder.build();
// Temperature is not set (o1 doesn't support it)
// Uses "max_completion_tokens" instead of "max_tokens"
```

### 3. Safe Defaults
Each model family has appropriate defaults:
- Creative tasks: temperature 0.9
- Structured tasks: temperature 0.2
- Max tokens: 2000
- Correct parameter names for each model

## Adding New Models

To add support for a new model family:

### 1. Add to ModelFamily enum
```java
public enum ModelFamily {
    // ... existing families ...

    /**
     * New Model Family
     * - Special capability 1
     * - Special capability 2
     */
    NEW_MODEL
}
```

### 2. Add detection in ModelConfig
```java
public static ModelConfig fromModelId(String modelId) {
    ModelFamily family;

    if (modelId.startsWith("newmodel-")) {
        family = ModelFamily.NEW_MODEL;
    }
    // ... rest of detection ...
}
```

### 3. Configure capabilities
```java
case NEW_MODEL:
    this.supportsTemperature = true;
    this.supportsSystemMessages = true;
    this.tokenParameter = "max_tokens";
    this.defaultTemperature = 0.9;
    this.defaultMaxTokens = 2000;
    break;
```

That's it! The rest of the system automatically adapts.

## Benefits

### 1. **No More Model-Specific Code**
Before:
```java
if (model.startsWith("o1")) {
    // Don't set temperature
    // Don't use system messages
    request.addProperty("max_completion_tokens", 2000);
} else if (model.startsWith("gpt-4o")) {
    request.addProperty("temperature", 0.9);
    request.addProperty("max_completion_tokens", 2000);
    // Add system message...
} else {
    // Handle other models...
}
```

After:
```java
ChatRequestBuilder builder = new ChatRequestBuilder(model)
    .setCreativeTask(true)
    .addSystemMessage("...")
    .addUserMessage(prompt);
```

### 2. **Automatic Future-Proofing**
When OpenAI releases `gpt-6`, just add one line to the detection logic and it works automatically.

### 3. **Type Safety**
The builder pattern ensures you can't create invalid requests.

### 4. **Testability**
Easy to test different model configurations without making API calls.

### 5. **Centralized Configuration**
All model-specific logic lives in one place (`ModelConfig`), not scattered throughout the codebase.

## Migration from Old Code

### Before (Manual Model Handling)
```java
boolean isO1Model = model.startsWith("o1");
boolean isGpt5Model = model.startsWith("gpt-5");

if (!isO1Model && !isGpt5Model) {
    request.addProperty("temperature", 0.9);
}

if (model.startsWith("gpt-4o") || model.startsWith("gpt-5") || model.startsWith("o1")) {
    request.addProperty("max_completion_tokens", 2000);
} else {
    request.addProperty("max_tokens", 2000);
}

JsonArray messages = new JsonArray();
if (!isO1Model) {
    JsonObject systemMessage = new JsonObject();
    systemMessage.addProperty("role", "system");
    systemMessage.addProperty("content", "System prompt");
    messages.add(systemMessage);
}
```

### After (Model Abstraction)
```java
ChatRequestBuilder builder = new ChatRequestBuilder(model)
    .setCreativeTask(true)
    .setMaxTokens(2000)
    .addSystemMessage("System prompt")
    .addUserMessage(prompt);

JsonObject request = builder.build();
```

## Files

- [ModelFamily.java](src/main/java/ai/torchlite/randomencounters/ai/models/ModelFamily.java) - Enum of model families
- [ModelConfig.java](src/main/java/ai/torchlite/randomencounters/ai/models/ModelConfig.java) - Model configuration and detection
- [ChatRequestBuilder.java](src/main/java/ai/torchlite/randomencounters/ai/models/ChatRequestBuilder.java) - Fluent request builder
- [OpenAIStorytellingService.java](src/main/java/ai/torchlite/randomencounters/ai/OpenAIStorytellingService.java) - Usage example
