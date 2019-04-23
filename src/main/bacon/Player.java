package bacon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Player class which contains player data and performs player actions.
 */
public class Player {

    /**
     * The index of the player.
     */
    public final int number;
    private int overrideStoneCount;
    private int bombCount;
    private boolean disqualified;
    private Set<Tile> stones;

    /**
     * Creates a new Player instance.
     *
     * @param number             index of the player
     * @param overrideStoneCount amount of override stones the player has
     * @param bombCount          amount o bombs the player has
     */
    public Player(int number, int overrideStoneCount, int bombCount) {
        this.number = number;
        this.overrideStoneCount = overrideStoneCount;
        this.bombCount = bombCount;
        this.disqualified = false;
        this.stones = new HashSet<>();
    }

    /**
     * Creates a shallow copy, meaning that (this != copy) but (copy.stones == this.stones)
     * @return a shallow copy
     */
    public Player shallowCopy(){
        Player copy = new Player(this.number,this.overrideStoneCount,this.bombCount);
        copy.disqualified = this.disqualified;
        copy.stones = this.stones;
        return copy;
    }
    /**
     * Returns the number of the player.
     *
     * @return number of player
     */
    public int getPlayerNumber() {
        return this.number;
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
     * Returns an iterator over all tiles owned by the player.
     *
     * @return an iterator containing all of the players tiles
     */
    public Iterator<Tile> getStonesIterator() {
        return this.stones.iterator();
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

}