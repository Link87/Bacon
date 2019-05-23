package bacon;

import java.util.Iterator;

/**
 * Instances of this class contain stateful information about the game.
 * Queries for stateless information are redirected to the Game singleton
 */
public class GameState {

    /**
     * Contains players in order, where index is number - 1.
     */
    private Player[] players;
    private Map map;
    private GamePhase currentPhase;
    private Player me;

    /**
     * constructor for when no information is known
     */
    public GameState() {}

    /**
     * Shallow copy constructor for deepCopy method.
     *
     * @param players      Array of Player that participate in the game
     * @param map          map on which the game is played
     * @param currentPhase the current GamePhase
     * @param me           the Player that uses the AI, has to be contained in the players array
     */
    private GameState(Player[] players, Map map, GamePhase currentPhase, Player me) {
        this.players = players;
        this.map = map;
        this.currentPhase = currentPhase;
        this.me = me;
    }

    /**
     * Creates a meaningful deepCopy of GameState.
     * Tiles and their owners are linked up here.
     *
     * @return deepCopy of GameState
     */
    public GameState getDeepCopy() {
        Map mapCopy = this.map.semiDeepCopy();

        Player[] playersCopy = new Player[this.getTotalPlayerCount()];
        //every Tile in CopyMap with an owner gets the correct owner linked
        //every Player gets the tiles he owns correctly assigned
        for (int i = 0; i < playersCopy.length; i++) {
            playersCopy[i] = this.players[i].shallowCopy();
            for (Tile stone : this.players[i].getStones()) {
                mapCopy.getTileAt(stone.x, stone.y).setOwner(playersCopy[i]);
                playersCopy[i].addStone(mapCopy.getTileAt(stone.x, stone.y));
            }
        }

        Player meCopy = playersCopy[this.me.getPlayerNumber() - 1];

        return new GameState(playersCopy, mapCopy, this.currentPhase, meCopy);
    }

    /**
     * This method finds the current player for a given player number.
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
        return Game.getGame().getBombRadius();
    }

    /**
     * Returns the total amount of players that participate in the game.
     * This value is constant throughout the game.
     *
     * @return the total player count
     */
    public int getTotalPlayerCount() {
        return Game.getGame().getTotalPlayerCount();
    }

    //TODO: Implement getTotalTileCount() and getOccupiedTileCount() to make clustering heuristic weighted
    /**
     * Returns the total amount of non-hole tiles on the map. This value is constant throughout the game.
     * HAS NOT BEEN IMPLEMENTED YET!!!
     *
     * @return the total non-hole tile count
     */
    public int getTotalTileCount() { return this.map.getTotalTiles(); }

    /**
     * Returns the amount of occupied tiles on the map, including expansion tiles.
     * HAS NOT BEEN IMPLEMENTED YET!!!
     *
     * @return the occupied tile count
     */
    public int getOccupiedTileCount() { return this.map.getOccupiedTiles(); }

    /**
     * Returns the map this game is played on.
     *
     * @return the map of this game
     */
    public Map getMap() {
        return map;
    }

    public Player getMe() {
        return me;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setGamePhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public void setMe(Player me) {
        this.me = me;
    }

}
