package bacon.ai;

import java.util.*;

/**
 * Records statistics of calculations for logging purposes.
 * <p>
 * Each move calculation should use a separate instance of {@code Statistics}.
 * A new one can be initialized by calling {@link #init()}.
 * <p>
 * This measures the total amount of time needed, as well as the
 * total amount of states in each layer of the search tree.
 * Additionally, the computing cost of leaf nodes can be measured.
 */
class Statistics {

    private static Statistics INSTANCE = new Statistics();

    private Map<Integer, Integer> stateCounts;
    private long startTimeStamp;
    private long stateTimeStamp;
    private List<Integer> stateTimes;
    private boolean inMeasuredState;

    /**
     * Initializes a new {@code Statistics} instance.
     * <p>
     * This deletes everything that was previously saved and starts the internal timer.
     */
    void init() {
        INSTANCE = new Statistics();
    }

    /**
     * Adds a state to the statistics.
     * <p>
     * Call either this or {@link #enterMeasuredState(int)} once per state.
     *
     * @param layer the layer of the state
     */
    void enterState(int layer) {
        this.stateCounts.putIfAbsent(layer, 0);
        this.stateCounts.put(layer, this.stateCounts.get(layer) + 1);
    }

    /**
     * Adds a state to the statistics.
     * <p>
     * Call either this or {@link #enterState(int)} once per state.
     * <p>
     * This call starts an internal timer, that measures the computing time of the state.
     * A call to {@link #leaveMeasuredState()} stopping the timer is obligatory.
     *
     * @param layer the layer of the state
     * @throws IllegalStateException when still measuring a state
     */
    void enterMeasuredState(int layer) {
        if (this.inMeasuredState) throw new IllegalStateException("Cannot enter another measured state.");
        this.inMeasuredState = true;
        enterState(layer);
        this.stateTimeStamp = System.nanoTime();
    }

    /**
     * Call this when calculations of a state are finished that was added by {@link #enterMeasuredState(int)}.
     *
     * @throws IllegalStateException when not measuring a state
     */
    void leaveMeasuredState() {
        if (!this.inMeasuredState) throw new IllegalStateException("Cannot leave state.");
        this.inMeasuredState = false;
        this.stateTimes.add((int) (System.nanoTime() - this.stateTimeStamp));
    }

    /**
     * Returns the time in nanoseconds since the timer was started.
     *
     * @return the nanoseconds that elapsed since timer start
     */
    long getElapsedNanos() {
        return System.nanoTime() - this.startTimeStamp;
    }

    /**
     * Returns the amount of states in each layer.
     *
     * @return a map containing the state counts, where the key is the layer and the value the state count
     */
    Map<Integer, Integer> getStateCounts() {
        return Collections.unmodifiableMap(this.stateCounts);
    }

    /**
     * Returns the total state count of all layers.
     *
     * @return the total state count
     */
    int getTotalStateCount() {
        return this.stateCounts.values().stream().reduce(Integer::sum).orElse(0);
    }

    /**
     * Returns the time statistics of all measured states.
     *
     * @return the measurement results
     */
    IntSummaryStatistics getStateMeasurementResults() {
        return this.stateTimes.stream().collect(IntSummaryStatistics::new,
                IntSummaryStatistics::accept, IntSummaryStatistics::combine);
    }

    /**
     * Returns the amount of leaves that were visited. This assumes that leaves are the states in the lowest layer.
     *
     * @return the leaf count
     */
    int getLeafCount() {
        if (this.stateCounts.size() > 0)
            return this.stateCounts.get(this.stateCounts.size() - 1);
        else return 0;
    }

    /**
     * Returns the default {@code Statistics} instance.
     *
     * @return the {@code Statistics} instance
     */
    static Statistics getStatistics() {
        return INSTANCE;
    }

    private Statistics() {
        this.stateCounts = new HashMap<>();
        this.stateTimes = new ArrayList<>();
        this.startTimeStamp = System.nanoTime();
        this.inMeasuredState = false;
    }
}
