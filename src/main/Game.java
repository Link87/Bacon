import java.util.Arrays;
import java.util.ArrayList;

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
                initializeMap(message);
                break;
            case "03":
                me = getPlayerFromNumber(Integer.parseInt(message, 16));
                break;
            case "06":
                // Server announces move of a player
                executeMove(message);
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
    private void initializeMap(String mapData) {
        currentPhase = GamePhase.PHASE_ONE;

        String[] lines = hexToAscii(mapData).split("\r?\n");

        int PlayerCount = Integer.parseInt(lines[0]);
        int initOverrideStoneCount = Integer.parseInt(lines[1]);

        String[] bomb = lines[2].split(" ");
        int bombCount = Integer.parseInt(bomb[0]);
        bombRadius = Integer.parseInt(bomb[1]);

        player = new Player[PlayerCount];
        for (int i = 1; i <= PlayerCount; i++) {
            player[i - 1] = new Player(i, initOverrideStoneCount, bombCount);
        }

        String[] bounds = lines[3].split(" ");
        int mapHeight = Integer.parseInt(bounds[0]);
        int mapWidth = Integer.parseInt(bounds[1]);

        map = Map.readFromString(mapWidth, mapHeight, Arrays.copyOfRange(lines, 4, lines.length), getTotalPlayerCount());
    }

    /**
     * Reads the given String and executes the contained move. String must follow the specification of message type 6.
     *
     * @param moveData String holding a move
     */
    private void executeMove(String moveData) {
        int x = Integer.parseInt(moveData.substring(0, 4), 16);
        int y = Integer.parseInt(moveData.substring(4, 8), 16);

        int bonusRequestNr = 0;
        if (moveData.length() > 8) bonusRequestNr = Integer.parseInt(moveData.substring(8, 10), 16);


        BonusRequest bonusRequest = getBonusRequestFromNumber(bonusRequestNr);

        int p = Integer.parseInt(moveData.substring(10, 12), 16);
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
     */
    public Player getPlayerFromNumber(int nr) {
        for (Player value : player) {
            if (value.getPlayerNumber() == nr) return value;
        }

        throw new IllegalArgumentException("Invalid Player Number:" + nr);
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
        ENDED
    }

    /**
     * Translate the BonusRequest string number into an enum
     *
     * @param bonusNr The BonusRequest as a number
     * @return The BonusRequest as an enum
     * @throws IllegalArgumentException for bonusNr which cannot be translated to the enum
     */
    public BonusRequest getBonusRequestFromNumber(int bonusNr){
        switch(bonusNr){
            case 0:
                return BonusRequest.NONE;
            case 1:
                return BonusRequest.ONE;
            case 2:
                return BonusRequest.TWO;
            case 3:
                return BonusRequest.THREE;
            case 4:
                return BonusRequest.FOUR;
            case 5:
                return BonusRequest.FIVE;
            case 6:
                return BonusRequest.SIX;
            case 7:
                return BonusRequest.SEVEN;
            case 8:
                return BonusRequest.EIGHT;
            case 20:
                return BonusRequest.BOMB;
            case 21:
                return BonusRequest.OVERRIDE;
            default:
                throw new IllegalArgumentException("The BonusRequestNumber is not valid");
        }
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
        return this.player.length;
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
