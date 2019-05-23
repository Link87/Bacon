package bacon.move;

import bacon.Game;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var state = Game.getGame().getCurrentState();

        state.getMap().getTileAt(8, 3).setOwnerId(8);
        state.getMap().getTileAt(3, 8).setOwnerId(8);
        state.getMap().getTileAt(3, 9).setOwnerId(8);
        state.getMap().getTileAt(3, 10).setOwnerId(8);

        assertTrue(MoveFactory.createMove(Game.getGame().getCurrentState(), 6,3, 11).isLegal());
        assertFalse(MoveFactory.createMove(Game.getGame().getCurrentState(), 6,3, 12).isLegal());
        assertTrue(MoveFactory.createMove(Game.getGame().getCurrentState(), 5,4, 3).isLegal());
        assertFalse(MoveFactory.createMove(Game.getGame().getCurrentState(), 1,10, 10).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var state = Game.getGame().getCurrentState();

        state.getMap().getTileAt(8, 3).setOwnerId(8);
        state.getMap().getTileAt(3, 8).setOwnerId(8);
        state.getMap().getTileAt(3, 9).setOwnerId(8);
        state.getMap().getTileAt(3, 10).setOwnerId(8);

        MoveFactory.createMove(state, 6,3, 11).doMove();
        assertEquals(6, state.getMap().getTileAt(8, 3).getOwnerId());
        assertEquals(6, state.getMap().getTileAt(3, 8).getOwnerId());
        assertEquals(6, state.getMap().getTileAt(3, 9).getOwnerId());
        assertEquals(6, state.getMap().getTileAt(3, 10).getOwnerId());


    }
}