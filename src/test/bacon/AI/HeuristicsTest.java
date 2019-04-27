package bacon.AI;

import bacon.Game;
import bacon.Maps;
import bacon.Tile;
import bacon.ai.Heuristics;
import bacon.ai.LegalMoves;
import bacon.ai.MoveType;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeuristicsTest {

    @Test
    public void uncertaintyPhase(){
        Game.getGame().readMap(Maps.EXAMPLE);
        assertTrue("Uncertainty Phase false negative",Heuristics.uncertaintyPhase(Game.getGame().getCurrentState()));

        Game.getGame().readMap(Maps.EXAMPLE_CERTAIN);
        assertFalse("Uncertainty Phase false positive", Heuristics.uncertaintyPhase(Game.getGame().getCurrentState()));
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

        assertEquals("Mobility Heuristic Error", 16 , (int) Heuristics.mobility(Game.getGame().getCurrentState(),1));
    }

    @Test
    public void stability(){
        Game.getGame().readMap(Maps.EXAMPLE_STABILITY);
        assertEquals("Stability heuristic error", 35 ,Heuristics.stability(Game.getGame().getCurrentState(), 1), 0.01);
    }

    @Test
    public void clustering(){
        Game.getGame().readMap(Maps.EXAMPLE_CLUSTERING);
        assertEquals("Clustering heuristic error", 11.37 , Heuristics.clustering(Game.getGame().getCurrentState(),1), 0.01);
    }

    @Test
    public void bonus(){
        Game.getGame().readMap(Maps.EXAMPLE);

        //Bombs
        assertEquals("Bomb bonus heuristic error", 200, (int) Heuristics.bonusBomb(Game.getGame().getCurrentState(), 1) );
        //Override stones
        assertEquals("Override bonus heuristic error",180, Heuristics.bonusOverride(Game.getGame().getCurrentState(), 1), 0.1);
    }
}
