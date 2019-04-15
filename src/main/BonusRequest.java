/**
 * This enum represents special arguments which are necessary for executing the purpose of the bonus tiles.
 * Bomb and Override apply to the Bonus tile.
 * The numbers apply to the player number from the player we want to change tiles with on a choice tile.
 * None is simply that there is either a inversion field not a special field at all.
 */
public enum BonusRequest {
    NONE,
    BOMB,
    OVERRIDE,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT;

    /**
     * Translates the enum variant into the corresponding player number
     *
     * @return the number of the player
     * @throws IllegalArgumentException if input is not a valid player number
     */
    public int getPlayerNumber(){
        switch (this){
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            default:
                throw new IllegalArgumentException("BonusRequest is not a player's number");
        }
    }

    /**
     * Translates the BonusRequest number into an enum. See the specification for details.
     *
     * @param bonusNr The BonusRequest as a number
     * @return The BonusRequest as an enum
     * @throws IllegalArgumentException for bonusNr which cannot be translated to the enum
     */
    public static BonusRequest fromNumber(int bonusNr){
        switch(bonusNr){
            case 0:
                return BonusRequest.NONE;
            case 1:
                return BonusRequest.ONE;
            case 2:
                return BonusRequest.TWO;
            case 3:
                return BonusRequest.THREE;
            case 4:
                return BonusRequest.FOUR;
            case 5:
                return BonusRequest.FIVE;
            case 6:
                return BonusRequest.SIX;
            case 7:
                return BonusRequest.SEVEN;
            case 8:
                return BonusRequest.EIGHT;
            case 20:
                return BonusRequest.BOMB;
            case 21:
                return BonusRequest.OVERRIDE;
            default:
                throw new IllegalArgumentException("The BonusRequestNumber is not valid");
        }
    }

}
