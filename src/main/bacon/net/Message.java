package bacon.net;

/**
 * A message that is send or received from the server.
 */
public class Message {

    private final Type type;
    private final byte[] content;

    /**
     * Creates a message from the given type with the given content.
     *
     * @param type    type the Message is of
     * @param content data that is contained in the message in byte representation
     */
    public Message(Type type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Returns the Type the Message has.
     *
     * @return the type of the message
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the data contained in the message.
     *
     * @return contained data
     */
    public byte[] getBinaryContent() {
        return content;
    }

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
         * Returns the binary value that corresponds to the Type.
         * See the network specification for details.
         *
         * @return the binary value of the Type
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the Type that corresponds to the given binary value.
         * See the network specification for details.
         *
         * @param value the value to translate
         * @return the Type that fits the value
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
    }
}