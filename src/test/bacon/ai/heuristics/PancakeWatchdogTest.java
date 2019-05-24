package bacon.ai.heuristics;

import bacon.Game;
import bacon.Maps;
import org.junit.Test;

import static org.junit.Assert.*;

public class PancakeWatchdogTest {

    @Test
    public void isPanic() throws InterruptedException {
        var watchdog = new PancakeWatchdog(500 + 100);

        Thread.sleep(400);
        assertFalse(watchdog.isPancake());
        Thread.sleep(100);
        assertTrue(watchdog.isPancake());

        Game.getGame().readMap(Maps.COMP_SQUARE);
        Game.getGame().getCurrentState().setMe(1);

        long deadline = System.nanoTime() + 200_000_000;
        var watchdog2 = new PancakeWatchdog( 100 + 100);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1);
            LegalMoves.getLegalRegularMoves(Game.getGame().getCurrentState(), 1, watchdog2);
            if (watchdog2.isTriggered()) break;
        }
        assertTrue(watchdog2.isTriggered());
        assertTrue(System.nanoTime() < deadline);

        long deadline2 = System.nanoTime() + 200_000_000;
        var watchdog3 = new PancakeWatchdog( 100 + 100);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1);
            LegalMoves.getLegalOverrideMoves(Game.getGame().getCurrentState(), 1, watchdog3);
            if (watchdog3.isTriggered()) break;
        }
        assertTrue(watchdog3.isTriggered());
        assertTrue(System.nanoTime() < deadline2);



    }
}