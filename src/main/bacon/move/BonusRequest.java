package bacon.move;

import bacon.GameState;
import bacon.Player;

/**
 * A class for bonus requests of choice or bonus {@link bacon.Tile}s.
 */
public class BonusRequest {

    /**
     * The {@link Type} the {@code BonusRequest} is of.
     */
    public final Type type;
    /**
     * The {@code id} of the other {@link Player}, if {@code type} is {@link Type#CHOOSE_PLAYER}.
     */
    private int other;

    /**
     * Creates a new {@code BonusRequest} with the given {@link Type}.
     *
     * @param type the {@code Type} the {@code BonusRequest} is of
     */
    public BonusRequest(Type type) {
        this(type, Player.NULL_PLAYER_ID);
    }

    /**
     * Create a new {@code BonusRequest} for switching {@link bacon.Tile}s with the {@link Player} with the given {@code id}.
     *
     * @param other {@code id} of {@code Player} to switch {@code Tile}s with
     */
    public BonusRequest(int other) {
        this(Type.CHOOSE_PLAYER, other);
    }

    /**
     * Creates a new {@code BonusRequest} from the given values.
     *
     * @param type  the {@code Type} the {@code BonusRequest} is of
     * @param other {@code id} of {@code Player} to switch {@code Tile}s with.
     *              Only applicable if the {@code type} is set accordingly.
     */
    private BonusRequest(Type type, int other) {
        this.type = type;
        this.other = other;
    }

    /**
     * Reads the given value and returns a {@code BonusRequest} of the according {@link Type}.
     *
     * @param value the value to translate
     * @param state the {@link GameState} to use
     * @return a {@code BonusRequest} of the right type
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
     * Returns the binary value that belongs to the {@code BonusRequest}.
     *
     * @return value of the {@code BonusRequest}
     */
    byte toValue() {
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

    /**
     * Returns the {@code id} of the {@link Player} to switch {@link bacon.Tile}s with
     * or {@code null} if that {@code Player} is not set (i.e. when a bonus {@code Type}).
     *
     * @return {@code id} of {@code Player} to switch tiles with or {@code null}
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
     * This enum contains the types a {@code BonusRequest} can have.
     */
    public enum Type {
        NONE,
        BOMB_BONUS,
        OVERRIDE_BONUS,
        CHOOSE_PLAYER
    }

}