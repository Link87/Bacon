package bacon;

public class Maps {

    public static final String EXAMPLE = "3\n" +
            "6\r\n" +
            "4 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 i 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 c 0 0 0 0 1 2 3 0 i 0 0 0 0\n" +
            "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x c 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_CERTAIN = "3\n" +
            "6\r\n" +
            "4 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 1 2 3 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_BRS_UNCERTAIN = "3\n" +
            "0\r\n" +
            "0 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 b 0 0 - - - - -\n" +
            "- - - - - 0 0 b 0 0 - - - - -\r\n" +
            "0 0 0 b 0 0 0 0 c 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 1 2 3 0 0 0 b 0 0\n" +
            "0 b 0 b 0 c 3 1 2 0 b 0 0 0 0\n" +
            "0 0 0 0 0 i 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 b 0 b 0 b i 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 b x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_BRS_BONUS = "3\n" +
            "0\r\n" +
            "0 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 b 0 0 - - - - -\n" +
            "- - - - - 0 0 b 0 0 - - - - -\r\n" +
            "0 0 0 b 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 1 2 3 0 0 0 b 0 0\n" +
            "0 b 0 b 0 0 3 1 2 0 b 0 0 0 0\n" +
            "0 0 0 0 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 b 0 b 0 b 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 b x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_STABILITY = "3\n" +
            "6\r\n" +
            "4 2\n" +
            "15 15\n" +
            "- - - - - 1 1 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 1 - - - - -\n" +
            "- - - - - 0 0 0 1 0 - - - - -\n" +
            "- - - - - 0 0 1 0 0 - - - - -\n" +
            "- - - - - 0 1 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 1 0 0 0 0 0 0 0 0 0\n" +
            "1 0 0 0 1 0 1 2 3 0 0 0 0 0 0\n" +
            "1 1 0 1 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 1 b 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "1 1 0 0 0 0 0 0 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 1 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_MOBILITY = "3\n" +
            "6\r\n" +
            "4 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 1 2 3 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 0 0 0 0 1 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_BOMBINGPHASE = "3\n" +
            "6\r\n" +
            "1 1\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 3 1 1 3 3 0 0 0 0 0\n" +
            "0 0 0 0 0 3 1 2 3 1 0 0 0 0 0\n" +
            "0 0 0 0 0 1 3 1 3 1 0 0 0 0 0\n" +
            "0 0 0 b 0 1 2 3 1 3 0 0 0 0 0\r\n" +
            "0 0 0 0 0 3 3 1 1 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String MODEXAMPLE = "3\n" +
            "6\r\n" +
            "4 2\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 i 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 c 0 0 0 0 1 2 3 0 i 0 0 0 0\n" +
            "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 0 0 0 0 x 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 c 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_BOMBGEOMETRY = "3\n" +
            "6\r\n" +
            "4 1\n" +
            "15 15\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 i 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 c 0 0 0 0 1 2 3 0 i 0 0 0 0\n" +
            "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
            "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\r\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 b 0 0\n" +
            "- - - - - 0 0 x 0 0 - - - - -\n" +
            "- - - - - 0 x x x 0 - - - - -\n" +
            "- - - - - 0 0 x c 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "- - - - - 0 0 0 0 0 - - - - -\n" +
            "6 0 0 <-> 9 1 1\n" +
            "7 14 4 <-> 7 0 0";

    public static final String EXAMPLE_LINEGEOMETRY = "3\n" +
            "6\r\n" +
            "4 1\n" +
            "5 15\n" +
            "- - - - - 1 0 1 0 0 - - - - -\n" +
            "- - - - - 0 1 2 1 1 - - - - -\n" +
            "- - - - - 1 2 3 1 0 - - - - -\n" +
            "- - - - - 0 1 3 1 0 - - - - -\n" +
            "- - - - - 0 1 0 0 1 - - - - -\n" +
            "9 0 2 <-> 7 0 0\n" +
            "5 1 6 <-> 9 1 2\n" +
            "5 3 6 <-> 5 3 5\n" +
            "9 4 2 <-> 9 4 4";

    public static final String COMP_SQUARE = "8\n" +
            "4\n" +
            "2 3\n" +
            "42 42\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 b b b b 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 4 5 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 1 8 0 0 0 0\n" +
            "0 0 0 0 5 4 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 8 1 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 2 4 3 1 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 c - 0 0 0 0 0 0 0 0 1 3 4 2 0 0 0 0 0 0 0 0 - c 0 0 0 0 0 0 0 0 0\n" +
            "- - - - - - - - - - - 0 0 0 0 0 0 0 0 3 1 2 4 0 0 0 0 0 0 0 0 - - - - - - - - - - -\n" +
            "0 0 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 4 2 1 3 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "b 0 0 0 0 0 0 0 6 8 7 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 6 8 7 5 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 5 7 8 6 0 0 0 0 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 5 7 8 6 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 7 5 6 8 0 0 0 0 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 7 5 6 8 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 8 6 5 7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 8 6 5 7 0 0 0 0 b\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 2 4 3 1 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 0 0\n" +
            "- - - - - - - - - - - 0 0 0 0 0 0 0 0 1 3 4 2 0 0 0 0 0 0 0 0 - - - - - - - - - - -\n" +
            "0 0 0 0 0 0 0 0 0 c - 0 0 0 0 0 0 0 0 3 1 2 4 0 0 0 0 0 0 0 0 - c 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 4 2 1 3 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 2 7 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 3 6 0 0 0 0\n" +
            "0 0 0 0 7 2 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 6 3 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 b b b b 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "9 0 2 <-> 32 0 6\n" +
            "9 1 2 <-> 32 1 6\n" +
            "9 2 2 <-> 32 2 6\n" +
            "9 3 2 <-> 32 3 6\n" +
            "9 4 2 <-> 32 4 6\n" +
            "9 5 2 <-> 32 5 6\n" +
            "9 6 2 <-> 32 6 6\n" +
            "9 7 2 <-> 32 7 6\n" +
            "9 8 2 <-> 32 8 6\n" +
            "9 9 2 <-> 32 9 6\n" +
            "9 1 1 <-> 32 0 5\n" +
            "9 2 1 <-> 32 1 5\n" +
            "9 3 1 <-> 32 2 5\n" +
            "9 4 1 <-> 32 3 5\n" +
            "9 5 1 <-> 32 4 5\n" +
            "9 6 1 <-> 32 5 5\n" +
            "9 7 1 <-> 32 6 5\n" +
            "9 8 1 <-> 32 7 5\n" +
            "9 9 1 <-> 32 8 5\n" +
            "9 0 3 <-> 32 1 7\n" +
            "9 1 3 <-> 32 2 7\n" +
            "9 2 3 <-> 32 3 7\n" +
            "9 3 3 <-> 32 4 7\n" +
            "9 4 3 <-> 32 5 7\n" +
            "9 5 3 <-> 32 6 7\n" +
            "9 6 3 <-> 32 7 7\n" +
            "9 7 3 <-> 32 8 7\n" +
            "9 8 3 <-> 32 9 7\n" +
            "9 32 2 <-> 32 32 6\n" +
            "9 33 2 <-> 32 33 6\n" +
            "9 34 2 <-> 32 34 6\n" +
            "9 35 2 <-> 32 35 6\n" +
            "9 36 2 <-> 32 36 6\n" +
            "9 37 2 <-> 32 37 6\n" +
            "9 38 2 <-> 32 38 6\n" +
            "9 39 2 <-> 32 39 6\n" +
            "9 40 2 <-> 32 40 6\n" +
            "9 41 2 <-> 32 41 6\n" +
            "9 33 1 <-> 32 32 5\n" +
            "9 34 1 <-> 32 33 5\n" +
            "9 35 1 <-> 32 34 5\n" +
            "9 36 1 <-> 32 35 5\n" +
            "9 37 1 <-> 32 36 5\n" +
            "9 38 1 <-> 32 37 5\n" +
            "9 39 1 <-> 32 38 5\n" +
            "9 40 1 <-> 32 39 5\n" +
            "9 41 1 <-> 32 40 5\n" +
            "9 32 3 <-> 32 33 7\n" +
            "9 33 3 <-> 32 34 7\n" +
            "9 34 3 <-> 32 35 7\n" +
            "9 35 3 <-> 32 36 7\n" +
            "9 36 3 <-> 32 37 7\n" +
            "9 37 3 <-> 32 38 7\n" +
            "9 38 3 <-> 32 39 7\n" +
            "9 39 3 <-> 32 40 7\n" +
            "9 40 3 <-> 32 41 7\n" +
            "0 9 4 <-> 0 32 0\n" +
            "1 9 4 <-> 1 32 0\n" +
            "2 9 4 <-> 2 32 0\n" +
            "3 9 4 <-> 3 32 0\n" +
            "4 9 4 <-> 4 32 0\n" +
            "5 9 4 <-> 5 32 0\n" +
            "6 9 4 <-> 6 32 0\n" +
            "7 9 4 <-> 7 32 0\n" +
            "8 9 4 <-> 8 32 0\n" +
            "9 9 4 <-> 9 32 0\n" +
            "1 9 5 <-> 0 32 1\n" +
            "2 9 5 <-> 1 32 1\n" +
            "3 9 5 <-> 2 32 1\n" +
            "4 9 5 <-> 3 32 1\n" +
            "5 9 5 <-> 4 32 1\n" +
            "6 9 5 <-> 5 32 1\n" +
            "7 9 5 <-> 6 32 1\n" +
            "8 9 5 <-> 7 32 1\n" +
            "9 9 5 <-> 8 32 1\n" +
            "0 9 3 <-> 1 32 7\n" +
            "1 9 3 <-> 2 32 7\n" +
            "2 9 3 <-> 3 32 7\n" +
            "3 9 3 <-> 4 32 7\n" +
            "4 9 3 <-> 5 32 7\n" +
            "5 9 3 <-> 6 32 7\n" +
            "6 9 3 <-> 7 32 7\n" +
            "7 9 3 <-> 8 32 7\n" +
            "8 9 3 <-> 9 32 7\n" +
            "32 9 4 <-> 32 32 0\n" +
            "33 9 4 <-> 33 32 0\n" +
            "34 9 4 <-> 34 32 0\n" +
            "35 9 4 <-> 35 32 0\n" +
            "36 9 4 <-> 36 32 0\n" +
            "37 9 4 <-> 37 32 0\n" +
            "38 9 4 <-> 38 32 0\n" +
            "39 9 4 <-> 39 32 0\n" +
            "40 9 4 <-> 40 32 0\n" +
            "41 9 4 <-> 41 32 0\n" +
            "33 9 5 <-> 32 32 1\n" +
            "34 9 5 <-> 33 32 1\n" +
            "35 9 5 <-> 34 32 1\n" +
            "36 9 5 <-> 35 32 1\n" +
            "37 9 5 <-> 36 32 1\n" +
            "38 9 5 <-> 37 32 1\n" +
            "39 9 5 <-> 38 32 1\n" +
            "40 9 5 <-> 39 32 1\n" +
            "41 9 5 <-> 40 32 1\n" +
            "32 9 3 <-> 33 32 7\n" +
            "33 9 3 <-> 34 32 7\n" +
            "34 9 3 <-> 35 32 7\n" +
            "35 9 3 <-> 36 32 7\n" +
            "36 9 3 <-> 37 32 7\n" +
            "37 9 3 <-> 38 32 7\n" +
            "38 9 3 <-> 39 32 7\n" +
            "39 9 3 <-> 40 32 7\n" +
            "40 9 3 <-> 41 32 7\n" +
            "9 32 1 <-> 32 9 5\n" +
            "9 9 3 <-> 32 32 7\n" +
            "0 0 0 <-> 20 19 4\n" +
            "0 0 6 <-> 19 20 2\n" +
            "0 0 7 <-> 19 19 3\n" +
            "41 0 1 <-> 22 19 5\n" +
            "41 0 0 <-> 21 19 4\n" +
            "41 0 2 <-> 22 20 6\n" +
            "41 41 3 <-> 22 22 7\n" +
            "41 41 2 <-> 22 21 6\n" +
            "41 41 4 <-> 21 22 0\n" +
            "0 41 5 <-> 19 22 1\n" +
            "0 41 4 <-> 21 22 0 \n" +
            "0 41 6 <-> 19 21 2";

    public static final String STARFISH = "8\n" +
            "6\n" +
            "1 2\n" +
            "33 33\n" +
            "0 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 0\n" +
            "- 0 0 - - - - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - - - - 0 0 -\n" +
            "- 0 0 - - - - - 0 6 8 0 0 0 0 0 c 0 0 0 0 0 5 7 0 - - - - - 0 0 -\n" +
            "- - - b 0 0 b - 0 8 6 0 0 0 0 0 c 0 0 0 0 0 7 5 0 - b 0 0 b - - -\n" +
            "- - - 0 6 5 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 7 8 0 - - -\n" +
            "- - - 0 5 6 0 - - - - - - - - - - - - - - - - - - - 0 8 7 0 - - -\n" +
            "- - - b 0 0 b - - - - - - - - - - - - - - - - - - - b 0 0 b - - -\n" +
            "- - - - - - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - - - - - -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 i - i 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - 0 0 0 0 -\n" +
            "- - - - - - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - - - - - -\n" +
            "- - - b 0 0 b - - - - - - - - - - - - - - - - - - - b 0 0 b - - -\n" +
            "- - - 0 3 4 0 - - - - - - - - - - - - - - - - - - - 0 2 1 0 - - -\n" +
            "- - - 0 4 3 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 1 2 0 - - -\n" +
            "- - - b 0 0 b - 0 4 2 0 0 0 0 0 c 0 0 0 0 0 3 1 0 - b 0 0 b - - -\n" +
            "- 0 0 - - - - - 0 2 4 0 0 0 0 0 c 0 0 0 0 0 1 3 0 - - - - - 0 0 -\n" +
            "- 0 0 - - - - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - - - - 0 0 -\n" +
            "0 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 0\n" +
            "0 0 0 <-> 0 32 4\n" +
            "0 0 6 <-> 32 0 2\n" +
            "0 32 6 <-> 32 32 2\n" +
            "32 32 4 <-> 32 0 0\n" +
            "1 24 4 <-> 8 28 6\n" +
            "2 24 4 <-> 8 29 6\n" +
            "3 24 4 <-> 8 30 6\n" +
            "4 24 4 <-> 8 31 6\n" +
            "28 24 4 <-> 24 28 2\n" +
            "29 24 4 <-> 24 29 2\n" +
            "30 24 4 <-> 24 30 2\n" +
            "31 24 4 <-> 24 31 2\n" +
            "1 8 0 <-> 8 1 6\n" +
            "2 8 0 <-> 8 2 6\n" +
            "3 8 0 <-> 8 3 6\n" +
            "4 8 0 <-> 8 4 6\n" +
            "28 8 0 <-> 24 1 2\n" +
            "29 8 0 <-> 24 2 2\n" +
            "30 8 0 <-> 24 3 2\n" +
            "31 8 0 <-> 24 4 2\n" +
            "6 6 4 <-> 15 16 2\n" +
            "6 26 0 <-> 16 15 4\n" +
            "26 26 0 <-> 17 16 6\n" +
            "26 6 4 <-> 16 17 0";
}
