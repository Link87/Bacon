package bacon;

import java.util.Arrays;

/**
 * A tile on the map. A Tile may have a special {@link Property}.
 * <p>
 * A tile can contain a single stone that is owned by a player. In this project, the concept of stones is simplified:
 * The ownership of the stone is represented by the ownership of that tile.
 */
public class Tile {

    /**
     * Neighbouring tiles in each direction. May also contains extraneous transitions.
     * The direction is defined by the array index, as defined in {@link Direction}.
     * <p>
     * If no transition is possible in a given direction, the value is set to <code>null</code>.
     */
    private final Tile[] transitions;

    /**
     * Direction in which the according transition arrives at the other tile.
     * The direction is defined by the array index, as defined in {@link Direction}.
     * <p>
     * If no transition is possible in a given direction, the array element is set to <code>Direction.NULL_DIRECTION_ID</code>.
     */
    private final int[] arrivals;

    private int ownerId;
    private Property property;

    public final int x;
    public final int y;

    /**
     * Creates a new Tile at the given position. If the owner is set, the property has to be set to <code>DEFAULT</code>.
     *
     * @param ownerId  number of {@link Player} that owns the stone on this Tile.
     *                 Set to <code>Player.NULL_PLAYER_ID</code> if there is no stone on this Tile.
     * @param property Special {@link Property} that this Tile has
     * @param x        horizontal coordinate of this Tile
     * @param y        vertical coordinate of this Tile
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
     * Sets the owner of this Tile and updates the players stones.
     *
     * @param ownerId id of new owner of this Tile.
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
     * Sets the (special) {@link Property} this Tile has.
     *
     * @param property the {@link Property} of this Tile
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Sets the transition at the given direction. The other tile is either a neighbour
     * or declared as an additional transition partner in the map file.
     *
     * @param other     Tile the transition leads to
     * @param direction Direction in which the transition is applied
     * @param arrival   Direction in which the transition arrives at the other tile
     */
    void setTransition(Tile other, int direction, int arrival) {
        this.transitions[direction] = other;
        this.arrivals[direction] = arrival;
    }


    /**
     * Makes a hole out of a tile by removing its owner, its transitions to other tiles and other tiles' transition to it.
     * Also sets property to 'HOLE'.
     */
    public void bombTile() {
        setProperty(Property.HOLE);
        setOwnerId(Player.NULL_PLAYER_ID);

        //remove transition from neighbors to bombed tile
        for (int direction = 0; direction < Direction.values().length; direction++) {
            Tile neighbor = this.getTransition(direction);
            if (neighbor == null) continue;
            for (int neighborDirection = 0; neighborDirection < Direction.values().length; neighborDirection++) {
                Tile t = neighbor.getTransition(neighborDirection);
                if (t == this) neighbor.setTransition(null, neighborDirection, Direction.NULL_DIRECTION_ID);
            }
        }
        //remove transitions from bombed tile to neighbors
        for (int direction = 0; direction < Direction.values().length; direction++) {
            this.setTransition(null, direction, Direction.NULL_DIRECTION_ID);
        }

    }


    /**
     * Returns the Tile the transition in the given direction leads to. Returns <code>null</code> if no transition is present.
     *
     * @param direction {@link Direction} in which the transition is applied
     * @return the Tile the transition points to or <code>null</code> if no transition is present
     */
    public Tile getTransition(int direction) {
        return this.transitions[direction];
    }

    /**
     * Returns the direction in which the transition arrives. Returns <code>null</code> if no transition is present.
     *
     * @param direction {@link Direction} in which the transition starts on this tile
     * @return the arriving direction or <code>Direction.NULL_DIRECTION_ID</code> if no transition is present in the given direction
     */
    public int getArrivalDirection(int direction) {
        return this.arrivals[direction];
    }

    /**
     * Returns the id of the owner of this Tile.
     *
     * @return the id of the owner of this Tile or <code>Player.NULL_PLAYER_ID</code> if tile is unoccupied
     */
    public int getOwnerId() {
        return this.ownerId;
    }

    /**
     * Returns the Property this Tile has.
     *
     * @return the Property of this Tile
     */
    public Property getProperty() {
        return this.property;
    }

    /**
     * Defines special properties a Tile might have. <code>DEFAULT</code> is used when no special property is available.
     */
    public enum Property {
        DEFAULT,
        HOLE,
        CHOICE,
        INVERSION,
        BONUS,
        EXPANSION;

        /**
         * Translates a single character into the Property that is associated with that.
         * See the specification for details on this.
         *
         * @param c the character to translate
         * @return the Property that is associated with the given character
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
