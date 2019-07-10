package bacon.ai.heuristics;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Heuristic that controls the search iterations.
 * <p>
 * Depth-limited games only do a single iteration, with maximum depth.
 * <p>
 * Time-limited games instead use iterative deepening. This class includes a heuristic,
 * that decides whether another iteration is likely to finish in the time limit.
 * Fails of the heuristic are caught by {@link PancakeWatchdog}.
 * <p>
 * Each turn requires to use a new instance of {@code IterationHeuristic}.
 */
public class IterationHeuristic {

    private static final Logger LOGGER = Logger.getGlobal();
    private static final int MAX_DEPTH = 15;
    private static final double SAFETY_FACTOR = 0.90;

    private final boolean useTimeLimit;

    private int currentDepth;

    // time only
    private long startTimeStamp;
    private long iterationTimeStamp;
    private int timeLimit;
    private static final Map<Integer, Long> avgTimes = new HashMap<>();
    private static final Map<Integer, Integer> layerCount = new HashMap<>();

    // depth only
    private int maxDepth;

    /**
     * Creates a new {@code IterationHeuristic} instance.
     * <p>
     * Either {@code timeLimit} or {@code maxDepth} have to be non-zero. Starts a timer in the first case.
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
     * Returns whether another iteration should be done. The depth is increased.
     * <p>
     * This has to be called once in every iteration.
     *
     * @return {@code true} if another iteration should be done, {@code false} otherwise
     */
    public boolean doIteration() {

        if (this.useTimeLimit) {
            // time limit => iterative deepening

            if (this.currentDepth == 0) {
                LOGGER.log(Level.FINE, "Depth: {0}, Start: {1}, It_Start: {2}", new Object[]{this.currentDepth, this.startTimeStamp, this.iterationTimeStamp});
                this.iterationTimeStamp = System.nanoTime();
                this.currentDepth += 1;
                return true;
            }

            long elapsed = System.nanoTime() - this.iterationTimeStamp;
            long elapsedSinceStart = System.nanoTime() - this.startTimeStamp;

            layerCount.putIfAbsent(this.currentDepth, 0);
            avgTimes.putIfAbsent(this.currentDepth, 1_000_000L); // initial value, works because layerCount is still 0
            if (layerCount.getOrDefault(this.currentDepth + 1, 0) == 0) {
                avgTimes.put(this.currentDepth + 1, avgTimes.get(this.currentDepth));
            }

            long estimate = (long) ((float) elapsed / avgTimes.get(this.currentDepth) * avgTimes.get(this.currentDepth + 1));
            boolean doAnother = (this.timeLimit - PancakeWatchdog.SAFETY_GAP) * 1_000_000L * SAFETY_FACTOR >= elapsedSinceStart + estimate && this.currentDepth < MAX_DEPTH;

            LOGGER.log(Level.FINE, "Depth: {0}, Start: {1}, It_Start: {2}", new Object[]{this.currentDepth, this.startTimeStamp, this.iterationTimeStamp, estimate});
            LOGGER.log(Level.FINE, "Limit: {0}, Elapsed(start): {1}, Est: {2}, Elapsed(it): {3}, avg(this): {4}, avg(next): {5}",
                    new Object[]{(this.timeLimit - PancakeWatchdog.SAFETY_GAP) * 1_000_000L, elapsedSinceStart,
                            estimate, elapsed, avgTimes.get(this.currentDepth), avgTimes.get(this.currentDepth + 1)});

            // avg' = (layer_count * avg + time) / layer_count'
            avgTimes.put(this.currentDepth, (layerCount.get(this.currentDepth) * avgTimes.get(this.currentDepth) + elapsed) /
                    (layerCount.get(this.currentDepth) + 1));
            // layer_count' = layer_count + 1
            layerCount.put(this.currentDepth, layerCount.get(this.currentDepth) + 1);

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
     * Returns the maximum search depth for the current iteration.
     *
     * @return the maximum search depth for the current iteration
     */
    public int getDepth() {
        return this.currentDepth;
    }

}
