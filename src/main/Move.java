/**
 * An interface which defines the basic functions of each typ of move
 */
public abstract class Move {
    private int moveID;
    private Map map;
    private Player player;
    private int xCoordinate;
    private int yCoordinate;
    private Tile tile = map.getTileAt(xCoordinate, yCoordinate);


    public static void createNewMove(Map map, Player player, int x, int y, int bonusRequest) {
        if (player.getStatus() == true) throw new IllegalArgumentException("Player has already been disqualified");
        else if (x >= map.width || y>= map.height) throw new IllegalArgumentException("Coordinate out of bounds");
        else if (Game.getGamePhase() == Game.GamePhase.ENDED) throw new IllegalArgumentException("Game has already ended");

        else if (Game.getGamePhase() == Game.GamePhase.PHASETWO) {

        }

    }


    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    abstract boolean isLegal();


    /**
    * execute a move
    */

    abstract void doMove();

}
