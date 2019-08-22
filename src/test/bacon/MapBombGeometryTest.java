package bacon;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class MapBombGeometryTest {

    @Test
    public void BombGeometryTest(){
        Game.getGame().readMap(Maps.EXAMPLE_BOMBGEOMETRY);
        Map map = Game.getGame().getCurrentState().getMap();

        Set<Tile> bombSet = map.getTileAt(7,7).getBombEffect();

        assertEquals("BombSet has the wrong size", 9, bombSet.size());

        for(int x = 6; x <= 8; x++){
            for(int y = 6; y <= 8; y++){
                assertTrue("Tile missing from BombSet: (" + x + "," + y + ")" ,bombSet.contains(map.getTileAt(x,y)));
            }
        }
    }
}
