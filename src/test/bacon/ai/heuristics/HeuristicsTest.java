package bacon.ai.heuristics;

import bacon.Game;
import bacon.GameState;
import bacon.Maps;
import bacon.move.OverrideMove;
import bacon.move.RegularMove;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeuristicsTest {

    @Test
    public void uncertaintyPhase() {
        Game.getGame().readMap(Maps.EXAMPLE);
        assertTrue("Uncertainty Phase false negative", Heuristics.isUncertaintyPhase(Game.getGame().getCurrentState()));

        Game.getGame().readMap(Maps.EXAMPLE_CERTAIN);
        assertFalse("Uncertainty Phase false positive", Heuristics.isUncertaintyPhase(Game.getGame().getCurrentState()));
    }

    @Test
    @Ignore("Mobility currently counts bonus tile and choice tile options as separate moves.")
    public void mobility() {
        Game.getGame().readMap(Maps.EXAMPLE_MOBILITY);

        System.out.println("Regular Moves");
        for(RegularMove mv : LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), 1)) {
            System.out.println("(" + mv.getX() + "," + mv.getY() + ")");
        }
        System.out.println("Override Moves");
        for(OverrideMove mv: LegalMoves.getLegalOverrideMoves(Game.getGame().getCurrentState(), 1)){
            System.out.println("("+ mv.getX() + "," + mv.getY() + ")");
        }

        assertEquals("Mobility Heuristic Error", 19, Heuristics.mobility(Game.getGame().getCurrentState(), 1));
    }

    //TODO Fix implement in map getTotalTile count and update the expected clustering value
    @Test
    public void clustering() {
        Game.getGame().readMap(Maps.EXAMPLE_CLUSTERING);
        assertEquals("Clustering heuristic error", 1.36, Heuristics.clustering(Game.getGame().getCurrentState(), 1), 0.01);
    }

    @Test
    public void bonus() {
        Game.getGame().readMap(Maps.EXAMPLE);

        //Bombs
        assertEquals("Bomb bonus heuristic error", 1319.5, Heuristics.bonusBomb(Game.getGame().getCurrentState(), 1), 0.01);
        //Override stones
        assertEquals("Override bonus heuristic error", 1051.5, Heuristics.bonusOverride(Game.getGame().getCurrentState(), 1), 0.1);
    }

    @Test
    public void bombingPhaseHeuristic() {
        Game.getGame().readMap(Maps.EXAMPLE_BOMBINGPHASE);
        GameState state =  Game.getGame().getCurrentState();
        assertEquals("Bombing Phase heuristic error", 2.0,
                Heuristics.bombingPhaseHeuristic(state, 1, state.getMap().getTileAt(7,7)), 0.01);
    }
}
