package bacon.ai.heuristics;

import bacon.Game;
import bacon.GamePhase;
import bacon.Maps;
import bacon.Tile;
import bacon.move.Move;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LegalMoveTest {

    @Test
    public void legalMoves() {
        Game.getGame().readMap(Maps.MODEXAMPLE);
        var map = Game.getGame().getCurrentState().getMap();
        int[] x1 = {7, 9, 9, 9, 5, 5, 5, 8, 6, 7, 6};
        int[] y1 = {5, 5, 6, 7, 7, 8, 9, 5, 9, 10, 10};
        int[] x2 = {8, 6, 5, 5, 5, 6, 8, 9, 9, 9, 7, 8};
        int[] y2 = {9, 9, 8, 7, 6, 5, 5, 5, 6, 8, 10, 10};
        int[] x3 = {8, 7, 6, 5, 5, 5, 6, 8, 9, 9, 9, 7};
        int[] y3 = {5, 5, 5, 6, 8, 9, 9, 9, 8, 7, 6, 10};

        Set<Tile> legalTiles1 = new HashSet<>() {
        };
        Set<Tile> legalTiles2 = new HashSet<>() {
        };
        Set<Tile> legalTiles3 = new HashSet<>() {
        };

        for (int i = 0; i < 12; i++) {
            if (i < 11) {
                legalTiles1.add(map.getTileAt(x1[i], y1[i]));
            }
            legalTiles2.add(map.getTileAt(x2[i], y2[i]));
            legalTiles3.add(map.getTileAt(x3[i], y3[i]));
        }

        //Regular Moves
        Set<? extends Move> evaluatedMoves1 = LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), 1, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves1) {
            assertTrue("Player 1: Illegal Move at: (" + mv.getX() + "," + mv.getY() + ")", legalTiles1.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        Set<? extends Move> evaluatedMoves2 = LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), 2, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves2) {
            assertTrue("Player 2: Illegal Move at: (" + mv.getX() + "," + mv.getY() + ")", legalTiles2.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        Set<? extends Move> evaluatedMoves3 = LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), 3, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves3) {
            assertTrue("Player 3: Illegal Move at: (" + mv.getX() + "," + mv.getY() + ")", legalTiles3.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        //Override Moves
        int[] x11 = {6, 6, 7, 7, 8, 8, 7};
        int[] y11 = {7, 8, 8, 6, 6, 7, 9};
        int[] x22 = {6, 6, 7, 7, 8, 8, 7};
        int[] y22 = {7, 6, 8, 7, 6, 8, 9};
        int[] x33 = {6, 7, 8, 6, 7, 8, 7};
        int[] y33 = {6, 7, 8, 8, 6, 7, 9};

        legalTiles1.clear();
        legalTiles2.clear();
        legalTiles3.clear();

        for (int i = 0; i < 7; i++) {
            legalTiles1.add(map.getTileAt(x11[i], y11[i]));
            legalTiles2.add(map.getTileAt(x22[i], y22[i]));
            legalTiles3.add(map.getTileAt(x33[i], y33[i]));
        }

        evaluatedMoves1 = LegalMoves.getLegalOverrideMoves(Game.getGame().getCurrentState(), 1, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves1) {
            assertTrue("Player 1: Illegal Override Move at: (" + mv.getX() + "," + mv.getY() + ")", legalTiles1.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        evaluatedMoves2 = LegalMoves.getLegalOverrideMoves(Game.getGame().getCurrentState(), 2, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves2) {
            assertTrue("Player 2: Illegal Override Move at: (" + mv.getX() + "," + mv.getY()  + ")", legalTiles2.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        evaluatedMoves3 = LegalMoves.getLegalOverrideMoves(Game.getGame().getCurrentState(), 3, new PancakeWatchdog(0));
        for (Move mv : evaluatedMoves3) {
            assertTrue("Player 3: Illegal Override Move at: (" + mv.getX() + "," + mv.getY() + ")", legalTiles3.contains(map.getTileAt(mv.getX(), mv.getY())));
        }

        //Bomb Moves
        legalTiles1.clear();

        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                if ((y < 10 && y > 4) || (x < 10 && x > 4)) {
                    legalTiles1.add(map.getTileAt(x, y));
                }
            }
        }

        Game.getGame().getCurrentState().setGamePhase(GamePhase.PHASE_TWO);

        evaluatedMoves1 = LegalMoves.getLegalBombMoves(Game.getGame().getCurrentState(), 1);
        for (Move mv : evaluatedMoves1) {
            assertTrue("Illegal Bomb Move at: (" + mv.getX() + "," + mv.getX() + ")", legalTiles1.contains(map.getTileAt(mv.getX(), mv.getY())));
        }
    }
}
