package ai.torchlite.randomencounters.story;

/**
 * Lifecycle status of a story thread
 */
public enum ThreadStatus {
    ACTIVE,      // Thread is actively progressing
    DORMANT,     // Thread has been inactive for a while
    RESOLVED,    // Thread has reached completion
    FAILED       // Thread has failed/been abandoned
}
