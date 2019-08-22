package bacon.ai.heuristics;

/**
 * A watchdog timer that triggers, when time is about to run out.
 * This serves as a safety mechanism in cases, where the {@link IterationHeuristic} fails.
 * A trigger event of the watchdog (<i>pancake</i> \uD83E\uDD5E) requires
 * immediate unwinding of the search tree (<i>time panic</i>).
 * <p>
 * The state of the watchdog has to be actively polled using {@link #isPancake()}.
 */
public class PancakeWatchdog {

    /**
     * Time in milliseconds to abort, before the hard time limit applies.
     * This is used to be able to still unwind the tree and return a move.
     */
    static final int SAFETY_GAP = 500;

    /**
     * The deadline (in system time) after which to trigger.
     */
    private final long deadline;
    /**
     * The watchdog does not trigger if set to {@code false}.
     */
    private final boolean active;
    /**
     * Flag that is set to {@code true} if watchdog has been triggered. This flag is never reset.
     */
    private boolean triggered;

    /**
     * Creates a new {@code PancakeWatchdog} with the given time limit. Zero represents unlimited time.
     *
     * @param timeLimit time limit in ms or zero
     */
    public PancakeWatchdog(int timeLimit) {
        this.active = timeLimit != 0;
        this.deadline = System.nanoTime() / 1000000 + timeLimit - SAFETY_GAP;
    }


    /**
     * Measures the time and returns {@code true} if a time panic should happen.
     * Irreversibly sets the {@code triggered} flag.
     *
     * @return {@code true} if a time panic should occur or {@code false} otherwise
     */
    public boolean isPancake() {
        this.triggered = this.triggered || (this.active && System.nanoTime() / 1000000 - this.deadline > 0);
        return this.triggered;
    }

    /**
     * Returns whether this watchdog has been triggered. I.e. the {@code triggered} flag has been set.
     *
     * @return {@code true} if the watchdog has been triggered, {@code true} otherwise
     */
    public boolean isTriggered() {
        return this.triggered;
    }

}
