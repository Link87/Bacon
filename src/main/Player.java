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
    }

    public static Player readFromString(int number, int initOverrideStoneCount, int initBombCount){
        Player player = new Player(number, initOverrideStoneCount, initBombCount);
        return player;
    }

    /**
     * Places a stone from this Player on the given Map instance at the given position.
     *
     * @param map the Map the stone is placed on
     * @param x   x coordinate of the Tile the stone is placed on
     * @param y   y coordinate of the Tile the stone is placed on
     */
    public void placeStoneOnMap(Map map, int x, int y, int bonus) {
        map.placeStone(this, x, y, bonus);
    }

    /**
     * Places an override stone from this Player on the given Map instance at the given position.
     * Decreases the amount of override stones of this player.
     *
     * @param map the Map the override stone is placed on
     * @param x   x coordinate of the Tile the override stone is placed on
     * @param y   y coordinate of the Tile the override stone is placed on
     */
    public void useOverrideStone(Map map, int x, int y, int bonus) {
        this.overrideStoneCount--;
        map.placeOverrideStone(this, x, y, bonus);
    }

    /**
     * Throws a bomb from this Player on the given Map instance at the given position.
     * Decreases the amount of bombs of this player.
     *
     * @param map the Map the bomb is thrown on
     * @param x   x coordinate of the Tile the bomb is thrown on
     * @param y   y coordinate of the Tile the bomb is thrown on
     */
    public void useBomb(Map map, int x, int y) {
        this.bombCount--;
        map.throwBomb(this, x, y);
    }

    public void disqualify() {
        this.disqualified = true;
    }

}