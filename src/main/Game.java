import java.util.Arrays;
import java.util.ArrayList;
import java.lang.System;

/**
 * A Game class which contains the basic information about the current game.
 */
public class Game {

    private static Game instance;

    private Player[] player;
    private Map map;
    private int bombRadius;
    private Player me;
    private GamePhase currentPhase;
    /**
     * Stack of actually executed moves,
     */
    private ArrayList<Move> moveStack = new ArrayList<>();
    /**
     * Stack of all moves received (including illegal ones)
     */
    private ArrayList<Move> allMovesGlossary = new ArrayList<>();

    public static Game getGame() {
        if (instance == null) {
            instance = new Game();
        }

        return instance;
    }

    /**
     * This is ExampleCode mend to show the processing/flow of data from the server
     *
     * @param hexData is the Message Data part of the server message (without the Type and Length parts)
     */
    public void initGame(String hexData) {
        //Split message into components according to format
        String messageType = hexData.substring(0, 2);
        String messageLength = hexData.substring(2, 10);
        String message = hexData.substring(10);

        switch (messageType) {
            case "01":
                //Send group number to server (INCOMPLETE)

            case "02":
                //Receive map from server

                currentPhase = GamePhase.PHASE_ONE;

                //hex to ascii and replacing \r\n with \n
                String asciiData = hexToAscii(message).replaceAll("\r", "");

                String[] lines = asciiData.split("\n");

                int playerCount = Integer.parseInt(lines[0]);
                int initOverrideStoneCount = Integer.parseInt(lines[1]);

                String[] bomb = lines[2].split(" ");
                int initBombCount = Integer.parseInt(bomb[0]);
                bombRadius = Integer.parseInt(bomb[0]);

                for (int i = 1; i <= playerCount; i++) {
                    player[i - 1] = Player.readFromString(i, initOverrideStoneCount, initBombCount);
                }

                String[] bounds = lines[3].split(" ");
                int mapHeight = Integer.parseInt(bounds[0]);
                int mapWidth = Integer.parseInt(bounds[1]);

                map = Map.readFromString(mapWidth, mapHeight, Arrays.copyOfRange(lines, 4, lines.length));

            case "03":
                me = Game.getGame().playerFromNumber(Integer.parseInt(message));

            case "04":
                //Received move request from server (INCOMPLETE)

            case "05":
                //Send move response to server (INCOMPLETE)

            case "06":
                //Server announces move by a player
                String xCoordinateHex = message.substring(0, 4);
                String yCoordinateHex = message.substring(4, 8);
                String BonusRequestHex = "";
                if (message.length() > 8) BonusRequestHex = message.substring(8, 10);
                String player = message.substring(10, 12);

                int x = Integer.parseInt(xCoordinateHex, 16);
                int y = Integer.parseInt(yCoordinateHex, 16);
                int bonusRequest = 0;
                if (message.length() > 8) bonusRequest = Integer.parseInt(BonusRequestHex, 16);
                int p = Integer.parseInt(player, 16);
                Player movingPlayer = Game.getGame().playerFromNumber(p);
                int moveStackTop = moveStack.size();
                int glossaryTop = allMovesGlossary.size();

                Move move = Move.createNewMove(glossaryTop, map, movingPlayer, x, y, bonusRequest);
                allMovesGlossary.set(glossaryTop, move);

                if (move.isLegal()) {
                    System.out.println("Move is legal.");
                    move.doMove();
                    moveStack.set(moveStackTop, move);
                } else System.out.println("Move is illegal.");

            case "07":
                //Disqualify player
                Game.getGame().playerFromNumber(Integer.parseInt(message)).disqualify();

            case "08":
                //Phase one of the game ends
                currentPhase = GamePhase.PHASE_TWO;

            case "09":
                //Phase two of the game ends
                currentPhase = GamePhase.ENDED;

            default:
                throw new IllegalArgumentException("Invalid Message Type: " + messageType);
        }

    }

    /**
     * A helper method that converts a String with hex numbers to a
     * String containing the corresponding ASCII characters.
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

    /**
     * This method finds the player for a given player number
     *
     * @param nr
     * @return
     */
    public Player playerFromNumber(int nr) {
        int n = player.length;
        for (int i = 0; i < n; i++) {
            if (player[i].getPlayerNumber() == nr) return player[i];
        }

        throw new IllegalArgumentException("Invalid Player Number");
    }

    /**
     * This enum represents the Phases of the game.
     * <code>PHASE_ONE<\code> stands for the playing phase,
     * <code>PHASE_TWO<\code> for the bombing phase,
     * <code>ENDED<\code> for the end of the game
     */

    public enum GamePhase {
        PHASE_ONE,
        PHASE_TWO,
        ENDED;
    }

    /**
     * These methods return base attributes about the game
     */
    public GamePhase getGamePhase() {
        return currentPhase;
    }

    public int getBombRadius() {
        return bombRadius;
    }

    public int getTotalPlayerNumber() {
        return player.length;
    }

    /**
     * Dummy constructor
     */
    private Game() {}
}
