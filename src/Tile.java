/**
 * A tile on the map. A Tile may have a special {@link Property}.
 *
 * A tile can contain a single stone that is owned by a player. In this project, the concept of stones is simplified:
 * The ownership of the stone is represented by the ownership of that tile.
 */
public class Tile {

    /**
     * Neighbouring tiles in each direction. May also contains extraneous transitions.
     * The direction is defined by the array index, as defined in {@link Direction}.
     *
     * If no transition is possible in a given direction, the array element is set to  <code>null</code>.
     */
    private final Tile[] transitions;

    private Player owner;
    private Property property;

    public final int x;
    public final int y;

    /**
     * Creates a new Tile at the given position. If the owner is set, the property has to be set to <code>DEFAULT</code>.
     * @param owner     {@link Player} that owns the stone on this Tile. Set to <code>null</code> if there is no stone on this Tile.
     * @param property  Special {@link Property} that this Tile has
     * @param x         horizontal coordinate of this Tile
     * @param y         vertical coordinate of this Tile
     */
    public Tile(Player owner, Property property, int x, int y){
        this.owner = owner;

        if (this.owner == null && property != Property.DEFAULT)
            this.property = property;
        else throw new IllegalArgumentException("Only default state can define initial owner");

        this.x = x;
        this.y = y;

        this.transitions = new Tile[Direction.values().length];
    }

    /**
     * Sets the owner of this Tile. Use <code>null</code> to remove any ownership.
     * @param owner new owner of this Tile. <code>null</code> resets ownership
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Sets the (special) {@link Property} this Tile has.
     * @param property the {@link Property} of this Tile
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Sets the transition at the given direction. The other tile is either a neighbour
     * or declared as an additional transition partner in the map file.
     * @param other Tile the transition leads to
     * @param direction Direction in which the transition is applied
     */
    public void setTransition(Tile other, Direction direction) {
        this.transitions[direction.index] = other;
    }

    /**
     * Gets the Tile the transition in the given direction leads to. Returns <code>null</code> if no transition is present.
     * @param direction {@link Direction} in which the transition is applied
     * @return  the Tile the transition points to or <code>null</code> if no transition is present
     */
    public Tile getTransition(Direction direction) { return transitions[direction.index]; }

    /**
     * Returns the owner of this Tile
     * @return  the owner of this Tile
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Returns the Property this Tile has.
     * @return the Property of this Tile
     */
    public Property getProperty() {
        return property;
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
         * @param c the character to translate
         * @return  the Property that is associated with the given character
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
