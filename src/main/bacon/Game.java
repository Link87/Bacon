package bacon;

import bacon.net.Message;
import bacon.net.ServerConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * A Game class which contains the stateless information about the current game
 * and provides access to the currentGameState
 */
public class Game {

    private static final int GROUP_NUMBER = 6;
    private static final Game INSTANCE = new Game();

    private int bombRadius;
    private int playerCount;

    /**
     * all stateful information is contained inside this object
     */
    private GameState currentGameState = new GameState();
    /**
     * Stack of actually executed moves,
     */
    private ArrayList<Move> moveStack = new ArrayList<>();
    /**
     * Stack of all moves received (including illegal ones)
     */
    private ArrayList<Move> allMovesGlossary = new ArrayList<>();

    public static Game getGame() {
        return INSTANCE;
    }

    public GameState getCurrentState() {
        return currentGameState;
    }

    /**
     * Initialize the game with the given {@link Config}. This sends the group number and receives
     * the map date and the starts the game loop.
     *
     * @param cfg the {@link Config} to use
     */
    void startGame(Config cfg) {
        ServerConnection connection = null;
        try {
            connection = new ServerConnection(cfg.getHost(), cfg.getPort());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        // send group number to server
        connection.sendMessage(new Message(Message.Type.GROUP_NUMBER, new byte[]{GROUP_NUMBER}));

        // receive map data from server
        var mapMsg = connection.awaitMessage();
        assert mapMsg.getType() == Message.Type.MAP_CONTENT;
        processMessage(mapMsg);

        runGame(connection);
    }

    /**
     * Runs the main game loop. Returns when game ends.
     *
     * @param connection the {@link ServerConnection} to use for the game
     */
    private void runGame(ServerConnection connection) {
        while (currentGameState.getGamePhase() != GamePhase.ENDED) {
            var msg = connection.awaitMessage();
            processMessage(msg);
        }
    }

    /**
     * Processes the given message according to the network specification.
     *
     * @param msg Message to process
     */
    public void processMessage(Message msg) {
        // Split message into components according to format. Message length is skipped, because Java *yay*
        // get representations of the message data
        var buffer = ByteBuffer.wrap(msg.getBinaryContent());
        var string = new String(msg.getBinaryContent(), StandardCharsets.US_ASCII);
        switch (msg.getType()) {
            case MAP_CONTENT:
                // Receive map from server
                // parse data and initialize the Game instance with given values
                readMap(string);
                break;
            case PLAYER_NUMBER:
                currentGameState.setMe(getCurrentState().getPlayerFromNumber(buffer.get()));
                break;
            case MOVE_ANNOUNCE:
                // Server announces move of a player
                executeMove(new String(msg.getBinaryContent(), StandardCharsets.US_ASCII));
                break;
            case DISQUALIFICATION:
                // Disqualify player -- quit when *we* where disqualified
                byte disqualified = buffer.get();
                if (currentGameState.getMe().number == disqualified)
                    currentGameState.setGamePhase(GamePhase.ENDED);
                getCurrentState().getPlayerFromNumber(disqualified).disqualify();
                break;
            case FIRST_PHASE_END:
                // Phase one of the game ends
                currentGameState.setGamePhase(GamePhase.PHASE_TWO);
                break;
            case GAME_END:
                // Phase two of the game ends
                currentGameState.setGamePhase(GamePhase.ENDED);
                break;
            default:
                throw new IllegalArgumentException("Invalid Message Type: " + msg.getType());
        }
    }

    /**
     * Reads the given String and initializes fields with the contained map data.
     * String must follow specifications of message type 3.
     *
     * @param mapData String holding a map
     */
    void readMap(String mapData) {
        currentGameState.setGamePhase(GamePhase.PHASE_ONE);

        String[] lines = mapData.split("\r?\n");

        playerCount = Integer.parseInt(lines[0]);
        int initOverrideStoneCount = Integer.parseInt(lines[1]);

        String[] bomb = lines[2].split(" ");
        int bombCount = Integer.parseInt(bomb[0]);
        bombRadius = Integer.parseInt(bomb[1]);

        Player[] players = new Player[playerCount];
        for (int i = 1; i <= playerCount; i++) {
            players[i - 1] = new Player(i, initOverrideStoneCount, bombCount);
        }
        currentGameState.setPlayers(players);

        String[] bounds = lines[3].split(" ");
        int mapHeight = Integer.parseInt(bounds[0]);
        int mapWidth = Integer.parseInt(bounds[1]);

        Map map = Map.readFromString(mapWidth, mapHeight, Arrays.copyOfRange(lines, 4, lines.length));
        currentGameState.setMap(map);
    }

    /**
     * Reads the given String and executes the contained move on the current map (in currentGameState).
     * String must follow the specification of message type 6.
     *
     * @param moveData String holding a move
     */
    private void executeMove(String moveData) {
        int x = Integer.parseInt(moveData.substring(0, 4));
        int y = Integer.parseInt(moveData.substring(4, 8));

        int bonusRequest = 0;
        if (moveData.length() > 8) bonusRequest = Integer.parseInt(moveData.substring(8, 10));

        int p = Integer.parseInt(moveData.substring(10, 12));
        Player movingPlayer = currentGameState.getPlayerFromNumber(p);

        Move move = Move.createNewMove(allMovesGlossary.size(), currentGameState.getMap(), movingPlayer, x, y, bonusRequest);
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
     * Returns the radius bombs have in the game.
     * This value is constant throughout the game.
     *
     * @return radius of bombs
     */
    public int getBombRadius() {
        return bombRadius;
    }

    /**
     * Returns the total amount of players that participate in the game.
     * This value is constant throughout the game.
     *
     * @return the total player count
     */
    public int getTotalPlayerCount() {
        return playerCount;
    }

    /**
     * Private dummy constructor because singleton.
     */
    private Game() {
    }
}
