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
     * These methods return at
     */
    public int getPlayerNumber() {return this.number;}

    public boolean getStatus() {return this.disqualified;}

    public int getOverrideStoneCount() {return this.overrideStoneCount;}

    public int getBombCount() {return this.bombCount;}


    /**
     * Increases the amount of override stones of this player.
     */
    public void receiveOverrideStone(int n) {
        this.overrideStoneCount += n;
    }

    /**
     * Increases the amount of bombs of this player.
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