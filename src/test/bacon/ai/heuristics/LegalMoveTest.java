package bacon.ai.heuristics;

import bacon.*;
import bacon.move.Move;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LegalMoveTest {

    @Test
    public void legalMoves(){
        Game.getGame().readMap(Maps.MODEXAMPLE);
        var map = Game.getGame().getCurrentState().getMap();
        int[] x1 = {7,9,9,9,5,5,5,8,6,7,6};
        int[] y1 = {5,5,6,7,7,8,9,5,9,10,10};
        int[] x2 = {8,6,5,5,5,6,8,9,9,9,7,8};
        int[] y2 = {9,9,8,7,6,5,5,5,6,8,10,10};
        int[] x3 = {8,7,6,5,5,5,6,8,9,9,9,7};
        int[] y3 = {5,5,5,6,8,9,9,9,8,7,6,10};

        Set<Tile> legalTiles1 = new HashSet<>(){};
        Set<Tile> legalTiles2 = new HashSet<>(){};
        Set<Tile> legalTiles3 = new HashSet<>(){};

        for(int i = 0; i<12; i++){
            if(i < 11){
                    legalTiles1.add(map.getTileAt(x1[i],y1[i]));
            }
            legalTiles2.add(map.getTileAt(x2[i],y2[i]));
            legalTiles3.add(map.getTileAt(x3[i],y3[i]));
        }

        //Regular Moves
        Set<Tile> evaluatedTiles1 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 1, Move.Type.REGULAR);
        for(Tile t: evaluatedTiles1){
            assertTrue("Player 1: Illegal Move at: ("+ t.x + "," + t.y +")" ,legalTiles1.contains(t));
        }

        Set<Tile> evaluatedTiles2 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 2, Move.Type.REGULAR);
        for(Tile t: evaluatedTiles2){
            assertTrue("Player 2: Illegal Move at: ("+ t.x + "," + t.y +")" , legalTiles2.contains(t));
        }

        Set<Tile> evaluatedTiles3 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 3, Move.Type.REGULAR);
        for(Tile t: evaluatedTiles3){
            assertTrue("Player 3: Illegal Move at: ("+ t.x + "," + t.y +")" , legalTiles3.contains(t));
        }

        //Override Moves
        int[] x11 = {6,6,7,7,8,8,7};
        int[] y11 = {7,8,8,6,6,7,9};
        int[] x22 = {6,6,7,7,8,8,7};
        int[] y22 = {7,6,8,7,6,8,9};
        int[] x33 = {6,7,8,6,7,8,7};
        int[] y33 = {6,7,8,8,6,7,9};

        legalTiles1.clear();
        legalTiles2.clear();
        legalTiles3.clear();

        for(int i = 0; i < 7; i++){
            legalTiles1.add(map.getTileAt(x11[i],y11[i]));
            legalTiles2.add(map.getTileAt(x22[i],y22[i]));
            legalTiles3.add(map.getTileAt(x33[i],y33[i]));
        }

        evaluatedTiles1 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 1, Move.Type.OVERRIDE);
        for(Tile t: evaluatedTiles1){
            assertTrue("Player 1: Illegal Override Move at: ("+ t.x + "," + t.y +")" ,legalTiles1.contains(t));
        }

        evaluatedTiles2 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 2, Move.Type.OVERRIDE);
        for(Tile t: evaluatedTiles2){
            assertTrue("Player 2: Illegal Override Move at: ("+ t.x + "," + t.y +")" , legalTiles2.contains(t));
        }

        evaluatedTiles3 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 3, Move.Type.OVERRIDE);
        for(Tile t: evaluatedTiles3){
            assertTrue("Player 3: Illegal Override Move at: ("+ t.x + "," + t.y +")" , legalTiles3.contains(t));
        }

        //Bomb Moves
        legalTiles1.clear();

        for(int x = 0; x < map.width; x++){
            for(int y = 0; y < map.height; y++){
                if((y < 10 && y > 4)||(x < 10 && x > 4)){
                    legalTiles1.add(map.getTileAt(x,y));
                }
            }
        }

        Game.getGame().getCurrentState().setGamePhase(GamePhase.PHASE_TWO);

        evaluatedTiles1 = LegalMoves.getLegalMoveTiles(Game.getGame().getCurrentState(), 1, Move.Type.BOMB);
        for(Tile t: evaluatedTiles1){
            assertTrue("Illegal Bomb Move at: ("+ t.x + "," + t.y +")" ,legalTiles1.contains(t));
        }
    }
}
