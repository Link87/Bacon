package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import bacon.ai.BRSNode;
import bacon.Player;
import bacon.move.Move;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class BRSTest {

    @Test
    public void legal() {
        Game.getGame().readMap(Maps.EXAMPLE);
        Player me = Game.getGame().getCurrentState().getPlayerFromNumber(1);
        Game.getGame().getCurrentState().setMe(me);
        BRSNode root = new BRSNode(0, 5, 2, true, null);
        root.doBRS();
        Move bestMove = root.getBestMove();

        assertTrue("BRS returns no move", bestMove != null);
        assertTrue("BRS returns illegal move", bestMove.isLegal());
        System.out.println("Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");
    }

}