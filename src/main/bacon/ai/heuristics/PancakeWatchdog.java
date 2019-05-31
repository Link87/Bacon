package bacon.ai.heuristics;

import java.util.logging.Logger;

public class PancakeWatchdog {

    /**
     * Time in ms to abort, before time limit applies. This is used to unwind the tree and return a move.
     */
    private static final int SAFETY_GAP = 150;

    private final long deadline;
    private final boolean active;
    private boolean triggered;

    /**
     * Creates a new watchdog with the given time limit. Zero represents unlimited time.
     *
     * @param timeLimit time limit in ms or zero
     */
    public PancakeWatchdog(int timeLimit) {
        this.active = timeLimit != 0;
        this.deadline = System.nanoTime() / 1000000 + timeLimit - SAFETY_GAP;
    }

    /**
     * Measures the time and returns true if a time panic should happen. Irreversibly sets the triggered flag.
     *
     * @return <code>true</code> if a time panic should occur or <code>false</code> otherwise
     */
    public boolean isPancake() {
        this.triggered = this.triggered || (this.active && System.nanoTime() / 1000000 - this.deadline > 0);
        return this.triggered;
    }

    /**
     * Returns whether this watchdog has been triggered.
     *
     * @return <code>true</code> if the watchdog has been triggered, <code>false</code> otherwise
     */
    public boolean isTriggered() {
        return this.triggered;
    }

}
