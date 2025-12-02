package ai.torchlite.randomencounters.story;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Manages story thread lifecycle and selection
 */
public class StoryThreadManager {

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
    private static final long WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS;

    private final Random random = new Random();

    /**
     * Update thread based on AI response
     */
    public void updateThread(PlayerStoryState state, StoryThreadUpdate update) {
        state.updateThread(update);
        updateThreadPriority(state.getActiveThreads().get(update.getThreadId()));
    }

    /**
     * Update thread priority based on progress and time
     */
    public void updateThreadPriority(StoryThread thread) {
        if (thread == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long daysSinceUpdate = (currentTime - thread.getLastUpdateTimestamp()) / DAY_IN_MILLIS;

        // Threads at high progress become URGENT
        if (thread.getProgressLevel() >= 8 && thread.getStatus() == ThreadStatus.ACTIVE) {
            thread.setPriority(ThreadPriority.URGENT);
        } else if (thread.getProgressLevel() >= 5) {
            thread.setPriority(ThreadPriority.HIGH);
        } else if (thread.getProgressLevel() >= 3) {
            thread.setPriority(ThreadPriority.MEDIUM);
        }

        // Old threads without progress become DORMANT
        if (daysSinceUpdate > 7 && thread.getProgressLevel() < 3 && thread.getStatus() == ThreadStatus.ACTIVE) {
            thread.setStatus(ThreadStatus.DORMANT);
            System.out.println("[StoryEncounters] Thread '" + thread.getTitle() +
                "' became dormant due to inactivity");
        }
    }

    /**
     * Select a thread for the next encounter
     * Returns null if no suitable thread exists (allowing emergent encounter)
     */
    public StoryThread selectThreadForEncounter(PlayerStoryState state) {
        // First check for URGENT threads
        StoryThread urgentThread = state.getUrgentThread();
        if (urgentThread != null && meetsEncounterCooldown(urgentThread)) {
            return urgentThread;
        }

        // Get all active threads that meet cooldown requirements
        List<StoryThread> candidates = state.getActiveThreadsList().stream()
            .filter(this::meetsEncounterCooldown)
            .sorted(Comparator
                .comparing(StoryThread::getPriority)
                .reversed()
                .thenComparing(StoryThread::getLastUpdateTimestamp)) // Prefer older updates
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return null; // No suitable thread - generate emergent encounter
        }

        // Weighted random selection favoring high priority
        return weightedRandomSelect(candidates);
    }

    /**
     * Check if thread meets its encounter cooldown
     */
    private boolean meetsEncounterCooldown(StoryThread thread) {
        Integer minEncounters = thread.getMinEncountersUntilNext();
        if (minEncounters == null || minEncounters <= 0) {
            return true; // No cooldown requirement
        }

        // TODO: Track encounters since last thread update
        // For now, always allow
        return true;
    }

    /**
     * Weighted random selection based on priority
     */
    private StoryThread weightedRandomSelect(List<StoryThread> threads) {
        if (threads.isEmpty()) {
            return null;
        }

        if (threads.size() == 1) {
            return threads.get(0);
        }

        // Calculate weights based on priority
        int totalWeight = 0;
        List<Integer> weights = new ArrayList<>();

        for (StoryThread thread : threads) {
            int weight = thread.getPriority().getValue() + 1; // +1 so LOW isn't 0
            weights.add(weight);
            totalWeight += weight;
        }

        // Random selection
        int randomValue = random.nextInt(totalWeight);
        int currentSum = 0;

        for (int i = 0; i < threads.size(); i++) {
            currentSum += weights.get(i);
            if (randomValue < currentSum) {
                return threads.get(i);
            }
        }

        // Fallback (shouldn't reach here)
        return threads.get(0);
    }

    /**
     * Create a new story thread
     */
    public StoryThread createThread(String threadId, String title, String description,
                                   String narrativeContext) {
        StoryThread thread = new StoryThread(threadId, title, description);
        thread.setNarrativeContext(narrativeContext);
        thread.setPriority(ThreadPriority.MEDIUM);
        thread.setProgressLevel(1); // Start at 1, not 0
        return thread;
    }

    /**
     * Mark thread as resolved
     */
    public void resolveThread(StoryThread thread) {
        thread.setStatus(ThreadStatus.RESOLVED);
        thread.setProgressLevel(10);
        thread.setLastUpdateTimestamp(System.currentTimeMillis());
        System.out.println("[StoryEncounters] Thread resolved: " + thread.getTitle());
    }

    /**
     * Mark thread as failed
     */
    public void failThread(StoryThread thread) {
        thread.setStatus(ThreadStatus.FAILED);
        thread.setLastUpdateTimestamp(System.currentTimeMillis());
        System.out.println("[StoryEncounters] Thread failed: " + thread.getTitle());
    }

    /**
     * Revive a dormant thread
     */
    public void reviveThread(StoryThread thread) {
        if (thread.getStatus() == ThreadStatus.DORMANT) {
            thread.setStatus(ThreadStatus.ACTIVE);
            thread.setPriority(ThreadPriority.MEDIUM);
            thread.setLastUpdateTimestamp(System.currentTimeMillis());
            System.out.println("[StoryEncounters] Thread revived: " + thread.getTitle());
        }
    }

    /**
     * Clean up old resolved/failed threads (housekeeping)
     */
    public void cleanupOldThreads(PlayerStoryState state) {
        long currentTime = System.currentTimeMillis();
        List<String> threadsToRemove = new ArrayList<>();

        for (StoryThread thread : state.getActiveThreads().values()) {
            long daysSinceUpdate = (currentTime - thread.getLastUpdateTimestamp()) / DAY_IN_MILLIS;

            // Remove resolved threads older than 30 days
            if (thread.getStatus() == ThreadStatus.RESOLVED && daysSinceUpdate > 30) {
                threadsToRemove.add(thread.getThreadId());
            }

            // Remove failed threads older than 14 days
            if (thread.getStatus() == ThreadStatus.FAILED && daysSinceUpdate > 14) {
                threadsToRemove.add(thread.getThreadId());
            }
        }

        for (String threadId : threadsToRemove) {
            state.getActiveThreads().remove(threadId);
        }

        if (!threadsToRemove.isEmpty()) {
            System.out.println("[StoryEncounters] Cleaned up " + threadsToRemove.size() + " old threads");
        }
    }
}
