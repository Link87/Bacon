package bacon;

import bacon.ai.AI;
import bacon.move.Move;
import bacon.move.MoveFactory;
import bacon.net.Message;
import bacon.net.ServerConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing a single game.
 * <p>
 * This class contains the stateless information about the current game.
 * Stateful, i.e. not constant, values are saved in a {@link GameState} instance instead.
 */
public class Game {

    private static final Logger LOGGER = Logger.getGlobal();

    private static final int GROUP_NUMBER = 6;
    private static final Game INSTANCE = new Game();
    /**
     * Contains all stateful information about the game.
     */
    private final GameState currentGameState = new GameState();
    private int bombRadius;
    private int playerCount;
    /**
     * Amount of moves done by any player.
     */
    private int moveCount = 1;

    /**
     * Private dummy constructor because singleton.
     */
    private Game() {}

    /**
     * Returns the singleton {@code Game} instance.
     *
     * @return the {@code Game} instance
     */
    public static Game getGame() {
        return INSTANCE;
    }

    /**
     * Returns the {@link GameState} of this {@code Game}.
     *
     * @return the current {@code GameState}
     */
    public GameState getCurrentState() {
        return currentGameState;
    }

    /**
     * Initializes the {@code Game} with the given {@link Config}.
     * <p>
     * This sends the group number and receives the map data. Finally the game loop is started.
     *
     * @param cfg the {@code Config} to use
     */
    void startGame(Config cfg) {

        try (var connection = new ServerConnection(cfg.getHost(), cfg.getPort())) {
            LOGGER.log(Level.INFO, "Established connection to server. Sending group number ({0}).", GROUP_NUMBER);

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

            runGame(connection, cfg);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString());
            System.exit(1);
        }
    }

    /**
     * Runs the main game loop. Returns when game ends.
     *
     * @param connection the {@link ServerConnection} to use for the game
     * @param cfg        the {@link Config} to use
     */
    private void runGame(ServerConnection connection, Config cfg) {
        while (currentGameState.getGamePhase() != GamePhase.ENDED) {
            var msg = connection.awaitMessage();

            if (msg.getType() == Message.Type.MOVE_REQUEST) {
                var buffer = ByteBuffer.wrap(msg.getBinaryContent());
                var move = AI.getAI().requestMove(buffer.getInt(), buffer.get(), cfg, this.getCurrentState());
                connection.sendMessage(new Message(Message.Type.MOVE_RESPONSE, move.encodeBinary()));
            } else processMessage(msg);
        }
    }

    /**
     * Processes the given {@link Message} according to the network specification.
     * <p>
     * Move requests can not be processed, because an established server connection is required to send a response.
     *
     * @param msg the {@code Message} to process
     */
    void processMessage(Message msg) {
        // Split message into components according to format. Message length is skipped, because Java *yay*
        // get representations of the message data
        var string = new String(msg.getBinaryContent(), StandardCharsets.US_ASCII);
        switch (msg.getType()) {
            case MAP_CONTENT:
                // Receive map from server
                // parse data and initialize the Game instance with given values
                LOGGER.log(Level.INFO, "Received map data from server:\n" + string);
                readMap(string);
                break;
            case PLAYER_NUMBER:
                byte me = msg.getBinaryContent()[0];
                LOGGER.log(Level.INFO, "We are player number {0}.", me);
                currentGameState.setMe(me);
                // initializes player share in MapLineGeometry
                this.getCurrentState().getMap().assignLineGeometryPlayers();
                break;
            case MOVE_ANNOUNCE:
                // Server announces move of a player
                executeMove(msg.getBinaryContent());
                break;
            case DISQUALIFICATION:
                // Disqualify player -- quit when *we* where disqualified
                byte disqualified = msg.getBinaryContent()[0];
                LOGGER.log(Level.INFO, "Player {0} is disqualified.", disqualified);
                if (currentGameState.getMe() == disqualified) {
                    currentGameState.setGamePhase(GamePhase.ENDED);
                    LOGGER.log(Level.SEVERE, "I have been disqualified \uD83D\uDE14");
                }
                getCurrentState().getPlayerFromId(disqualified).disqualify();
                break;
            case FIRST_PHASE_END:
                // Phase one of the game ends
                LOGGER.log(Level.INFO, "First game phase ends.");
                currentGameState.setGamePhase(GamePhase.PHASE_TWO);
                break;
            case GAME_END:
                // Phase two of the game ends
                LOGGER.log(Level.INFO, "Game ends.");
                currentGameState.setGamePhase(GamePhase.ENDED);
                printSummary();
                break;
            default:
                LOGGER.log(Level.SEVERE, "Received an invalid message type: {0}!", msg.getType());
                throw new IllegalArgumentException("Invalid Message Type: " + msg.getType());
        }
    }

    /**
     * Reads the given {@code String} and initializes fields with the contained map data.
     * String must follow specifications of message type 2 ({@link Message.Type#MAP_CONTENT}).
     *
     * @param mapData a {@code String} holding a {@link Map}
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
     * Reads the given binary data and executes the contained move on the current {@link Map} (in the current {@link GameState}).
     * The data must follow the specification of message type 6 ({@link Message.Type#MOVE_ANNOUNCE}).
     *
     * @param moveData byte array holding a move
     */
    private void executeMove(byte[] moveData) {
        Move move = MoveFactory.decodeBinary(moveData, currentGameState);

        if (move.isLegal()) {
            LOGGER.log(Level.FINE, "Move #{0}: Received legal move by player {1} on ({2}, {3}).",
                    new Object[]{moveCount, move.getPlayerId(), move.getX(), move.getY()});
            move.doMove();
        } else LOGGER.log(Level.SEVERE, "Move #{0}: Can't execute move by player {1} on ({2}, {3}): is illegal!",
                new Object[]{moveCount, move.getPlayerId(), move.getX(), move.getY()});

        moveCount++;
    }

    /**
     * Prints a game summary to the log.
     */
    private void printSummary() {

        List<Player> players = new ArrayList<>(this.playerCount);
        for (int i = 1; i <= getTotalPlayerCount(); i++) {
            players.add(currentGameState.getPlayerFromId(i));
        }
        players.sort(Comparator.comparing((Player p) -> p.getStones().size()).reversed());

        if (players.get(0).id == currentGameState.getMe())
            LOGGER.log(Level.INFO, "I have won! 🎉🎉🎉");
        else LOGGER.log(Level.INFO, "I have not won! \uD83D\uDE14");

        LOGGER.log(Level.INFO, "The game results are:");
        String[] suffixes = {"st", "nd", "rd", "th", "th", "th", "th", "th"};
        for (int i = 1; i <= getTotalPlayerCount(); i++) {
            LOGGER.log(Level.INFO, "{0}: Player {1} owns {2} tiles.",
                    new Object[]{i + suffixes[i - 1], players.get(i - 1).id, players.get(i - 1).getStones().size()});
        }
    }

    /**
     * Returns the radius bombs have in the game.
     * <p>
     * This value is constant throughout the game.
     *
     * @return radius of bombs
     */
    int getBombRadius() {
        return bombRadius;
    }

    /**
     * Returns the total amount of {@link Player}s  that participate in the game.
     * <p>
     * This value is constant throughout the game.
     *
     * @return the total {@code Player} count
     */
    public int getTotalPlayerCount() {
        return playerCount;
    }
}
