package bacon.ai.heuristics;

import java.util.HashMap;
import java.util.Map;

/**
 * Heuristic that controls the search iterations.
 */
public class IterationHeuristic {

    /**
     * Time limit factor below which a new iteration is done.
     */
    private static final double DELTA = PancakeWatchdog.SAFETY_GAP;

    private final boolean useTimeLimit;

    private int currentDepth;

    // time only
    private long startTimeStamp;
    private long iterationTimeStamp;
    private int timeLimit;
    private static Map<Integer, Double> avgTimes = new HashMap<>();
    private static Map<Integer, Integer> layerCount = new HashMap<>();

    // depth only
    private int maxDepth;

    /**
     * Creates a new IterationHeuristic instance. Starts a timer when time limit is not zero.
     *
     * @param timeLimit the time limit in ms that applies or zero if no time limit is set
     * @param maxDepth  the maximum search depth or zero if depth is not set
     */
    public IterationHeuristic(int timeLimit, int maxDepth) {
        assert timeLimit == 0 || maxDepth == 0 : "Either time limit or maximum search depth have to be zero";
        if (timeLimit == 0) {
            // depth is set
            this.useTimeLimit = false;
            this.currentDepth = 0;
            this.maxDepth = maxDepth;
        } else {
            // time limit is set: start timer
            this.useTimeLimit = true;
            this.currentDepth = 0;
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
            // time limit => iterative deepening

            if (this.currentDepth > 0) {
                avgTimes.putIfAbsent(this.currentDepth, 0.0);
                layerCount.putIfAbsent(this.currentDepth, 0);

                double newTotal = avgTimes.get(this.currentDepth) * layerCount.get(this.currentDepth)
                        + System.nanoTime() - this.iterationTimeStamp;
                layerCount.put(this.currentDepth, layerCount.get(this.currentDepth) + 1);
                avgTimes.put(this.currentDepth, newTotal / layerCount.get(this.currentDepth));
            }
            this.iterationTimeStamp = System.nanoTime();
            this.currentDepth += 1;
            return (System.nanoTime() - this.startTimeStamp) / 1000000.0 <= this.timeLimit * 0.2;


        } else {
            // depth limit => directly search on highest depth

            if (this.currentDepth == 0) {
                this.currentDepth = this.maxDepth;
                return true;
            }
            // abort after first iteration
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
