package bacon;

import bacon.move.Move;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * A Game class which contains the basic information about the current game.
 */
public class Game {

    private static Game instance = new Game();

    /**
     * Contains players in order, where index is number - 1.
     */
    private Player[] players;
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
        return instance;
    }

    /**
     * Processes the given message string according to the network specification.
     *
     * @param data String containing hexadecimal data
     */
    public void processMessage(String data) {
        // Split message into components according to format. Message length is skipped, because Java *yay*
        String messageType = data.substring(0, 2);
        String message = data.substring(10);

        switch (messageType) {
            case "02":
                // Receive map from server
                // parse data and initialize the Game instance with given values
                readMap(hexToAscii(message));
                break;
            case "03":
                me = getPlayerFromNumber(Integer.parseInt(message, 16));
                break;
            case "06":
                // Server announces move of a player
                executeMove(hexToAscii(message));
                break;
            case "07":
                // Disqualify player
                getPlayerFromNumber(Integer.parseInt(message, 16)).disqualify();
                break;
            case "08":
                // Phase one of the game ends
                currentPhase = GamePhase.PHASE_TWO;
                break;
            case "09":
                // Phase two of the game ends
                currentPhase = GamePhase.ENDED;
                break;
            default:
                throw new IllegalArgumentException("Invalid Message Type: " + messageType);
        }
    }

    /**
     * Reads the given String and initializes fields with the contained map data.
     * String must follow specifications of message type 3.
     *
     * @param mapData String holding a map
     */
    public void readMap(String mapData) {
        currentPhase = GamePhase.PHASE_ONE;

        String[] lines = mapData.split("\r?\n");

        int playerCount = Integer.parseInt(lines[0]);
        int initOverrideStoneCount = Integer.parseInt(lines[1]);

        String[] bomb = lines[2].split(" ");
        int bombCount = Integer.parseInt(bomb[0]);
        bombRadius = Integer.parseInt(bomb[1]);

        players = new Player[playerCount];
        for (int i = 1; i <= playerCount; i++) {
            players[i - 1] = new Player(i, initOverrideStoneCount, bombCount);
        }

        String[] bounds = lines[3].split(" ");
        int mapHeight = Integer.parseInt(bounds[0]);
        int mapWidth = Integer.parseInt(bounds[1]);

        map = Map.readFromString(mapWidth, mapHeight, Arrays.copyOfRange(lines, 4, lines.length));
    }

    /**
     * Reads the given String and executes the contained move. String must follow the specification of message type 6.
     *
     * @param moveData String holding a move
     */
    private void executeMove(String moveData) {
        int x = Integer.parseInt(moveData.substring(0, 4));
        int y = Integer.parseInt(moveData.substring(4, 8));

        int bonusRequest = 0;
        if (moveData.length() > 8) bonusRequest = Integer.parseInt(moveData.substring(8, 10));

        int p = Integer.parseInt(moveData.substring(10, 12));
        Player movingPlayer = getPlayerFromNumber(p);

        Move move = Move.createNewMove(allMovesGlossary.size(), map, movingPlayer, x, y, bonusRequest);
        allMovesGlossary.add(move);

        if (move.isLegal()) {
            System.out.println("Move is legal.");
            move.doMove();
            moveStack.add(move);
        } else System.out.println("Move is illegal.");
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
     * This method finds the player for a given player number.
     *
     * @param nr number of the player to search for
     * @return the player that corresponds to the given number
     * @throws ArrayIndexOutOfBoundsException when player number is illegal
     */
    public Player getPlayerFromNumber(int nr) {
        // the player array is 0-based
        return players[nr - 1];
    }

    /**
     * This enum represents the Phases of the game.
     * <code>PHASE_ONE</code> stands for the playing phase,
     * <code>PHASE_TWO</code> for the bombing phase,
     * <code>ENDED</code> for the end of the game
     */

    public enum GamePhase {
        PHASE_ONE,
        PHASE_TWO,
        ENDED
    }

    /**
     * Returns the phase the game is currently in.
     *
     * @return {@link GamePhase} representing the current game phase
     */
    public GamePhase getGamePhase() {
        return currentPhase;
    }

    /**
     * Returns the radius bombs have in the game. This value is constant throughout the game.
     *
     * @return radius of bombs
     */
    public int getBombRadius() {
        return bombRadius;
    }

    /**
     * Returns the total amount of players that participate in the game.
     *
     * @return the total player count
     */
    public int getTotalPlayerCount() {
        return players.length;
    }

    /**
     * Returns the map this game is played on.
     *
     * @return the map of this game
     */
    public Map getMap() {
        return map;
    }

    /**
     * Private dummy constructor because singleton.
     */
    private Game() {
    }
}
