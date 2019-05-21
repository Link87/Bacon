package bacon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void opposite() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.DOWN_RIGHT, Direction.UP_LEFT.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.UP_RIGHT, Direction.DOWN_LEFT.opposite());
        assertEquals(Direction.UP, Direction.DOWN.opposite());
        assertEquals(Direction.UP_LEFT, Direction.DOWN_RIGHT.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
        assertEquals(Direction.DOWN_LEFT, Direction.UP_RIGHT.opposite());
    }

    @Test
    public void oppositeOf() {
        assertEquals(Direction.DOWN.id, Direction.oppositeOf(Direction.UP.id));
        assertEquals(Direction.DOWN_RIGHT.id, Direction.oppositeOf(Direction.UP_LEFT.id));
        assertEquals(Direction.RIGHT.id, Direction.oppositeOf(Direction.LEFT.id));
        assertEquals(Direction.UP_RIGHT.id, Direction.oppositeOf(Direction.DOWN_LEFT.id));
        assertEquals(Direction.UP.id, Direction.oppositeOf(Direction.DOWN.id));
        assertEquals(Direction.UP_LEFT.id, Direction.oppositeOf(Direction.DOWN_RIGHT.id));
        assertEquals(Direction.LEFT.id, Direction.oppositeOf(Direction.RIGHT.id));
        assertEquals(Direction.DOWN_LEFT.id, Direction.oppositeOf(Direction.UP_RIGHT.id));
    }
}