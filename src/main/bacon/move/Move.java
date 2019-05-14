package bacon.move;

import bacon.GameState;
import bacon.Player;

import java.nio.ByteBuffer;

/**
 * An interface which defines the basic functions of each typ of move.
 */
public abstract class Move {
    GameState state;
    Player player;
    int xPos;
    int yPos;
    Type type;

    double value;

    /**
     * Creates a new move from the given values.
     *
     * @param state  the game state on which the move operates
     * @param player the player of the move
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    Move(GameState state, Player player, int x, int y) {
        this.state = state;
        this.player = player;
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * Checks if this move is legal.
     *
     * @return true if the move is legal, false otherwise
     */
    public abstract boolean isLegal();


    /**
     * Executes this move.
     */
    public abstract void doMove();

    /**
     * Returns the x coordinate of this move.
     *
     * @return the x coordinate of this move
     */
    public int getX() {
        return xPos;
    }

    /**
     * Returns the y coordinate of this move.
     *
     * @return the y coordinate of this move
     */
    public int getY() {
        return yPos;
    }

    /**
     * Returns the player of this move.
     *
     * @return the player of this move
     */
    public Player getPlayer() {
        return player;
    }

    public Type getType() {
        return type;
    }

    /**
     * Returns the evaluation value of this move.
     *
     * @return the evaluation value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the evaluation value of this move.
     *
     * @param value the evaluation value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the move in binary representation.
     *
     * @return byte array containing this moves binary representation
     */
    public byte[] encodeBinary() {
        var data = new byte[5];
        ByteBuffer.wrap(data)
                .putShort((short) xPos)
                .putShort((short) yPos)
                .put((byte) 0);

        return data;
    }

    public abstract void undoMove();

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Move other = (Move) obj;
        return this.xPos == other.xPos && this.yPos == other.yPos &&
                this.type == other.type && this.player.equals(other.player);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * (this.xPos * 50 + this.yPos);
        result += prime * result + this.type.hashCode();
        result += prime * result + this.player.hashCode();
        return result;
    }

    /**
     * An enum for the different types of moves.
     */
    public enum Type {
        REGULAR,
        OVERRIDE,
        BOMB,
    }

}
