package bacon.ai.heuristics;

import bacon.Game;
import bacon.GameState;
import bacon.Maps;
import bacon.Tile;
import bacon.ai.BRSNode;
import bacon.Player;
import bacon.move.BuildMove;
import bacon.move.Move;
import bacon.move.RegularMove;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

public class BRSTest {

    @Test
    public void legal() {
        Game.getGame().readMap(Maps.EXAMPLE_BRS);
        Player me = Game.getGame().getCurrentState().getPlayerFromNumber(1);
        Game.getGame().getCurrentState().setMe(me);

        BRSNode root = new BRSNode(0, 5, 1, true, null);
        root.evaluateNode();
        Move bestMove = root.getBestMove();

        assertTrue("BRS returns no move", bestMove != null);
        System.out.println("Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");

        System.out.println(Game.getGame().getCurrentState().getMap().toString());

        assertTrue("BRS returns illegal move", bestMove.isLegal());

        //Does BRS try to capture bonus tiles ?
        Game.getGame().readMap(Maps.EXAMPLE_BRS2);
        me = Game.getGame().getCurrentState().getPlayerFromNumber(1);
        Game.getGame().getCurrentState().setMe(me);

        for(int i = 0; i < 20; i++) {
            root = new BRSNode(0, 3, 5, true, null);
            root.evaluateNode();
            bestMove = root.getBestMove();

            assertTrue("BRS returns no move", bestMove != null);
            System.out.println("Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");


            assertTrue("BRS returns illegal move", bestMove.isLegal());

            bestMove.doMove();
            System.out.println(Game.getGame().getCurrentState().getMap().toString());

            for(int j = 2; j < 4; j++) {
                Set<RegularMove> move = LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), j);
                int size = move.size();
                int index = (int) (Math.random() * (size));

                Move doMove = null;
                for (Move m : move) {
                    if (index == 0) {
                        doMove = m;
                        break;
                    }
                    index--;
                }

                doMove.doMove();
            }
        }
        System.out.println("BombCount: " + me.getBombCount() + "OverrideCount: " + me.getOverrideStoneCount());
    }

}