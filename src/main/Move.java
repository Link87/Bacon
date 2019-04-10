/**
 * An interface which defines the basic functions of each typ of move
 */
public interface Move {

    /**
    * execute a move
    */

    void doMove();

    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    boolean isLegal();

}
