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
        Game.getGame().readMap(Maps.EXAMPLE);
        Game.getGame().getCurrentState().setMe(1);

        RandomRollout randroll = new RandomRollout(Game.getGame().getCurrentState());
        randroll.doRandRoll(1);
        System.out.println(Game.getGame().getCurrentState().getMap().toString());
    }
}
