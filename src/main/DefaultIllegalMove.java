/**
 * A class which represents default illegal moves; i.e. moves where
 * - The game has ended, or
 * - The player has already been disqualified, or
 * - The coordinates lie outside the map, or
 * - The coordinates lie on a hole, or
 * - The coordinates lie on a tile already occupied by the player himself
 */
public class DefaultIllegalMove extends Move {

    public DefaultIllegalMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * Checks if this move is legal.
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        return false;
    }


    /**
     * Executes this move. No action is taken since all moves in this class are illegal.
     */
    public void doMove() {
    }

}