/**
 * An interface which defines the basic functions of each typ of move
 */
public abstract class Move {
    protected int moveID;
    protected Map map;
    protected Player player;
    protected int xCoordinate;
    protected int yCoordinate;
    protected int bonusRequest;


    public static Move createNewMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        Tile tile = map.getTileAt(x, y);
        Player owner = tile.getOwner();
        Tile.Property tileProperty = tile.getProperty();

        if (player.getStatus() == true) throw new IllegalArgumentException("Player has already been disqualified");
        else if (x >= map.width || y>= map.height) throw new IllegalArgumentException("Coordinate out of bounds");

        else if (Game.getGamePhase() == Game.GamePhase.PHASEONE) {

            if (tileProperty == Tile.Property.HOLE) throw new IllegalArgumentException("Tile is a hole");

            else if (owner == player) throw new IllegalArgumentException("Tile is already occupied by player");

            else if (owner == null && tileProperty != Tile.Property.EXPANSION) {
                Move regularmove = new RegularMove(moveID, map, player, x, y, bonusRequest);
                return regularmove;
            }

            else {
                Move overridemove = new OverrideMove(moveID, map, player, x, y, bonusRequest);
                return overridemove;
            }
        }

        else if (Game.getGamePhase() == Game.GamePhase.PHASETWO) {
            Move bombmove = new BombMove(moveID, map, player, x, y, bonusRequest);
            return bombmove;
        }

        else if (Game.getGamePhase() == Game.GamePhase.ENDED) throw new IllegalArgumentException("Game has already ended");

        Move defaultillegalmove = new DefaultIllegalMove(moveID, map, player, x, y, bonusRequest);
        return defaultillegalmove;
    }

    /**
     * Constructor
     *
     * @param moveID
     * @param map
     * @param player
     * @param x
     * @param y
     * @param bonusRequest
     */
    public Move(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        this.moveID = moveID;
        this.map = map;
        this.player = player;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.bonusRequest = bonusRequest;
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
