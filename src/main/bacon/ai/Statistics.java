package bacon.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics {

    private static Statistics INSTANCE = new Statistics();

    private List<Integer> stateCounts;
    private long startTime;

    /**
     * Initializes the stats. This deletes everything that was previously saved and starts the internal Timer.
     */
    void init() {
        INSTANCE = new Statistics();
    }

    /**
     * Adds a state to the statistics. Call this once per state.
     *
     * @param layer the layer of the state
     */
    public void enteredState(int layer) {
        stateCounts.set(layer, stateCounts.get(layer) + 1);
    }

    /**
     * Returns the time in nanoseconds since the timer was started.
     *
     * @return the nanoseconds that elapsed since timer start
     */
    long getElapsedNanos() {
        return System.nanoTime() - startTime;
    }

    /**
     * Returns the amount of states in each layer.
     *
     * @return a list containing the state counts, where the index determines the layer
     */
    List<Integer> getStateCounts() {
        return Collections.unmodifiableList(stateCounts);
    }

    /**
     * Returns the total state count of all layers.
     *
     * @return the total state count
     */
    int getTotalStateCount() {
        return stateCounts.stream().reduce(Integer::sum).orElse(0);
    }

    /**
     * Returns the amount of leaves that were visited. This assumes that leaves are the states in the lowest layer.
     *
     * @return the leaf count
     */
    int getLeafCount() {
        if (stateCounts.size() > 0)
            return stateCounts.get(stateCounts.size() - 1);
        else return 0;
    }

    /**
     * Returns the default {@link Statistics} instance.
     *
     * @return the Statistics instance
     */
    Statistics getStatistics() {
        return INSTANCE;
    }

    private Statistics() {
        this.stateCounts = new ArrayList<>();
        this.startTime = System.nanoTime();
    }
}
