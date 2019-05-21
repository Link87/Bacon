package bacon;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GameStateTest {

    @Test
    public void getDeepCopy() {
        Game.getGame().readMap(Maps.STARFISH);

        var origin = Game.getGame().getCurrentState();
        origin.setMe(origin.getPlayerFromNumber(1));

        var copy = origin.getDeepCopy();

        assertEquals(origin.getMap().width, copy.getMap().width);
        assertEquals(origin.getMap().height, copy.getMap().height);
        for (int x = 0; x < origin.getMap().width; x++) {
            for (int y = 0; y < origin.getMap().height; y++) {
                assertNotSame(origin.getMap().getTileAt(x, y), copy.getMap().getTileAt(x, y));
                assertEquals(origin.getMap().getTileAt(x, y).getProperty(), copy.getMap().getTileAt(x, y).getProperty());
                for (int direction = 0; direction < Direction.values().length; direction++) {
                    if (origin.getMap().getTileAt(x, y).getTransition(direction) == null) {
                        assertNull(copy.getMap().getTileAt(x, y).getTransition(direction));
                    } else {
                        assertNotSame(origin.getMap().getTileAt(x, y).getTransition(direction),
                                copy.getMap().getTileAt(x, y).getTransition(direction));
                        assertEquals(origin.getMap().getTileAt(x, y).getTransition(direction).x,
                                copy.getMap().getTileAt(x, y).getTransition(direction).x);
                        assertEquals(origin.getMap().getTileAt(x, y).getTransition(direction).y,
                                copy.getMap().getTileAt(x, y).getTransition(direction).y);
                    }
                }

                for (int direction = 0; direction < Direction.values().length; direction++) {
                    if (origin.getMap().getTileAt(x, y).getTransition(direction) == null)
                        assertEquals(Direction.NO_DIRECTION_NO, copy.getMap().getTileAt(x, y).getArrivalDirection(direction));
                    else assertEquals(origin.getMap().getTileAt(x, y).getArrivalDirection(direction),
                                copy.getMap().getTileAt(x, y).getArrivalDirection(direction));
                }

                if (origin.getMap().getTileAt(x, y).getOwner() == null) {
                    assertNull(copy.getMap().getTileAt(x, y).getOwner());
                } else {
                    assertNotSame(origin.getMap().getTileAt(x, y).getOwner(),
                            copy.getMap().getTileAt(x, y).getOwner());
                    assertEquals(origin.getMap().getTileAt(x, y).getOwner().getPlayerNumber(),
                            copy.getMap().getTileAt(x, y).getOwner().getPlayerNumber());
                    assertEquals(origin.getMap().getTileAt(x, y).getOwner().getOverrideStoneCount(),
                            copy.getMap().getTileAt(x, y).getOwner().getOverrideStoneCount());
                    assertEquals(origin.getMap().getTileAt(x, y).getOwner().getBombCount(),
                            copy.getMap().getTileAt(x, y).getOwner().getBombCount());
                    assertEquals(origin.getMap().getTileAt(x, y).getOwner().getStoneCount(),
                            copy.getMap().getTileAt(x, y).getOwner().getStoneCount());

                    Set<Tile> tiles = new HashSet<>();
                    Set<Tile> tilesCopies = new HashSet<>();
                    origin.getMap().getTileAt(x, y).getOwner().getStonesIterator().forEachRemaining(tiles::add);
                    copy.getMap().getTileAt(x, y).getOwner().getStonesIterator().forEachRemaining(tilesCopies::add);
                    assertEquals(tiles.size(), tilesCopies.size());
                    for (Tile tile1 : tiles) {
                        boolean found = false;
                        for (Tile tile2 : tilesCopies) {
                            assertNotSame(tile1, tile2);
                            if (tile1.x == tile2.x && tile1.y == tile2.y) {
                                found = true;
                            }
                        }
                        assertTrue(found);
                    }
                }
            }
        }
    }

    @Test
    public void getBombRadius() {
        assertEquals(Game.getGame().getBombRadius(), Game.getGame().getCurrentState().getBombRadius());
    }

    @Test
    public void getTotalPlayerCount() {
        assertEquals(Game.getGame().getTotalPlayerCount(), Game.getGame().getCurrentState().getTotalPlayerCount());
    }
}