package bacon.move;

import bacon.Game;
import bacon.Map;
import bacon.Player;
import bacon.Tile;
import bacon.GamePhase;

import java.nio.ByteBuffer;

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

        if (Game.getGame().getCurrentState().getGamePhase() == GamePhase.PHASE_ONE) {

            if (tileProperty == Tile.Property.HOLE) return new DefaultIllegalMove();

            else if (owner == player) return new DefaultIllegalMove();

            else if (owner == null && tileProperty != Tile.Property.EXPANSION)
                return new RegularMove(moveID, map, player, x, y, bonusRequest);

            else return new OverrideMove(moveID, map, player, x, y, bonusRequest);

        } else if (Game.getGame().getCurrentState().getGamePhase() == GamePhase.PHASE_TWO) {

            if (tileProperty == Tile.Property.HOLE) return new DefaultIllegalMove();

            else return new BombMove(moveID, map, player, x, y, bonusRequest);

        } else if (Game.getGame().getCurrentState().getGamePhase() == GamePhase.ENDED)
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
    public abstract boolean isLegal();


    /**
     * Executes this move.
     */
    public abstract void doMove();

    /**
     * Returns the move in binary representation.
     *
     * @return byte array containing this moves binary representation
     */
    public byte[] encodeBinary() {
        var data = new byte[5];
        ByteBuffer.wrap(data)
                .putShort((short) xCoordinate)
                .putShort((short) yCoordinate)
                .put((byte) 0);

        return data;
    }

}
