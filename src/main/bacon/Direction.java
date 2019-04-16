package bacon;

/**
 * This enum defines the directions that transitions can be assigned to.
 * The corresponding integer value can be determined with <code>ordinal()</code>.
 * Given a direction by its integer value, the enum variant can be obtained with
 * <code>values()[value]</code>.
 */
public enum Direction {
    // DO NOT CHANGE ORDER OF VARIANTS, THIS WILL BREAK THINGS
    UP,
    UP_RIGHT,
    RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    LEFT,
    UP_LEFT;

    public Direction opposite() {
        return Direction.values()[(this.ordinal() + 4) % 8];
    }
}