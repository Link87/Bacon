/**
 * this enum represents special arguments which are necessary for executing the purpose of the bonus tiles
 * Bomb and Override apply to the Bonus tile
 * The numbers apply to the player number from the player we want to change tiles with on a choice tile
 * None is simply that there is either a inversion field not a special field at all
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
    EIGHT
}
