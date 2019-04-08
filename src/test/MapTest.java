import org.junit.Test;

import static org.junit.Assert.*;

public class MapTest {

    private static String ascii =
            "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 i 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "0 0 0 0 0 b 0 0 0 0 0 0 0 0 0\n" +
                    "0 c 0 0 0 0 1 2 3 0 i 0 0 0 0\n" +
                    "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
                    "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\n" +
                    "0 0 0 0 0 0 0 0 0 0 0 0 b 0 0\n" +
                    "− − − − − 0 0 x 0 0 − − − − −\n" +
                    "− − − − − 0 x x x 0 − − − − −\n" +
                    "− − − − − 0 0 x c 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "6 0 0 <−> 9 1 1\n" +
                    "7 14 4 <−> 7 0 0\n";

    private static Tile.Property[][] properties =  {
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.INVERSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },

            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.BONUS, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.CHOICE, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.INVERSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT, Tile.Property.DEFAULT },

            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.CHOICE, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE }
    };

    @Test
    public void readFromString() {
        String[] lines = ascii.split("\r?\n");

        Map map = Map.readFromString(15, 15, lines);
        assertEquals(15, map.width);
        assertEquals(15, map.height);

        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                Tile tile = map.getTileAt(x, y);
                assertEquals("Wrong value in tile: " + x + ", " + y, x, tile.x);
                assertEquals("Wrong value in tile: " + x + ", " + y, y, tile.y);
                assertEquals("Wrong value in tile: " + x + ", " + y, properties[y][x], tile.getProperty());
            }
        }
    }
}