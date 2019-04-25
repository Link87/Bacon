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
        int[] x1 = {7,9,9,9,5,5,5,7};
        int[] y1 = {5,5,6,7,7,8,9,9};
        int[] x2 = {7,8,6,5,5,5,6,8,9,9,9};
        int[] y2 = {9,9,9,8,7,6,5,5,5,6,8};
        int[] x3 = {8,7,6,5,5,5,6,8,9,9,9};
        int[] y3 = {5,5,5,6,8,9,9,9,8,7,6};

        Set<Tile> legalTiles1 = new HashSet<>(){};
        Set<Tile> legalTiles2 = new HashSet<>(){};
        Set<Tile> legalTiles3 = new HashSet<>(){};

        for(int i = 0; i<8; i++){
            legalTiles1.add(map.getTileAt(x1[i],y1[i]));
            legalTiles2.add(map.getTileAt(x2[i],y2[i]));
            legalTiles3.add(map.getTileAt(x3[i],y3[i]));
        }

        Set<Tile> evaluatedTiles1 = LegalMoves.legalMoves(Game.getGame().getCurrentState(), 1, MoveType.REGULAR);
        for(Tile t: legalTiles1){
            assertTrue("Player 1: Doesn't contain: "+ t.x + "," + t.y ,evaluatedTiles1.contains(t));
        }

        Set<Tile> evaluatedTiles2 = LegalMoves.legalMoves(Game.getGame().getCurrentState(), 2, MoveType.REGULAR);
        for(Tile t: legalTiles2){
            assertTrue("Player 2: Doesn't contain: "+ t.x + "," + t.y ,evaluatedTiles2.contains(t));
        }

        Set<Tile> evaluatedTiles3 = LegalMoves.legalMoves(Game.getGame().getCurrentState(), 3, MoveType.REGULAR);
        for(Tile t: legalTiles3){
            assertTrue("Player 3: Doesn't contain: "+ t.x + "," + t.y ,evaluatedTiles3.contains(t));
        }
        


    }
}
