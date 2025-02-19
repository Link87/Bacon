package bacon;

/**
 * Instances of this class contain stateful information about the game.
 * <p>
 * Queries for stateless information are redirected to the {@link Game} singleton.
 */
public class GameState {

    /**
     * Array of {@link Player}s in order.
     * <p>
     * The index is equal to {@link Player#id}{@code  - 1}.
     */
    private Player[] players;
    private Map map;
    private GamePhase currentPhase;
    /**
     * {@code id} of the {@link Player} that is controlled by the ai.
     */
    private int me;

    /**
     * Creates a new {@code GameState} with default values.
     */
    public GameState() {}

    /**
     * Returns the {@link Player} that corresponds to the given player {@code id}.
     *
     * @param id the id of the {@code Player} to search for
     * @return the {@code Player} that corresponds to the given id
     * @throws ArrayIndexOutOfBoundsException when no {@code Player} with that id is present
     */
    public Player getPlayerFromId(int id) {
        // the player array is 0-based
        return players[id - 1];
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
     * Sets the {@link GamePhase} the game currently is in.
     *
     * @param currentPhase the current game phase
     */
    public void setGamePhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * Returns the current move count
     *
     * @return the current move count
     */
    public int getMoveCount() {return Game.getGame().getMoveCount(); }

    /**
     * Returns the radius bombs have in the game.
     * <p>
     * This value is constant throughout the game.
     *
     * @return radius of bombs
     */
    public int getBombRadius() {
        return Game.getGame().getBombRadius();
    }

    /**
     * Returns the total amount of {@link Player}s that participate in the game.
     * <p>
     * This value is constant throughout the game.
     *
     * @return the total player count
     */
    public int getTotalPlayerCount() {
        return Game.getGame().getTotalPlayerCount();
    }

    /**
     * Returns the {@link Map} this game is played on.
     *
     * @return the {@code Map} of this game
     */
    public Map getMap() {
        return map;
    }

    /**
     * Sets the {@link Map} the game is played on.
     *
     * @param map the {@code Map} the game is played on
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Return the {@code id} of the {@link Player} that is controlled by the ai.
     *
     * @return the {@code id} of the {@code Player} controlled by the ai
     */
    public int getMe() {
        return me;
    }

    /**
     * Sets the {@code id} of the {@link Player} that is controlled by the ai.
     *
     * @param me the {@code id} of the {@code Player} controlled by the ai
     */
    public void setMe(int me) {
        this.me = me;
    }

    /**
     * Sets the array of {@link Player}s that participate in the game.
     *
     * @param players the {@code Player}s that participate
     */
    void setPlayers(Player[] players) {
        this.players = players;
    }

}
