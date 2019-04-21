package bacon;

import org.junit.Test;

import static bacon.Tile.Property.DEFAULT;
import static org.junit.Assert.*;

public class OverrideMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(9, 1).setOwner(Game.getGame().getPlayerFromNumber(6));

        assertTrue(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(8),9, 1, 0).isLegal());
        assertFalse(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(8),10, 1, 0).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(11, 3).setProperty(Tile.Property.EXPANSION);
        Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(8),11, 3, 0).doMove();

        assertEquals(DEFAULT, map.getTileAt(11, 3).getProperty());
        assertEquals(5, Game.getGame().getPlayerFromNumber(8).getOverrideStoneCount());
    }
}