package bacon;

import java.util.Arrays;

/**
 * A tile on the map.
 * <p>
 * A {@code Tile} can contain a single stone that is owned by a player. In this project, the concept of stones is simplified:
 * The ownership of the stone is represented by the ownership of that {@code Tile}.
 * <p>
 * A Tile may also have a special {@link Property}.
 */
public class Tile {

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

    public final int x;
    public final int y;

    /**
     * Row/column/diagonal/indiagonal the tile belongs to
     */
    private MapLine row;
    private MapLine column;
    private MapLine diagonal;
    private MapLine indiagonal;

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

        this.transitions = new Tile[Direction.values().length];
        this.arrivals = new int[Direction.values().length];
        Arrays.fill(this.arrivals, Direction.NULL_DIRECTION_ID);
    }

    /**
     * Sets the owner of this {@code Tile} and updates the players stones.
     *
     * @param ownerId id of new owner of this {@code Tile}.
     */
    public void setOwnerId(int ownerId) {
        if (this.ownerId != Player.NULL_PLAYER_ID) {
            Game.getGame().getCurrentState().getPlayerFromId(this.ownerId).removeStone(this);
        }
        if (ownerId != Player.NULL_PLAYER_ID) {
            Game.getGame().getCurrentState().getPlayerFromId(ownerId).addStone(this);
        }
        this.ownerId = ownerId;

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

    public void setRow(MapLine row) {
        this.row = row;
    }

    public void setColumn(MapLine column) {
        this.column = column;
    }

    public void setDiagonal(MapLine diagonal) {
        this.diagonal = diagonal;
    }

    public void setIndiagonal(MapLine indiagonal) {
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
        for (int direction = 0; direction < Direction.values().length; direction++) {
            Tile neighbor = this.getTransition(direction);
            if (neighbor == null) continue;
            for (int neighborDirection = 0; neighborDirection < Direction.values().length; neighborDirection++) {
                Tile t = neighbor.getTransition(neighborDirection);
                if (t == this) neighbor.setTransition(null, neighborDirection, Direction.NULL_DIRECTION_ID);
            }
        }
        // remove transitions from bombed tile to neighbors
        for (int direction = 0; direction < Direction.values().length; direction++) {
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
     * Returns the {@code Property} this {@code Tile} has.
     *
     * @return the {@code Property} of this {@code Tile}
     */
    public Property getProperty() {
        return this.property;
    }

    public MapLine getRow() {
        return row;
    }

    public MapLine getColumn() {
        return column;
    }

    public MapLine getDiagonal() {
        return diagonal;
    }

    public MapLine getIndiagonal() {
        return indiagonal;
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
