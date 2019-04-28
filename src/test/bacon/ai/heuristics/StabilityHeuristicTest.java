package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import org.junit.Test;

import static org.junit.Assert.*;

public class StabilityHeuristicTest {

    @Test
    public void stability(){
        Game.getGame().readMap(Maps.EXAMPLE_STABILITY);
        assertEquals("Stability heuristic error", 41, StabilityHeuristic.stability(Game.getGame().getCurrentState(), 1), 0.01);
    }

}