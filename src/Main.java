public class Main {

    static String asci =
            "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 i 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
                    "0 c 0 0 0 0 1 2 3 0 i 0 0 0 0\n" +
                    "0 0 0 0 0 0 3 1 2 0 0 0 0 0 0\n" +
                    "0 0 0 b 0 0 2 3 1 0 0 0 0 0 0\n" +
                    "0 0 0 0 0 0 0 0 0 0 0 0 b 0 0\n" +
                    "− − − − − 0 0 x 0 0 − − − − −\n" +
                    "− − − − − 0 x x x 0 − − − − −\n" +
                    "− − − − − 0 0 x c 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −";

    public static void main(String[] args) {
        Map myMap = Map.readFromString(15,15,asci);
        System.out.println(myMap);
    }
}
