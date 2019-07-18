package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import bacon.ai.RandomRollout;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RandRollTest {
    @Test
    public void legal() {
        Game.getGame().readMap(Maps.COMP_SQUARE);
        Game.getGame().getCurrentState().setMe(1);
        System.out.println(Game.getGame().getCurrentState().getMap().toString());

        long startTimeStamp = System.nanoTime();
        RandomRollout randroll = new RandomRollout(Game.getGame().getCurrentState(), 20, startTimeStamp + 500000000);
        long endTimeStamp = System.nanoTime();

        double timeElapsed = (endTimeStamp - startTimeStamp)/1000000;
        System.out.println(Game.getGame().getCurrentState().getMap().toString());
        System.out.println("Time Elapsed: " + timeElapsed + "ms, last complete iteration: " + randroll.getTotalIteration());
    }
}
