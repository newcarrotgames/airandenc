package ai.torchlite.randomencounters.story;

/**
 * Priority level for story threads - determines likelihood of being featured in encounters
 */
public enum ThreadPriority {
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    URGENT(3);

    private final int value;

    ThreadPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
