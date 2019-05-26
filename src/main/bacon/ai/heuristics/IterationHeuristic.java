package bacon.ai.heuristics;

/**
 * Heuristic that controls the search iterations.
 */
public class IterationHeuristic {

    /**
     * Time limit factor below which a new iteration is done.
     */
    private static final double THRESHOLD_FACTOR = 0.2;

    private final boolean useTimeLimit;

    private long startTimeStamp;
    private int timeLimit;

    private int maxDepth;
    private int currentDepth;

    /**
     * Creates a new IterationHeuristic instance. Starts a timer when time limit is not zero.
     *
     * @param timeLimit the time limit in ms that applies or zero if no time limit is set
     * @param maxDepth the maximum search depth or zero if depth is not set
     */
    public IterationHeuristic(int timeLimit, int maxDepth) {
        assert timeLimit == 0 || maxDepth == 0 : "Either time limit or maximum search depth have to be zero";
        if (timeLimit == 0) {
            // depth is set
            this.useTimeLimit = false;
            this.maxDepth = maxDepth;
            this.currentDepth = 0;
        } else {
            // time limit is set: start timer
            this.useTimeLimit = true;
            this.startTimeStamp = System.nanoTime();
            this.timeLimit = timeLimit;
        }
    }

    /**
     * Returns whether another iteration should be done. The depth is increased. This has to be called once on every iteration.
     *
     * @return <code>true</code> if another iteration should be done, <code>false</code> otherwise
     */
    public boolean doIteration() {

        if (this.useTimeLimit) {
            this.currentDepth += 1;
            return (System.nanoTime() - this.startTimeStamp) / 1000000.0 <= this.timeLimit * THRESHOLD_FACTOR;
        } else {
            if (this.currentDepth == 0) {
                this.currentDepth = this.maxDepth;
                return true;
            }
            return false;
        }

    }

    /**
     * Returns the depth to search to in the iteration.
     *
     * @return the maximum search depth for the current iteration
     */
    public int getDepth() {
        return this.currentDepth;
    }

}
