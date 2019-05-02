package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeuristicsTest {

    @Test
    public void uncertaintyPhase(){
        Game.getGame().readMap(Maps.EXAMPLE);
        assertTrue("Uncertainty Phase false negative", Heuristics.isUncertaintyPhase(Game.getGame().getCurrentState()));

        Game.getGame().readMap(Maps.EXAMPLE_CERTAIN);
        assertFalse("Uncertainty Phase false positive", Heuristics.isUncertaintyPhase(Game.getGame().getCurrentState()));
    }

    @Test
    public void mobility(){
        Game.getGame().readMap(Maps.EXAMPLE_MOBILITY);

        /*for(Tile t: LegalMoves.legalMoves(Game.getGame().getCurrentState(), 1, MoveType.REGULAR)){
            System.out.println("("+ t.x + "," + t.y + ")");
        }

        for(Tile t: LegalMoves.legalMoves(Game.getGame().getCurrentState(), 1, MoveType.OVERRIDE)){
            System.out.println("("+ t.x + "," + t.y + ")");
        }*/

        assertEquals("Mobility Heuristic Error", 16 , Heuristics.mobility(Game.getGame().getCurrentState(),1));
    }
    //TODO Fix implement in map getTotalTile count and update the expected clustering value
    @Test
    public void clustering(){
        Game.getGame().readMap(Maps.EXAMPLE_CLUSTERING);
        assertEquals("Clustering heuristic error", 0.0 , Heuristics.clustering(Game.getGame().getCurrentState(),1), 0.01);
    }

    @Test
    public void bonus(){
        Game.getGame().readMap(Maps.EXAMPLE);

        //Bombs
        assertEquals("Bomb bonus heuristic error", 131.95, Heuristics.bonusBomb(Game.getGame().getCurrentState(), 1), 0.01 );
        //Override stones
        assertEquals("Override bonus heuristic error",105.15, Heuristics.bonusOverride(Game.getGame().getCurrentState(), 1), 0.1);
    }
}
