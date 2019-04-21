package bacon;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void processMessage() {
        Game.getGame().processMessage("0200000034320a300a3120310a3220360a2d202d206220782032202d0a2d203020782031202d202d0a3120312035203c2d3e2034203020310a");
        assertEquals(2, Game.getGame().getTotalPlayerCount());
        assertEquals(0, Game.getGame().getPlayerFromNumber(1).getOverrideStoneCount());
        assertEquals(1, Game.getGame().getPlayerFromNumber(1).getBombCount());
        assertEquals(1, Game.getGame().getBombRadius());
        assertEquals(2, Game.getGame().getMap().height);
        assertEquals(6, Game.getGame().getMap().width);
        assertEquals(GamePhase.PHASE_ONE, Game.getGame().getGamePhase());
    }
}