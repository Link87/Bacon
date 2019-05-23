package bacon;

import java.util.*;

/**
 * A Player class which contains player data and performs player actions.
 */
public class Player {

    /**
     * Integer representation of an invalid or <code>null</code> player instance.
     */
    public static final int NULL_PLAYER_ID = 0;

    /**
     * The index of the player.
     */
    public final int id;
    private int overrideStoneCount;
    private int bombCount;
    private boolean disqualified;
    private Set<Tile> stones;

    /**
     * Creates a new Player instance.
     *
     * @param id                 index of the player
     * @param overrideStoneCount amount of override stones the player has
     * @param bombCount          amount o bombs the player has
     */
    public Player(int id, int overrideStoneCount, int bombCount) {
        this.id = id;
        this.overrideStoneCount = overrideStoneCount;
        this.bombCount = bombCount;
        this.disqualified = false;
        // Create a set that only hold weak references to its contents
        // This may break the invariants of all set methods, if not handled carefully!
        this.stones = new HashSet<>();
    }

    /**
     * Creates a shallow copy, meaning that (this != copy)
     * stones Set is left empty
     *
     * @return a shallow copy
     */
    Player shallowCopy() {
        Player copy = new Player(this.id, this.overrideStoneCount, this.bombCount);
        copy.disqualified = this.disqualified;
        return copy;
    }

    /**
     * Returns the number of tiles the player currently owns
     *
     * @return the number of tiles of the player
     */
    public int getStoneCount() {
        return this.stones.size();
    }

    /**
     * Returns whether the player is disqualified.
     *
     * @return true if the player is disqualified
     */
    public boolean isDisqualified() {
        return this.disqualified;
    }

    /**
     * Returns the amount of override stones the player has left.
     *
     * @return amount of override stones
     */
    public int getOverrideStoneCount() {
        return this.overrideStoneCount;
    }

    /**
     * Returns the amount of bombs the player has left.
     *
     * @return amount of bombs
     */
    public int getBombCount() {
        return this.bombCount;
    }

    /**
     * Places tile in players possession.
     * <p>
     * Behavior is undefined on <code>null</code> values.
     *
     * @param tile which now belongs to the player
     */
    void addStone(Tile tile) {
        stones.add(tile);
    }

    /**
     * Removes ownership of the tile owned by this owner
     *
     * @param tile which no longer belongs to this player
     */
    void removeStone(Tile tile) {
        stones.remove(tile);
    }

    /**
     * Returns the tiles owned by the player.
     *
     * @return a set containing all of the players tiles
     */
    public Set<Tile> getStones() {
        return Collections.unmodifiableSet(this.stones);
    }

    /**
     * Changes the amount of override stones of this player by given number.
     *
     * @param n amount of stones that are added (or removed when negative)
     */
    public void receiveOverrideStone(int n) {
        this.overrideStoneCount += n;
    }

    /**
     * Changes the amount of bombs of this player by given number.
     *
     * @param n amount of stones that are added (or removed when negative)
     */
    public void receiveBomb(int n) {
        this.bombCount += n;
    }

    /**
     * Marks this player as disqualified.
     */
    public void disqualify() {
        this.disqualified = true;
    }

    /**
     * Returns whether the player is the same as the given object. Returns <code>true</code>, when object is a Player
     * instance and has the same player number.
     *
     * @param obj object to compare with
     * @return true if player numbers are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        return this.id == ((Player) obj).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}