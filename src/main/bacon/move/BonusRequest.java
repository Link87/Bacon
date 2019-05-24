package bacon.move;

import bacon.GameState;
import bacon.Player;

public class BonusRequest {

    /**
     * This enum contains the types a bonus request can have.
     */
    public enum Type {
        NONE,
        BOMB_BONUS,
        OVERRIDE_BONUS,
        CHOOSE_PLAYER
    }

    public final Type type;
    private int other;

    public BonusRequest(Type type) {
        this(type, Player.NULL_PLAYER_ID);
    }

    /**
     * Create a new BonusRequest for switching tiles with the given player-
     *
     * @param other Player to switch tiles with
     */
    public BonusRequest(int other) {
        this(Type.CHOOSE_PLAYER, other);
    }

    private BonusRequest(Type type, int other) {
        this.type = type;
        this.other = other;
    }

    /**
     * Reads the value and returns a BonusRequest of the according type.
     *
     * @param value value to translate
     * @param state GameState to use
     * @return a BonusRequest of the right type
     */
    public static BonusRequest fromValue(int value, GameState state) {
        if (value > 0 && value <= state.getTotalPlayerCount()) {
            var request = new BonusRequest(Type.CHOOSE_PLAYER);
            request.other = value;
            return request;
        } else if (value == 20)
            return new BonusRequest(Type.BOMB_BONUS);
        else if (value == 21)
            return new BonusRequest(Type.OVERRIDE_BONUS);
        else return null;
    }

    /**
     * Returns the player to switch tiles with or <code>null</code> if that player is not set (i.e. when a bonus type)
     *
     * @return Player to switch tiles with or <code>null</code>
     */
    int getOtherPlayerId() {
        return other;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        if (this.type == Type.CHOOSE_PLAYER)
            return ((BonusRequest) obj).type == Type.CHOOSE_PLAYER && this.other == ((BonusRequest) obj).other;
        return this.type == ((BonusRequest) obj).type;
    }

    @Override
    public int hashCode() {
        return 31 * (31 + type.hashCode()) + other;
    }

    /**
     * Returns the binary value that belongs to the BonusRequest.
     *
     * @return value of the request
     */
    public byte toValue() {
        switch (type) {
            case BOMB_BONUS:
                return 20;
            case OVERRIDE_BONUS:
                return 21;
            case CHOOSE_PLAYER:
                return (byte) other;
            default:
                return 0;
        }
    }
}