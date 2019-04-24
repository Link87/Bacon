package bacon.move;

import bacon.Game;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var state = Game.getGame().getCurrentState();

        state.getMap().getTileAt(8, 3).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 8).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 9).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 10).setOwner(state.getPlayerFromNumber(8));

        assertTrue(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(6),3, 11).isLegal());
        assertFalse(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(6),3, 12).isLegal());
        assertTrue(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(5),4, 3).isLegal());
        assertFalse(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(1),10, 10).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var state = Game.getGame().getCurrentState();

        state.getMap().getTileAt(8, 3).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 8).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 9).setOwner(state.getPlayerFromNumber(8));
        state.getMap().getTileAt(3, 10).setOwner(state.getPlayerFromNumber(8));

        MoveFactory.createMove(state, Game.getGame().getCurrentState().getPlayerFromNumber(6),3, 11).doMove();
        assertEquals(6, state.getMap().getTileAt(8, 3).getOwner().number);
        assertEquals(6, state.getMap().getTileAt(3, 8).getOwner().number);
        assertEquals(6, state.getMap().getTileAt(3, 9).getOwner().number);
        assertEquals(6, state.getMap().getTileAt(3, 10).getOwner().number);


    }
}