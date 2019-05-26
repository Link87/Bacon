package bacon.ai.heuristics;

import org.junit.Test;

import static org.junit.Assert.*;

public class IterationHeuristicTest {

    @Test
    public void doIteration() throws InterruptedException{
        IterationHeuristic timeHeuristic = new IterationHeuristic(500, 0);

        for (int i = 1; i <= 10; i++) {
            assertTrue(timeHeuristic.doIteration());
            assertEquals(i, timeHeuristic.getDepth());
        }

        Thread.sleep(110);

        assertFalse(timeHeuristic.doIteration());

        IterationHeuristic depthHeuristic = new IterationHeuristic(0, 10);

        assertTrue(depthHeuristic.doIteration());
        assertEquals(10, depthHeuristic.getDepth());
        assertFalse(depthHeuristic.doIteration());

    }

}