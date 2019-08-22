package bacon;

import bacon.move.Move;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Collection of {@link Tile}s. Represents a single row, column, diagonal or indiagonal on the map.
 * <p>
 * This is used for calculating stability and override stability evaluation values.
 * Completely filled rows are considered stable and rows filled completely with players own stones are considered
 * override stable.
 */
public class TileLine {

    private final Set<Tile> lineTiles;
    private int lineSize;
    private int fillLevel;
    private int playerShare;

    /**
     * Creates a new {@code TileLine} instance.
     * <p>
     * Call this in the static map analysis.
     */
    public TileLine() {
        this.lineTiles = new HashSet<>();
        this.lineSize = 0;
        this.fillLevel = 0;
        this.playerShare = 0;
    }

    /**
     * Adds the given {@link Tile} to the {@code TileLine} instance.
     * <p>
     * {@code lineSize} and {@code fillLevel} are updated.
     *
     * @param tile the {@code Tile} to be added
     */
    private void addTile(Tile tile) {
        if (!this.lineTiles.contains(tile)) {
            this.lineTiles.add(tile);
            this.lineSize++;
            if (tile.getOwnerId() != Player.NULL_PLAYER_ID) this.fillLevel++;
        }
    }

    /**
     * Initializes the player share of the {@code TileLine}.
     * <p>
     * Call this after we have been assigned our player number by the server.
     */
    void initializePlayerShare() {
        for (Tile t : this.lineTiles) {
            if (t.getOwnerId() != Player.NULL_PLAYER_ID) {
                if (t.getOwnerId() == Game.getGame().getCurrentState().getMe()) this.playerShare++;
            }
        }
    }

    /**
     * Updates player share after a move.
     * <p>
     * Must be updated after every {@link Move#doMove()} and {@link Move#undoMove()}
     *
     * @param count the number of new player's stones added
     */
    void changePlayerShare(int count) {
        playerShare += count;
    }

    /**
     * Updates fill level after a move.
     * <p>
     * Must be updated after every {@link Move#doMove()} and {@link Move#undoMove()}
     *
     * @param count the number of new opponent's stones added
     */
    void changeFillLevel(int count) {
        fillLevel += count;
    }

    /**
     * Returns the {@link Tile}s in the {@code TileLine}.
     *
     * @return a set of {@code Tile}s being in the {@code TileLine}
     */
    Set<Tile> getLineTiles() {
        return Collections.unmodifiableSet(lineTiles);
    }

    /**
     * Returns the amount of {@link Tile}s in the {@code TileLine}.
     *
     * @return the size of the {@code TileLine}
     */
    public int getLineSize() {
        return lineSize;
    }

    /**
     * Returns the fill level.
     * <p>
     * The fill level is the amount of occupied tiles in the {@code TileLine}.
     *
     * @return the fill level
     */
    public int getFillLevel() {
        return fillLevel;
    }

    /**
     * Returns the player share.
     *
     * @return the player share
     */
    public int getPlayerShare() {
        return playerShare;
    }

    /**
     * Finds all tiles that belong to the {@code TileLine}.
     * <p>
     * It searches from the given {@link Tile} in the given {@link Direction}.
     *
     * @param origin    The {@code Tile} for which a new {@code TileLine} has to be created.
     *                  Search originates from this {@code Tile}.
     * @param direction the {@link Direction} in integer representation to start the search
     */
    void lineSearch(Tile origin, int direction) {
        Tile curTile = origin;
        this.addTile(origin);

        int searchDirection = direction;
        int arrivalDirection;

        while (curTile.getTransition(searchDirection) != null) {
            arrivalDirection = curTile.getArrivalDirection(searchDirection);

            curTile = curTile.getTransition(searchDirection);
            if (curTile == origin && Direction.oppositeOf(arrivalDirection) == direction) break;
            this.addTile(curTile);

            switch (Direction.fromId(arrivalDirection)) {
                case UP:
                case DOWN:
                    curTile.setColumn(this);
                    break;
                case RIGHT:
                case LEFT:
                    curTile.setRow(this);
                    break;
                case UP_LEFT:
                case DOWN_RIGHT:
                    curTile.setIndiagonal(this);
                    break;
                case UP_RIGHT:
                case DOWN_LEFT:
                    curTile.setDiagonal(this);
                    break;
            }
            searchDirection = Direction.oppositeOf(arrivalDirection);
        }
    }

}
