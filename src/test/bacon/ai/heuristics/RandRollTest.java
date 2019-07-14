package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import bacon.ai.BRSNode;
import bacon.ai.RandomRollout;
import bacon.move.Move;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RandRollTest {
    @Test
    public void legal() {
        Game.getGame().readMap(Maps.EXAMPLE_BRS_UNCERTAIN);
        Game.getGame().getCurrentState().setMe(1);
        long startTimeStamp = System.nanoTime();

        System.out.println(Game.getGame().getCurrentState().getMap().toString());
        RandomRollout randroll = new RandomRollout(Game.getGame().getCurrentState(), 1, startTimeStamp);
        randroll.doRandRoll(1);
        long endTimeStamp = System.nanoTime();
        double timeElapsed = (endTimeStamp - startTimeStamp)/1000000;

        System.out.println(Game.getGame().getCurrentState().getMap().toString());
        System.out.println("Time Elapsed: " + timeElapsed + "ms");
    }
}
