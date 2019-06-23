package bacon.ai.heuristics;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Heuristic that controls the search iterations.
 */
public class IterationHeuristic {

    private static final Logger LOGGER = Logger.getGlobal();

    private final boolean useTimeLimit;

    private int currentDepth;

    // time only
    private long startTimeStamp;
    private long iterationTimeStamp;
    private int timeLimit;
    private static Map<Integer, Long> avgTimes = new HashMap<>();
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

            if (this.currentDepth == 0) {
                LOGGER.log(Level.FINE, "Depth: {0}, Start: {1}, It_Start: {2}", new Object[]{ this.currentDepth, this.startTimeStamp, this.iterationTimeStamp});
                this.iterationTimeStamp = System.nanoTime();
                this.currentDepth += 1;
                return true;
            }

            long elapsed = System.nanoTime() - this.iterationTimeStamp;

            layerCount.putIfAbsent(this.currentDepth, 0);
            avgTimes.putIfAbsent(this.currentDepth, 1L); // initial value, works because layerCount is still 0
            avgTimes.putIfAbsent(this.currentDepth + 1, 1L);

            long estimate = elapsed / avgTimes.get(this.currentDepth) + avgTimes.get(this.currentDepth + 1);
            LOGGER.log(Level.FINE, "Depth: {0}, Start: {1}, It_Start: {2}, Est: {3}", new Object[]{ this.currentDepth, this.startTimeStamp, this.iterationTimeStamp, estimate});

            long elapsedSinceStart = System.nanoTime() - this.startTimeStamp;
            boolean doAnother = (this.timeLimit - PancakeWatchdog.SAFETY_GAP) * 1_000_000 >= elapsedSinceStart + estimate;
            LOGGER.log(Level.FINE, "Limit: {0}, Elapsed(start): {1}", new Object[]{ (this.timeLimit - PancakeWatchdog.SAFETY_GAP) * 1_000_000, elapsedSinceStart});

            // layer_count' = layer_count + 1
            layerCount.put(this.currentDepth, layerCount.get(this.currentDepth) + 1);
            // avg' = (layer_count * avg + time) / layer_count'
            avgTimes.put(this.currentDepth, (layerCount.get(this.currentDepth) * avgTimes.get(this.currentDepth) + elapsed) /
                    (layerCount.get(this.currentDepth) + 1));

            this.iterationTimeStamp = System.nanoTime();
            this.currentDepth += 1;
            return doAnother;

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
