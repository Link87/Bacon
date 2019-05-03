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

    /**
     * An enum for the different types of moves.
     */
    public enum Type {
        REGULAR,
        OVERRIDE,
        BOMB,
    }

}
