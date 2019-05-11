package bacon.ai.heuristics;

import bacon.Game;
import bacon.GameState;
import bacon.Maps;
import bacon.Tile;
import bacon.ai.BRSNode;
import bacon.Player;
import bacon.move.Move;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;

public class BRSTest {

    @Test
    public void legal() {
        Game.getGame().readMap(Maps.EXAMPLE_CERTAIN);
        Player me = Game.getGame().getCurrentState().getPlayerFromNumber(1);
        Game.getGame().getCurrentState().setMe(me);

        BRSNode root = new BRSNode(0, 5, 2, true, null);
        root.evaluateNode();
        Move bestMove = root.getBestMove();

        assertTrue("BRS returns no move", bestMove != null);
        System.out.println("Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");

        System.out.println(Game.getGame().getCurrentState().getMap().toString());

        assertTrue("BRS returns illegal move", bestMove.isLegal());
    }

}