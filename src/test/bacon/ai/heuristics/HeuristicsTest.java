package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import bacon.Tile;
import bacon.move.Move;
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
    public void mobility() {
        Game.getGame().readMap(Maps.EXAMPLE_MOBILITY);

        System.out.println("Regular Moves");
        for(Tile t: LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 1, Move.Type.REGULAR)) {
            System.out.println("(" + t.x + "," + t.y + ")");
        }
        System.out.println("Override Moves");
        for(Tile t: LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 1, Move.Type.OVERRIDE)){
            System.out.println("("+ t.x + "," + t.y + ")");
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
        assertEquals("Bomb bonus heuristic error", 131.95, Heuristics.bonusBomb(Game.getGame().getCurrentState(), 1), 0.01);
        //Override stones
        assertEquals("Override bonus heuristic error", 105.15, Heuristics.bonusOverride(Game.getGame().getCurrentState(), 1), 0.1);
    }
}
