/**
 * An interface which defines the basic functions of each typ of move
 */
public abstract class Move {
    Player player;

    /**
    * execute a move
    */

    abstract void doMove();

    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    abstract boolean isLegal();

}
