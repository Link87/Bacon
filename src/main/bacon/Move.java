package bacon;

/**
 * An interface which defines the basic functions of each typ of move.
 */
public abstract class Move {
    protected int moveID;
    protected Map map;
    protected Player player;
    protected int xCoordinate;
    protected int yCoordinate;
    protected int bonusRequest;

    /**
     * Returns an instance of a Move subclass. The subclass is selected according to the provided data.
     *
     * @param moveID       the ID of the move
     * @param map          the map on which the move is executed
     * @param player       the player of the move
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param bonusRequest
     * @return Move
     * @throws IllegalArgumentException when the illegal data is provided
     */
    public static Move createNewMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        Tile tile = map.getTileAt(x, y);
        Player owner = tile.getOwner();
        Tile.Property tileProperty = tile.getProperty();

        if (player.isDisqualified()) throw new IllegalArgumentException("Player has already been disqualified");

        if (x >= map.width || y >= map.height) throw new IllegalArgumentException("Coordinate out of bounds");

        if (Game.getGame().getGamePhase() == Game.GamePhase.PHASE_ONE) {

            if (tileProperty == Tile.Property.HOLE) throw new IllegalArgumentException("Tile is a hole");

            else if (owner == player) throw new IllegalArgumentException("Tile is already occupied by player");

            else if (owner == null && tileProperty != Tile.Property.EXPANSION)
                return new RegularMove(moveID, map, player, x, y, bonusRequest);

            else return new OverrideMove(moveID, map, player, x, y, bonusRequest);

        } else if (Game.getGame().getGamePhase() == Game.GamePhase.PHASE_TWO) {

            if (tileProperty == Tile.Property.HOLE) throw new IllegalArgumentException("Tile is a hole");

            else return new BombMove(moveID, map, player, x, y, bonusRequest);

        } else if (Game.getGame().getGamePhase() == Game.GamePhase.ENDED)
            throw new IllegalArgumentException("Game has already ended");

        throw new IllegalArgumentException("Default Illegal Move");
    }

    /**
     * Creates a new move from the given values.
     *
     * @param moveID       the ID of the move
     * @param map          the map on which the move is executed
     * @param player       the player of the move
     * @param x            the x coordinate
     * @param y            the y coordinate
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
     * Checks if this move is legal.
     *
     * @return true if the move is legal, false otherwise
     */
    abstract boolean isLegal();


    /**
     * Executes this move.
     */
    abstract void doMove();

}
