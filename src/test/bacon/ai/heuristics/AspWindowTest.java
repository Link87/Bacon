package bacon.ai.heuristics;

import bacon.*;
import bacon.ai.AI;
import bacon.move.Move;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class AspWindowTest {

    @Ignore
    @Test
    public void aspWindowTest() {
        Game.getGame().readMap(Maps.COMP_SQUARE);
        Game.getGame().getCurrentState().setMe(1);

        //run time-limited BRS with aspiration window ON
        Config aspConfig = new Config(null, 0, true, true, 0, true, true);
        Move aspMove = AI.getAI().requestMove(1000, 0, aspConfig, Game.getGame().getCurrentState());

        //run time-limited BRS with aspiration window OFF as control
        Config config = new Config(null, 0, true, true, 0, false, true);
        Move move = AI.getAI().requestMove(1000, 0, config, Game.getGame().getCurrentState());
    }

}