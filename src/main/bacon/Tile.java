package bacon;

import java.util.Arrays;
import java.util.Set;

/**
 * A tile on the map.
 * <p>
 * A {@code Tile} can contain a single stone that is owned by a player. In this project, the concept of stones is simplified:
 * The ownership of the stone is represented by the ownership of that {@code Tile}.
 * <p>
 * A Tile may also have a special {@link Property}.
 */
public class Tile {

    public final int x;
    public final int y;
    /**
     * Neighbouring {@code Tile}s in each {@link Direction}. May also contains extraneous transitions.
     * The array index corresponds to the {@link Direction#id}.
     * <p>
     * If no transition is possible in a given direction, the value is set to {@code null}.
     */
    private final Tile[] transitions;
    /**
     * {@link Direction} in which the according transition arrives at the other tile.
     * The array index corresponds to the {@link Direction#id}.
     * <p>
     * If no transition is possible in a given direction, the array element is set to {@link Direction#NULL_DIRECTION_ID}.
     */
    private final int[] arrivals;
    private int ownerId;
    private Property property;
    // tiles that would be affected if this tile was bombed
    private Set<Tile> bombEffect;
    // the tile lines this tile is part of
    private TileLine row;
    private TileLine column;
    private TileLine diagonal;
    private TileLine indiagonal;

    /**
     * Creates a new {@code Tile} at the given position.
     * <p>
     * If the tile owner is set, the {@link Property} has to be set to {@link Property#DEFAULT}.
     *
     * @param ownerId  id of {@link Player} that owns the stone on this {@code Tile}.
     *                 Set to {@link Player#NULL_PLAYER_ID} if there is no stone on this {@code Tile}.
     * @param property Special {@code Property} that this {@code Tile} has
     * @param x        horizontal coordinate of this {@code Tile}
     * @param y        vertical coordinate of this {@code Tile}
     */
    public Tile(int ownerId, Property property, int x, int y) {
        this.ownerId = ownerId;
        this.property = property;

        assert this.ownerId == Player.NULL_PLAYER_ID || property == Property.DEFAULT : "Only default state can define initial owner";

        this.x = x;
        this.y = y;

        this.transitions = new Tile[Direction.DIRECTION_COUNT];
        this.arrivals = new int[Direction.DIRECTION_COUNT];
        Arrays.fill(this.arrivals, Direction.NULL_DIRECTION_ID);
    }

    /**
     * Sets the transition at the given {@code Direction}. The other {@code Tile} is either a neighbour
     * or declared as an additional transition partner in the map file.
     *
     * @param other     {@code Tile} the transition leads to
     * @param direction {@code Direction} in integer representation in which the transition is applied
     * @param arrival   {@code Direction} in integer representation in which the transition arrives at the other tile
     */
    void setTransition(Tile other, int direction, int arrival) {
        this.transitions[direction] = other;
        this.arrivals[direction] = arrival;
    }

    /**
     * Returns the tiles within bomb radius of this tile.
     *
     * @return the tiles within bomb radius of this tile.
     */
    public Set<Tile> getBombEffect() {
        return bombEffect;
    }

    /**
     * Sets the tiles within bomb radius of this tile.
     *
     * @param bombEffect the tiles within bomb radius of this tile.
     */
    void setBombEffect(Set<Tile> bombEffect) {
        this.bombEffect = bombEffect;
    }

    /**
     * Returns the horizontal {@link TileLine} the {@code Tile} is part of.
     *
     * @return the horizontal {@code TileLine} of the {@code Tile}
     */
    public TileLine getRow() {
        return row;
    }

    /**
     * Sets the horizontal {@link TileLine} the {@code Tile} is part of.
     *
     * @param row the horizontal {@code TileLine} of the {@code Tile}
     */
    void setRow(TileLine row) {
        this.row = row;
    }

    /**
     * Returns the vertical {@link TileLine} the {@code Tile} is part of.
     *
     * @return the vertical {@code TileLine} of the {@code Tile}
     */
    public TileLine getColumn() {
        return column;
    }

    /**
     * Sets the vertical {@link TileLine} the {@code Tile} is part of.
     *
     * @param column the vertical {@code TileLine} of the {@code Tile}
     */
    void setColumn(TileLine column) {
        this.column = column;
    }

    /**
     * Returns the diagonal {@link TileLine} the {@code Tile} is part of.
     *
     * @return the horizontal {@code TileLine} of the {@code Tile}
     */
    public TileLine getDiagonal() {
        return diagonal;
    }

    /**
     * Sets the diagonal {@link TileLine} the {@code Tile} is part of.
     *
     * @param diagonal the horizontal {@code TileLine} of the {@code Tile}
     */
    void setDiagonal(TileLine diagonal) {
        this.diagonal = diagonal;
    }

    /**
     * Returns the backwards diagonal (indiagonal) {@link TileLine} the {@code Tile} is part of.
     *
     * @return the indiagonal {@code TileLine} of the {@code Tile}
     */
    public TileLine getIndiagonal() {
        return indiagonal;
    }

    /**
     * Sets the backwards diagonal (indiagonal) {@link TileLine} the {@code Tile} is part of.
     *
     * @param indiagonal the indiagonal {@code TileLine} of the {@code Tile}
     */
    void setIndiagonal(TileLine indiagonal) {
        this.indiagonal = indiagonal;
    }

    /**
     * Applies a bomb to this {@code Tile}.
     * <p>
     * Makes a hole out of this {@code Tile} by removing its owner, its transitions to other {@code Tile}s
     * and other {@code Tile}s transition to it.
     * The {@code Property} is set to {@link Property#HOLE}
     */
    public void bombTile() {
        setProperty(Property.HOLE);
        setOwnerId(Player.NULL_PLAYER_ID);

        // remove transition from neighbors to bombed tile
        for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
            Tile neighbor = this.getTransition(direction);
            if (neighbor == null) continue;
            for (int neighborDirection = 0; neighborDirection < Direction.DIRECTION_COUNT; neighborDirection++) {
                Tile t = neighbor.getTransition(neighborDirection);
                if (t == this) neighbor.setTransition(null, neighborDirection, Direction.NULL_DIRECTION_ID);
            }
        }
        // remove transitions from bombed tile to neighbors
        for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
            this.setTransition(null, direction, Direction.NULL_DIRECTION_ID);
        }

    }

    /**
     * Returns the {@code Tile} the transition in the given {@code Direction} leads to.
     * Returns {@code null} if no transition is present.
     *
     * @param direction {@code Direction} in integer representation in which the transition is applied
     * @return the {@code Tile} the transition points to or {@code null} if no transition is present
     */
    public Tile getTransition(int direction) {
        return this.transitions[direction];
    }

    /**
     * Returns the {@code Direction} in which the transition arrives. Returns {@code null} if no transition is present.
     *
     * @param direction {@code Direction} in integer representation in which the transition starts on this tile
     * @return the arriving {@code Direction} in integer representation
     * or {@link Direction#NULL_DIRECTION_ID} if no transition is present in the given direction
     */
    public int getArrivalDirection(int direction) {
        return this.arrivals[direction];
    }

    /**
     * Returns the id of the owner of this {@code Tile}.
     *
     * @return the id of the owner of this {@code Tile} or {@link Player#NULL_PLAYER_ID} if tile is unoccupied
     */
    public int getOwnerId() {
        return this.ownerId;
    }

    /**
     * Sets the owner of this {@code Tile} and updates the players stones.
     * Also updates fill level and player share of map lines.
     *
     * @param ownerId id of new owner of this {@code Tile}.
     */
    public void setOwnerId(int ownerId) {
        int myId = Game.getGame().getCurrentState().getMe();
        if (this.ownerId != Player.NULL_PLAYER_ID) {
            Game.getGame().getCurrentState().getPlayerFromId(this.ownerId).removeStone(this);
        }
        if (ownerId != Player.NULL_PLAYER_ID) {
            Game.getGame().getCurrentState().getPlayerFromId(ownerId).addStone(this);
        }

        if (this.ownerId == Player.NULL_PLAYER_ID && ownerId != Player.NULL_PLAYER_ID) {
            this.row.changeFillLevel(1);
            this.column.changeFillLevel(1);
            this.diagonal.changeFillLevel(1);
            this.indiagonal.changeFillLevel(1);
        } else if (this.ownerId != Player.NULL_PLAYER_ID && ownerId == Player.NULL_PLAYER_ID) {
            this.row.changeFillLevel(-1);
            this.column.changeFillLevel(-1);
            this.diagonal.changeFillLevel(-1);
            this.indiagonal.changeFillLevel(-1);
        }
        if (this.ownerId != myId && ownerId == myId) {
            this.row.changePlayerShare(1);
            this.column.changePlayerShare(1);
            this.diagonal.changePlayerShare(1);
            this.indiagonal.changePlayerShare(1);
        } else if (this.ownerId == myId && ownerId != myId) {
            this.row.changePlayerShare(-1);
            this.column.changePlayerShare(-1);
            this.diagonal.changePlayerShare(-1);
            this.indiagonal.changePlayerShare(-1);
        }

        this.ownerId = ownerId;
    }

    /**
     * Returns the {@code Property} this {@code Tile} has.
     *
     * @return the {@code Property} of this {@code Tile}
     */
    public Property getProperty() {
        return this.property;
    }

    /**
     * Sets the (special) {@link Property} this {@code Tile} has.
     *
     * @param property the {@code Property} of this {@code Tile}
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Defines special properties a {@code Tile} might have.
     * <p>
     * {@code DEFAULT} is used when no special property is available.
     */
    public enum Property {
        DEFAULT,
        HOLE,
        CHOICE,
        INVERSION,
        BONUS,
        EXPANSION;

        /**
         * Translates a single character into the {@code Property} that is associated with that.
         * See the specification for details on this.
         *
         * @param c the character to translate
         * @return the {@code Property} that is associated with the given character
         */
        public static Property fromChar(char c) {
            switch (c) {
                case '-':
                    return HOLE;
                case 'c':
                    return CHOICE;
                case 'i':
                    return INVERSION;
                case 'b':
                    return BONUS;
                case 'x':
                    return EXPANSION;
                default:
                    return DEFAULT;
            }
        }
    }

}
