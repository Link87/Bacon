public enum Direction {
    UP(0),
    UP_RIGHT(1),
    RIGHT(2),
    DOWN_RIGHT(3),
    DOWN(4),
    DOWN_LEFT(5),
    LEFT(6),
    UP_LEFT(7);

    public final int index;

    Direction(int index) {
        this.index = index;
    }

}