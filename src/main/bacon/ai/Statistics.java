package bacon.ai;

import java.util.*;

class Statistics {

    private static Statistics INSTANCE = new Statistics();

    private Map<Integer, Integer> stateCounts;
    private long startTimeStamp;
    private long stateTimeStamp;
    private List<Integer> stateTimes;
    private boolean inMeasuredState;

    /**
     * Initializes the stats. This deletes everything that was previously saved and starts the internal Timer.
     */
    void init() {
        INSTANCE = new Statistics();
    }

    /**
     * Adds a state to the statistics. Call either this or <code>enterMeasuredState</code> once per state.
     *
     * @param layer the layer of the state
     */
    void enterState(int layer) {
        this.stateCounts.putIfAbsent(layer, 0);
        this.stateCounts.put(layer, this.stateCounts.get(layer) + 1);
    }

    /**
     * Adds a state to the statistics. Call either this or <code>enterState</code> once per state.
     * This call starts an internal timer, that measures the computing time of the state. A call to
     * <code>leaveMeasuredState</code> stopping the timer is obligatory.
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
     * Call this when calculations of a state are finished that was added by <code>enterMeasuredState</code>.
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
     * @return a map containing the state counts, where <code>key</code> is the layer and <code>value</code> the count
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
     * Returns the default {@link Statistics} instance.
     *
     * @return the Statistics instance
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
