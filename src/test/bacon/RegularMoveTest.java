package bacon;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RegularMoveTest {

    @Test
    public void doMove() {
        Game.getGame().readMap(Maps.STARFISH);
        var map = Game.getGame().getCurrentState().getMap();

        // Test inversion tiles
        var tiles = new ArrayList<Tile>();
        var owners = new ArrayList<Player>();

        for (int x = 0; x < 33; x++) {
            for (int y = 0; y < 33; y++) {
                if (map.getTileAt(x, y).getOwner() != null) {
                    tiles.add(map.getTileAt(x, y));
                    if (x == 9 && (y == 1 || y == 2))
                        owners.add(Game.getGame().getPlayerFromNumber(8));
                    else owners.add(map.getTileAt(x, y).getOwner());
                }
            }
        }

        map.getTileAt(9, 1).setProperty(Tile.Property.INVERSION);
        Move.createNewMove(0, map, Game.getGame().getPlayerFromNumber(8), 9, 1, 0).doMove();

        for (int i = 0; i < tiles.size(); i++) {
            int num = (owners.get(i).getPlayerNumber() + 1) % (Game.getGame().getTotalPlayerCount() + 1);
            if (num == 0) num = 1;
            assertTrue("Error at tile " + tiles.get(i).x + ", " + tiles.get(i).y + " num=" + num,
                    num >= 1 && num <= Game.getGame().getTotalPlayerCount());
            assertEquals("Error at tile " + tiles.get(i).x + ", " + tiles.get(i).y,
                    num, tiles.get(i).getOwner().getPlayerNumber());
        }
    }
}