package bacon.move;

import bacon.Game;
import bacon.Tile;
import bacon.Maps;
import org.junit.Test;

import static bacon.Tile.Property.DEFAULT;
import static org.junit.Assert.*;

public class OverrideMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(9, 1).setOwner(Game.getGame().getCurrentState().getPlayerFromNumber(6));

        assertTrue(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(8),9, 1).isLegal());
        assertFalse(MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(8),10, 1).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(11, 3).setProperty(Tile.Property.EXPANSION);
        MoveFactory.createMove(Game.getGame().getCurrentState(), Game.getGame().getCurrentState().getPlayerFromNumber(8),11, 3).doMove();

        assertEquals(DEFAULT, map.getTileAt(11, 3).getProperty());
        assertEquals(5, Game.getGame().getCurrentState().getPlayerFromNumber(8).getOverrideStoneCount());
    }
}