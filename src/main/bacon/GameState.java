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
    public GameState(){}

    /**
     * shallow copy constructor for deepCopy method
     * @param players
     * @param map
     * @param currentPhase
     * @param me
     */
    public  GameState(Player[] players,Map map, GamePhase currentPhase, Player me){
        this.players = players;
        this.map = map;
        this.currentPhase = currentPhase;
        this.me = me;
    }

    /**
     * Creates a meaningful deepCopy of GameState.
     * Tiles and their owners are linked up here.
     * @return deepCopy of GameState
     */
    public GameState getDeepCopy(){
        Map mapCopy = this.map.semiDeepCopy();

        Player[] playersCopy = new Player[this.getTotalPlayerCount()];
        //every Tile in CopyMap with an owner gets the correct owner linked
        //every Player gets the tiles he owns correctly assigned
        for (int i = 0; i < playersCopy.length; i++) {
            playersCopy[i] = this.players[i].shallowCopy();
            Iterator<Tile> itr = this.players[i].getStonesIterator();
            while (itr.hasNext()){
                Tile stone = itr.next();
                int xPos = stone.x;
                int yPos = stone.y;
                mapCopy.getTileAt(xPos,yPos).setOwner(playersCopy[i]);
                playersCopy[i].addStone(mapCopy.getTileAt(xPos,yPos));
            }
        }

        Player meCopy = playersCopy[this.me.getPlayerNumber()-1];

        return new GameState(playersCopy,mapCopy,this.currentPhase,meCopy);
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
