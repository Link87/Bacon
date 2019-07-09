package bacon;

import java.util.*;

/**
 * A Player class which contains player data and performs player actions.
 */
public class Player {

    /**
     * Integer representation of an invalid or {@code null} {@code Player} instance.
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
     * Creates a new {@code Player} instance.
     *
     * @param id                 index of the {@code Player}
     * @param overrideStoneCount amount of override stones the {@code Player} has
     * @param bombCount          amount o bombs the {@code Player} has
     */
    public Player(int id, int overrideStoneCount, int bombCount) {
        this.id = id;
        this.overrideStoneCount = overrideStoneCount;
        this.bombCount = bombCount;
        this.disqualified = false;
        this.stones = new HashSet<>();
    }

    /**
     * Returns the number of {@link Tile}s the {@code Player} currently owns
     *
     * @return the number of {@code Tile}s of the {@code Player}
     */
    public int getStoneCount() {
        return this.stones.size();
    }

    /**
     * Returns whether the {@code Player} is disqualified.
     *
     * @return {@code true} if the {@code Player} is disqualified, {@code false} otherwise
     */
    public boolean isDisqualified() {
        return this.disqualified;
    }

    /**
     * Returns the amount of override stones the {@code Player} has left.
     *
     * @return the amount of override stones of the {@code Player}
     */
    public int getOverrideStoneCount() {
        return this.overrideStoneCount;
    }

    /**
     * Returns the amount of bombs the {@code Player} has left.
     *
     * @return the amount of bombs of the {@code Player}
     */
    public int getBombCount() {
        return this.bombCount;
    }

    /**
     * Puts the given {@code Tile} in {@code Player}s possession.
     * <p>
     * Behavior is undefined on {@code null} values.
     *
     * @param tile {@code Tile} which now belongs to the {@code Player}
     */
    void addStone(Tile tile) {
        stones.add(tile);
    }

    /**
     * Removes ownership of the {@code Tile} formerly owned by this {@code Player}
     *
     * @param tile {@code Tile} which no longer belongs to this {@code Player}
     */
    void removeStone(Tile tile) {
        stones.remove(tile);
    }

    /**
     * Returns the {@code Tile}s owned by the {@code Player}.
     *
     * @return a set containing all of the {@code Player}s tiles
     */
    public Set<Tile> getStones() {
        return Collections.unmodifiableSet(this.stones);
    }

    /**
     * Changes the amount of override stones of this {@code Player} by the given number.
     * Provide a negative number to remove stones from the {@code Player}s inventory.
     *
     * @param n amount of override stones that are added, if value is positive, or removed, if value is negative
     */
    public void receiveOverrideStone(int n) {
        this.overrideStoneCount += n;
    }

    /**
     * Changes the amount of bombs of this {@code Player} by the given number.
     * Provide a negative number to remove bombs from the {@code Player}s inventory.
     *
     * @param n amount of bombs that are added, if value is positive, or removed, if value is negative
     */
    public void receiveBomb(int n) {
        this.bombCount += n;
    }

    /**
     * Marks this {@code Player} as disqualified.
     */
    void disqualify() {
        this.disqualified = true;
    }

    /**
     * Returns whether the {@code Player} is the same as the given object. Returns {@code true}, when object is a
     * {@code Player} instance and has the same id.
     *
     * @param obj object to compare with
     * @return {@code true} if both objects are {@code Player}s and {@link Player#id}s are equal, {@code false} otherwise
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