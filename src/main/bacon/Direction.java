package bacon;

import java.util.EnumMap;

/**
 * This enum defines the directions that transitions can be assigned to.
 * <p>
 * The corresponding integer value can be determined with the constant {@link Direction#id} field.
 * Given a direction by its integer value, the enum variant can be obtained with
 * {@link Direction#fromId(int)}.
 */
public enum Direction {
    UP(0),
    UP_RIGHT(1),
    RIGHT(2),
    DOWN_RIGHT(3),
    DOWN(4),
    DOWN_LEFT(5),
    LEFT(6),
    UP_LEFT(7);

    /**
     * Integer value that represents an invalid {@code Direction}.
     */
    public static final int NULL_DIRECTION_ID = -1;
    /**
     * Number of {@code Direction} variants.
     */
    public static final int DIRECTION_COUNT = 8;
    /**
     * A {@code Map} containing the opposite {@code Direction} of each {@code Direction}.
     */
    private static final EnumMap<Direction, Direction> opposite = new EnumMap<>(Direction.class);
    /**
     * An array for fast reverse lookup of {@code Direction} variants, when integer values are given.
     */
    private static final Direction[] lookup = new Direction[8];

    static {
        // initialize lookup tables

        opposite.put(UP, DOWN);
        opposite.put(UP_RIGHT, DOWN_LEFT);
        opposite.put(RIGHT, LEFT);
        opposite.put(DOWN_RIGHT, UP_LEFT);
        opposite.put(DOWN, UP);
        opposite.put(DOWN_LEFT, UP_RIGHT);
        opposite.put(LEFT, RIGHT);
        opposite.put(UP_LEFT, DOWN_RIGHT);

        for (Direction direction : Direction.values())
            lookup[direction.id] = direction;
    }

    /**
     * The integer representation of the direction.
     */
    public final int id;
    Direction(int id) {
        this.id = id;
    }

    /**
     * Convert an integer representation of a direction into a {@code Direction} enum variant.
     *
     * @param id the integer to look up
     * @return the associated {@code Direction} variant
     */
    public static Direction fromId(int id) {
        return lookup[id];
    }

    /**
     * Returns the opposite {@code Direction} of the given direction in integer representation.
     *
     * @param id integer representing a {@code Direction}
     * @return the opposite {@code Direction} as an integer
     */
    public static int oppositeOf(int id) {
        return (id + 4) % 8;
    }

    /**
     * Returns the opposite {@code Direction}.
     *
     * @return the opposite {@code Direction}
     */
    public Direction opposite() {
        return Direction.opposite.get(this);
    }
}