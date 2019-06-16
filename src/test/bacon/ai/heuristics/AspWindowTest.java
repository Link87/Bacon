package bacon.ai.heuristics;

import bacon.*;
import bacon.ai.AI;
import bacon.move.Move;
import org.junit.Test;

import static org.junit.Assert.*;

public class AspWindowTest {

    @Test
    public void aspWindowTest() {
        Game.getGame().readMap(Maps.COMP_SQUARE);
        Game.getGame().getCurrentState().setMe(1);

        Config aspConfig = new Config(true, true, 0, true);
        Move aspMove = AI.getAI().requestMove(1000, 0, aspConfig, Game.getGame().getCurrentState());

        Config config = new Config(true, true, 0, false);
        Move move = AI.getAI().requestMove(1000, 0, config, Game.getGame().getCurrentState());
    }

}