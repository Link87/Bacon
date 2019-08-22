package bacon;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GameStateTest {

    @Test
    public void getBombRadius() {
        assertEquals(Game.getGame().getBombRadius(), Game.getGame().getCurrentState().getBombRadius());
    }

    @Test
    public void getTotalPlayerCount() {
        assertEquals(Game.getGame().getTotalPlayerCount(), Game.getGame().getCurrentState().getTotalPlayerCount());
    }
}