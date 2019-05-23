package bacon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapTest {

    private static Tile.Property[][] properties = {
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.INVERSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},

            {Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT},
            {Tile.Property.DEFAULT, Tile.Property.CHOICE, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.INVERSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT},
            {Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT},
            {Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT},
            {Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT, Tile.Property.DEFAULT},

            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.CHOICE, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE},
            {Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE}
    };

    private static int[][] playerIds = new int[15][15];

    static {
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                playerIds[x][y] = 0;
            }
        }
        playerIds[6][6] = 1;
        playerIds[7][6] = 2;
        playerIds[8][6] = 3;
        playerIds[6][7] = 3;
        playerIds[7][7] = 1;
        playerIds[8][7] = 2;
        playerIds[6][8] = 2;
        playerIds[7][8] = 3;
        playerIds[8][8] = 1;

    }

    @Test
    public void readFromString() {
        Game.getGame().readMap(Maps.EXAMPLE);

        var map = Game.getGame().getCurrentState().getMap();
        assertEquals(15, map.width);
        assertEquals(15, map.height);

        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                Tile tile = map.getTileAt(x, y);
                assertEquals("Wrong value in tile: " + x + ", " + y, x, tile.x);
                assertEquals("Wrong value in tile: " + x + ", " + y, y, tile.y);
                assertEquals("Wrong value in tile: " + x + ", " + y, properties[y][x], tile.getProperty());
                if (playerIds[x][y] == 0)
                    assertEquals("Wrong value in tile: " + x + ", " + y, Player.NULL_PLAYER_ID, tile.getOwnerId());
                else
                    assertEquals("Wrong value in tile: " + x + ", " + y, playerIds[x][y], tile.getOwnerId());
            }
        }

        assertEquals(map.getTileAt(7, 7).getTransition(Direction.UP.id), map.getTileAt(7, 6));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.UP_LEFT.id), map.getTileAt(6, 6));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.LEFT.id), map.getTileAt(6, 7));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.DOWN_LEFT.id), map.getTileAt(6, 8));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.DOWN.id), map.getTileAt(7, 8));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.DOWN_RIGHT.id), map.getTileAt(8, 8));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.RIGHT.id), map.getTileAt(8, 7));
        assertEquals(map.getTileAt(7, 7).getTransition(Direction.UP_RIGHT.id), map.getTileAt(8, 6));

        assertEquals(map.getTileAt(6, 0).getTransition(Direction.UP.id), map.getTileAt(9, 1));
        assertEquals(map.getTileAt(9, 1).getTransition(Direction.UP_RIGHT.id), map.getTileAt(6, 0));
        assertEquals(map.getTileAt(6, 0).getArrivalDirection(Direction.UP.id), Direction.UP_RIGHT.id);
        assertEquals(map.getTileAt(9, 1).getArrivalDirection(Direction.UP_RIGHT.id), Direction.UP.id);

        assertEquals(map.getTileAt(7, 14).getTransition(Direction.DOWN.id), map.getTileAt(7, 0));
        assertEquals(map.getTileAt(7, 0).getTransition(Direction.UP.id), map.getTileAt(7, 14));
        assertEquals(map.getTileAt(7, 14).getArrivalDirection(Direction.DOWN.id), Direction.UP.id);
        assertEquals(map.getTileAt(7, 0).getArrivalDirection(Direction.UP.id), Direction.DOWN.id);

    }
}