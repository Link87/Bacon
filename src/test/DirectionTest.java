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
}