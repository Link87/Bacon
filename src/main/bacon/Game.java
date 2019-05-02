package bacon;

import bacon.ai.AI;
import bacon.move.Move;
import bacon.move.MoveFactory;
import bacon.net.Message;
import bacon.net.ServerConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Game class which contains the stateless information about the current game
 * and provides access to the currentGameState
 */
public class Game {

    private static final Logger LOGGER = Logger.getGlobal();

    private static final int GROUP_NUMBER = 6;
    private static final Game INSTANCE = new Game();

    private int bombRadius;
    private int playerCount;

    private int moveCount = 1;

    /**
     * all stateful information is contained inside this object
     */
    private GameState currentGameState = new GameState();

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

        try (var connection = new ServerConnection(cfg.getHost(), cfg.getPort())) {
            // send group number to server
            connection.sendMessage(new Message(Message.Type.GROUP_NUMBER, new byte[]{GROUP_NUMBER}));

            // receive map data from server
            var mapMsg = connection.awaitMessage();
            assert mapMsg.getType() == Message.Type.MAP_CONTENT;
            processMessage(mapMsg);

            // receive player number from server
            var numMsg = connection.awaitMessage();
            assert numMsg.getType() == Message.Type.PLAYER_NUMBER;
            processMessage(numMsg);

            runGame(connection);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString());
            System.exit(1);
        }
    }

    /**
     * Runs the main game loop. Returns when game ends.
     *
     * @param connection the {@link ServerConnection} to use for the game
     */
    private void runGame(ServerConnection connection) {
        while (currentGameState.getGamePhase() != GamePhase.ENDED) {
            var msg = connection.awaitMessage();

            if (msg.getType() == Message.Type.MOVE_REQUEST) {
                var buffer = ByteBuffer.wrap(msg.getBinaryContent());
                var move = AI.getAI().requestMove(buffer.getInt(), buffer.get(), this.getCurrentState());
                connection.sendMessage(new Message(Message.Type.MOVE_RESPONSE, move.encodeBinary()));
                // Manual gc is usually bad practice, but we have lots of spare time after here
                // TODO maybe skip GC when we directly have a second turn
                System.gc();
            } else processMessage(msg);
        }
    }

    /**
     * Processes the given message according to the network specification.
     * Move Requests can not be processed, because an established server connection is required to send a response.
     *
     * @param msg Message to process
     */
    void processMessage(Message msg) {
        // Split message into components according to format. Message length is skipped, because Java *yay*
        // get representations of the message data
        var string = new String(msg.getBinaryContent(), StandardCharsets.US_ASCII);
        switch (msg.getType()) {
            case MAP_CONTENT:
                // Receive map from server
                // parse data and initialize the Game instance with given values
                readMap(string);
                break;
            case PLAYER_NUMBER:
                currentGameState.setMe(getCurrentState().getPlayerFromNumber(msg.getBinaryContent()[0]));
                break;
            case MOVE_ANNOUNCE:
                // Server announces move of a player
                executeMove(msg.getBinaryContent());
                break;
            case DISQUALIFICATION:
                // Disqualify player -- quit when *we* where disqualified
                byte disqualified = msg.getBinaryContent()[0];
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
    public void readMap(String mapData) {
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
     * Reads the given binary data and executes the contained move on the current map (in currentGameState).
     * Data must follow the specification of message type 6.
     *
     * @param moveData byte array holding a move
     */
    private void executeMove(byte[] moveData) {
        Move move = MoveFactory.decodeBinary(moveData, currentGameState);

        if (move.isLegal()) {
            LOGGER.log(Level.FINE, "Move #{0}: Move is legal. Executing.", moveCount);
            move.doMove();
        } else LOGGER.log(Level.SEVERE, "Move #{0}: Can't execute move: is illegal!", moveCount);
        moveCount++;
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
    private Game() {}
}
