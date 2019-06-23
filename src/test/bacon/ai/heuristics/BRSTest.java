package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import bacon.Player;
import bacon.ai.BRSNode;
import bacon.move.Move;
import bacon.move.RegularMove;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BRSTest {

    @Test
    public void legal() {
        Game.getGame().readMap(Maps.EXAMPLE_CERTAIN);
        Game.getGame().getCurrentState().setMe(1);

        BRSNode root = new BRSNode(4, 20, false, true, false, -Double.MAX_VALUE, Double.MAX_VALUE ,new PancakeWatchdog(0));
        root.evaluateNode();
        Move bestMove = root.getBestMove();

        assertNotNull("BRS returns no move", bestMove);
        System.out.println("Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");

        System.out.println(Game.getGame().getCurrentState().getMap().toString());

        assertTrue("BRS returns illegal move", bestMove.isLegal());
    }

    @Test
    public void bonusCapture() {
        //Does BRS try to capture bonus tiles ?
        Game.getGame().readMap(Maps.EXAMPLE_BRS_BONUS);
        Game.getGame().getCurrentState().setMe(1);
        Player me = Game.getGame().getCurrentState().getPlayerFromId(1);

        for (int i = 0; i < 10; i++) {
            BRSNode root = new BRSNode(6, 5, true, true, false, -Double.MAX_VALUE, Double.MAX_VALUE, new PancakeWatchdog(0));
            root.evaluateNode();
            Move bestMove = root.getBestMove();

            assertNotNull("BRS returns no move", bestMove);
            System.out.println("Player 1 Best Move: " + "(" + bestMove.getX() + "," + bestMove.getY() + ")");
            assertTrue("BRS returns illegal move", bestMove.isLegal());

            bestMove.doMove();
            System.out.println(Game.getGame().getCurrentState().getMap().toString());

            for (int j = 2; j < 4; j++) {
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
                if(doMove==null){
                    System.out.println("Player " + j +" could not make a regular Move so we skip him.");
                    continue;
                }
                System.out.println("Player " + j + " doMove: " + "(" + doMove.getX() + "," + doMove.getY() + ")");
                doMove.doMove();
                System.out.println(Game.getGame().getCurrentState().getMap().toString());
            }
        }
        System.out.println("BombCount: " + me.getBombCount() + "; OverrideCount: " + me.getOverrideStoneCount());
    }

}