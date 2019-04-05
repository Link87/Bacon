public enum Direction {
    UP(0),
    UP_LEFT(1),
    LEFT(2),
    DOWN_LEFT(3),
    DOWN(4),
    DOWN_RIGHT(5),
    RIGHT(6),
    UP_RIGHT(7);

    public final int index;

    Direction(int index) {
        this.index = index;
    }

}
