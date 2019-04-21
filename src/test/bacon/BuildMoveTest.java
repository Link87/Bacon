package bacon;

import org.junit.Test;

import static org.junit.Assert.*;

public class BuildMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(8, 3).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 8).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 9).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 10).setOwner(Game.getGame().getPlayerFromNumber(8));

        assertTrue(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(6),3, 11, 0).isLegal());
        assertFalse(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(6),3, 12, 0).isLegal());
        assertTrue(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(5),4, 3, 0).isLegal());
        assertFalse(Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(1),10, 10, 0).isLegal());

    }

    @Test
    public void doMove() {
        Game.getGame().readMap(bacon.Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        map.getTileAt(8, 3).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 8).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 9).setOwner(Game.getGame().getPlayerFromNumber(8));
        map.getTileAt(3, 10).setOwner(Game.getGame().getPlayerFromNumber(8));

        Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(6),3, 11, 0).doMove();
        assertEquals(6, map.getTileAt(8, 3).getOwner().number);
        assertEquals(6, map.getTileAt(3, 8).getOwner().number);
        assertEquals(6, map.getTileAt(3, 9).getOwner().number);
        assertEquals(6, map.getTileAt(3, 10).getOwner().number);


    }
}