package bacon.ai.heuristics;

import bacon.Game;
import bacon.GamePhase;
import bacon.GameState;
import bacon.Maps;
import bacon.move.BombMove;
import bacon.move.MoveFactory;
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

    @Ignore("Mobility currently counts bonus tile and choice tile options as separate moves.")
    @Test
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

    @Test
    public void overrideStability() {
        Game.getGame().readMap(Maps.EXAMPLE_OVERRIDE_STABILITY);
        Game.getGame().getCurrentState().setMe(1);
        Game.getGame().getCurrentState().getMap().assignLineGeometryPlayers();
        assertEquals("Override stability heuristic error", 14, Heuristics.overrideStability(Game.getGame().getCurrentState(), 1), 0.1);
    }

    @Test
    public void bonus() {
        Game.getGame().readMap(Maps.EXAMPLE);

        //Bombs
        assertEquals("Bomb bonus heuristic error", 40, Heuristics.bonusBomb(Game.getGame().getCurrentState(), 1), 0.01);
        //Override stones
        assertEquals("Override bonus heuristic error", 36, Heuristics.bonusOverride(Game.getGame().getCurrentState(), 1), 0.1);
    }

    @Test
    public void bombingPhaseHeuristic() {
        Game.getGame().readMap(Maps.EXAMPLE_BOMBINGPHASE);
        GameState state =  Game.getGame().getCurrentState();
        state.setGamePhase(GamePhase.PHASE_TWO);
        assertEquals("Bombing Phase heuristic error", -1.04,
                Heuristics.bombingPhaseHeuristic(state, (BombMove) MoveFactory.createMove(state, 1, 7, 7)), 0.01);
    }
}
