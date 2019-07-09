package bacon;

import org.junit.Test;
import java.util.Set;

import static org.junit.Assert.*;

public class MapLineGeometryTest {

    @Test
    public void LineGeometryTest() {
        Game.getGame().readMap(Maps.EXAMPLE_LINEGEOMETRY);
        Map map = Game.getGame().getCurrentState().getMap();

        Set<MapLine> LineSet = map.getLineSet();

        for (MapLine m : LineSet) {
            System.out.println();
            for (Tile t : m.getLineTiles()) {
                System.out.println("(" + t.x + "," + t.y + ")");
            }
        }

        assertEquals("LineSet has the wrong size", 25, LineSet.size());

        // Input x, y and line direction to print out the map line of a specific tile
        int x = 5;
        int y = 3;
        Direction direction = Direction.UP_RIGHT;

        Tile tile = map.getTileAt(x, y);
        MapLine tileLine;
        switch (direction) {
            case UP:
            case DOWN:
                tileLine = tile.getColumn();
                break;
            case RIGHT:
            case LEFT:
                tileLine = tile.getRow();
                break;
            case UP_LEFT:
            case DOWN_RIGHT:
                tileLine = tile.getIndiagonal();
                break;
            case UP_RIGHT:
            case DOWN_LEFT:
                tileLine = tile.getDiagonal();
                break;
            default:
                tileLine = tile.getColumn();

        }

        Set<Tile> lineTiles = tileLine.getLineTiles();
        System.out.println();
        System.out.println("Tiles in the Map Line of the specified tile:");

        for (Tile t : lineTiles) {
            System.out.println("(" + t.x + "," + t.y + ")");
        }

    }

    @Test
    public void LineFillTest() {
        Game.getGame().readMap(Maps.EXAMPLE_LINEGEOMETRY);
        Map map = Game.getGame().getCurrentState().getMap();

        // Input x, y and line direction to check the fill level of the map line of a specific tile
        int x = 6;
        int y = 4;
        Direction direction = Direction.UP_RIGHT;

        Tile tile = map.getTileAt(x, y);
        MapLine tileLine;
        switch (direction) {
            case UP:
            case DOWN:
                tileLine = tile.getColumn();
                break;
            case RIGHT:
            case LEFT:
                tileLine = tile.getRow();
                break;
            case UP_LEFT:
            case DOWN_RIGHT:
                tileLine = tile.getIndiagonal();
                break;
            case UP_RIGHT:
            case DOWN_LEFT:
                tileLine = tile.getDiagonal();
                break;
            default:
                tileLine = tile.getColumn();

        }

        // Needs to be changed as well if tile is changed
        assertEquals("MapLine has the wrong size", 4, tileLine.getLineSize());
        assertEquals("MapLine has the wrong fill level", 4, tileLine.getFillLevel());
    }
}
