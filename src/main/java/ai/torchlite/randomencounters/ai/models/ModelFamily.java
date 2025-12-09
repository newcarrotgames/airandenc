package ai.torchlite.randomencounters.ai.models;

/**
 * Enum representing different AI model families with their unique characteristics
 */
public enum ModelFamily {
    /**
     * OpenAI o1 models (o1-mini, o1-preview)
     * - No temperature control
     * - No system messages
     * - Uses max_completion_tokens
     */
    O1,

    /**
     * OpenAI GPT-5 models
     * - Fixed temperature (1.0 only)
     * - Supports system messages
     * - Uses max_completion_tokens
     */
    GPT5,

    /**
     * OpenAI GPT-4o models (gpt-4o, gpt-4o-mini)
     * - Full temperature control
     * - Supports system messages
     * - Uses max_completion_tokens
     */
    GPT4O,

    /**
     * OpenAI GPT-4 Turbo models (gpt-4-turbo, gpt-4-1106-preview)
     * - Full temperature control
     * - Supports system messages
     * - Uses max_tokens
     */
    GPT4_TURBO,

    /**
     * OpenAI GPT-4 base models (gpt-4, gpt-4-0613)
     * - Full temperature control
     * - Supports system messages
     * - Uses max_tokens
     */
    GPT4,

    /**
     * OpenAI GPT-3.5 models (gpt-3.5-turbo)
     * - Full temperature control
     * - Supports system messages
     * - Uses max_tokens
     */
    GPT3_5,

    /**
     * Anthropic Claude models (claude-3-5-sonnet, claude-3-opus, etc.)
     * - Full temperature control
     * - Supports system messages
     * - Uses max_tokens
     */
    CLAUDE,

    /**
     * Unknown or unsupported model
     * - Conservative defaults applied
     */
    UNKNOWN
}
