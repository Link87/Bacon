package bacon;

import org.junit.Test;

import static org.junit.Assert.*;

public class BombMoveTest {

    @Test
    public void isLegal() {
        Game.getGame().readMap(Maps.STARFISH);
        // test if bomb count is handled right
        new BombMove(0, Game.getGame().getCurrentState().getMap(), Game.getGame().getCurrentState().getPlayerFromNumber(1),
                0, 0, 0).doMove();
        assertFalse(new BombMove(0, Game.getGame().getCurrentState().getMap(), Game.getGame().getCurrentState().getPlayerFromNumber(1),
                0, 0, 0).isLegal());
    }

    @Test
    public void doMove1() {
        Game.getGame().readMap(Maps.COMP_SQUARE);
        // test if tiles are turned to holes
        BombMove bomb1 = new BombMove(0, Game.getGame().getCurrentState().getMap(), Game.getGame().getCurrentState().getPlayerFromNumber(1),
                9, 0, 0);
        bomb1.doMove();
        // tiles to the left
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(9, 0).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(8, 0).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(7, 0).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(6, 0).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(5, 0).getProperty());

        // diagonal: lower left corner
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(10, 0).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(11, 0).getProperty());

        // down
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(6, 3).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(6, 4).getProperty());

        // test if additional transition is handled
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(32, 0).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(33, 0).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(34, 0).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(35, 0).getProperty());
    }

    @Test
    public void doMove2() {
        Game.getGame().readMap(Maps.STARFISH);
        // test if tiles are turned to holes
        BombMove bomb1 = new BombMove(0, Game.getGame().getCurrentState().getMap(), Game.getGame().getCurrentState().getPlayerFromNumber(1),
                8, 4, 0);
        bomb1.doMove();
        // upwards
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(8, 4).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(8, 3).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(8, 2).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(8, 1).getProperty());

        // across the left transition border and then downwards and left (full test)
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(4, 8).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(4, 9).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(4, 10).getProperty());

        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(3, 8).getProperty());
        assertEquals(Tile.Property.HOLE, Game.getGame().getCurrentState().getMap().getTileAt(3, 9).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(3, 10).getProperty());

        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(2, 8).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(2, 9).getProperty());
        assertEquals(Tile.Property.DEFAULT, Game.getGame().getCurrentState().getMap().getTileAt(2, 10).getProperty());
    }
}