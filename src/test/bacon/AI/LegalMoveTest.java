package bacon.AI;

import bacon.Game;
import bacon.Maps;
import bacon.Tile;
import bacon.ai.LegalMoves;
import bacon.ai.MoveType;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LegalMoveTest {

    @Test
    public void legalMoves(){
        Game.getGame().readMap(Maps.EXAMPLE);
        var map = Game.getGame().getCurrentState().getMap();
        int[] x = {7,9,9,9,5,5,5,7};
        int[] y = {5,5,6,7,7,8,9,9};

        Set<Tile> legalTiles = new HashSet<>(){};

        for(int i = 0; i<8; i++){
            legalTiles.add(map.getTileAt(x[i],y[i]));
        }
        Set<Tile> evaluatedTiles = LegalMoves.legalMoves(Game.getGame().getCurrentState(), 1, MoveType.REGULAR);

        for(Tile t: legalTiles){
            assertTrue("Doesn't contain: "+ t.x + "," + t.y ,evaluatedTiles.contains(t));
        }
    }

}
