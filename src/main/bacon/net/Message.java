package bacon.net;

/**
 * A message that is send or received from the server.
 */
public class Message {

    private final Type type;
    private final byte[] content;

    /**
     * Creates a {@code Message} from the given type with the given content.
     *
     * @param type    type the {@code Message} is of
     * @param content data that is contained in the message in byte representation
     */
    public Message(Type type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Returns the {@code Type} the {@code Message} has.
     *
     * @return the type of the message
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the data contained in the {@code Message}.
     *
     * @return contained data
     */
    public byte[] getBinaryContent() {
        return content;
    }

    /**
     * The types a message can have.
     * <p>
     * The variants of this enum each represent a specific message type.
     * The fact that these types can only either be send or received is ignored here.
     * Each variant can be uniquely identified by its integer value,
     * which is also used in the binary message representation.
     */
    public enum Type {
        GROUP_NUMBER(1),
        MAP_CONTENT(2),
        PLAYER_NUMBER(3),
        MOVE_REQUEST(4),
        MOVE_RESPONSE(5),
        MOVE_ANNOUNCE(6),
        DISQUALIFICATION(7),
        FIRST_PHASE_END(8),
        GAME_END(9);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        /**
         * Returns the {@code Type} that corresponds to the given binary value.
         * See the network specification for details.
         *
         * @param value the value to translate
         * @return the {@code Type} that fits the value
         */
        public static Type fromValue(int value) {
            switch (value) {
                case 1:
                    return GROUP_NUMBER;
                case 2:
                    return MAP_CONTENT;
                case 3:
                    return PLAYER_NUMBER;
                case 4:
                    return MOVE_REQUEST;
                case 5:
                    return MOVE_RESPONSE;
                case 6:
                    return MOVE_ANNOUNCE;
                case 7:
                    return DISQUALIFICATION;
                case 8:
                    return FIRST_PHASE_END;
                case 9:
                    return GAME_END;
                default:
                    return null;
            }
        }

        /**
         * Returns the binary value that corresponds to the {@code Type}.
         * See the network specification for details.
         *
         * @return the binary value of the {@code Type}
         */
        public int getValue() {
            return value;
        }
    }
}