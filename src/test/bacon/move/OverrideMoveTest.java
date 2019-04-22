package bacon.move;

import bacon.Game;
import bacon.Tile;
import org.junit.Test;

import static bacon.Tile.Property.DEFAULT;
import static org.junit.Assert.*;

public class OverrideMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(9, 1).setOwner(Game.getGame().getCurrentState().getPlayerFromNumber(6));

        assertTrue(Move.createNewMove(0, map, Game.getGame().getCurrentState().getPlayerFromNumber(8),9, 1, 0).isLegal());
        assertFalse(Move.createNewMove(0, map, Game.getGame().getCurrentState().getPlayerFromNumber(8),10, 1, 0).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(11, 3).setProperty(Tile.Property.EXPANSION);
        Move.createNewMove(0, map, Game.getGame().getCurrentState().getPlayerFromNumber(8),11, 3, 0).doMove();

        assertEquals(DEFAULT, map.getTileAt(11, 3).getProperty());
        assertEquals(5, Game.getGame().getCurrentState().getPlayerFromNumber(8).getOverrideStoneCount());
    }
}