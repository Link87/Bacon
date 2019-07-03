package bacon.move;

import bacon.GameState;

import java.nio.ByteBuffer;

/**
 * An abstract class representing a move.
 * <p>
 * Subclasses exists for each possible type of move and override the methods accordingly.
 * <p>
 * A {@code Move} can be done and undone, as described in the <i>command</i> pattern.
 */
public abstract class Move {
    GameState state;
    int playerId;
    int xPos;
    int yPos;
    Type type;

    private double value;

    /**
     * Creates a new {@code Move} from the given values.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link bacon.Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     */
    Move(GameState state, int playerId, int x, int y) {
        this.state = state;
        this.playerId = playerId;
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * Checks if the {@code Move} is legal.
     *
     * @return {@code true} if the {@code Move} is legal, {@code false} otherwise
     */
    public abstract boolean isLegal();

    /**
     * Executes the {@code Move}.
     */
    public abstract void doMove();

    /**
     * Undoes the {@code Move}.
     *
     * Requires the {@code Move} to previously be done.
     */
    public abstract void undoMove();

    /**
     * Returns the horizontal coordinate of the {@code Move}.
     *
     * @return the horizontal coordinate of the {@code Move}
     */
    public int getX() {
        return xPos;
    }

    /**
     * Returns the vertical coordinate of the {@code Move}.
     *
     * @return the vertical coordinate of the {@code Move}
     */
    public int getY() {
        return yPos;
    }

    /**
     * Returns the {@code id} of the {@link bacon.Player} of the {@code Move}.
     *
     * @return the {@code id} of the {@code Player} of the {@code Move}
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Returns the {@link Type} of the {@code Move}.
     *
     * @return the {@code Type} of the {@code Move}
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the evaluation value of the {@code Move}.
     *
     * @return the evaluation value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the evaluation value of the {@code Move}.
     *
     * @param value the evaluation value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the {@code Move} in binary representation.
     *
     * @return byte array containing the {@code Move}s binary representation
     */
    public byte[] encodeBinary() {
        var data = new byte[5];
        ByteBuffer.wrap(data)
                .putShort((short) xPos)
                .putShort((short) yPos)
                .put((byte) 0);

        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Move other = (Move) obj;
        return this.xPos == other.xPos && this.yPos == other.yPos &&
                this.type == other.type && this.playerId == other.playerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * (this.xPos * 50 + this.yPos);
        result += prime * result + this.type.hashCode();
        result += prime * result + this.playerId;
        return result;
    }

    /**
     * An enum for the different types of the {@code Move}s.
     */
    public enum Type {
        /**
         * A variant representing a regular move on an unoccupied tile.
         */
        REGULAR,

        /**
         * A variant representing an override move on an occupied tile.
         */
        OVERRIDE,

        /**
         * A variant representing a bomb move on a tile.
         */
        BOMB,
    }

}
