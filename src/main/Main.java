import java.util.Arrays;

public class Main {

    private static Player[] players;
    private static Map myMap;

    public static void main(String[] args) {
        //this is the example from netwokSpecifications.pdf
        initGameExample("320a300a3120310a3220360a2d202d206220782032202d0a2d203020782031202d202d0a3120312035203c2d3e2034203020310a");
    }

    /**
     * This is ExampleCode mend to show the processing/flow of data from the server
     *
     * @param hexData is the Message Data part of the server message (without the Type and Length parts)
     */
    public static void initGameExample(String hexData) {
        //hex to ascii and replacing \r\n with \n
        String asciData = hexToAscii(hexData).replaceAll("\r", "");
        String[] lines = asciData.split("\n");

        //TODO Use Lines(indexes) 0-2 to init Players and give them bombs/override stones

        String[] bounds = lines[3].split(" ");
        int mapHight = Integer.parseInt(bounds[0]);
        int mapWidth = Integer.parseInt(bounds[1]);

        myMap = Map.readFromString(mapWidth, mapHight, Arrays.copyOfRange(lines, 4, lines.length));
    }

    /**
     * converts hex String to ASCII String
     *
     * @param hexStr a not null hex String
     * @return corresponding ASCII String
     */
    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static Player playerFromNumber(int nr) {
        return players[nr - 1];
    }
}
