package bacon;

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

    public GameState(Player[] players,Map map,GamePhase currentPhase){
        this.players = players;
        this.map = map;
        this.currentPhase = currentPhase;
    }

    public GameState getDeepCopy(){
        return null;
    }

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

}
