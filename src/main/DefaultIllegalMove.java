/**
 *  A class which represents default illegal moves; i.e. moves where
 *  - The game has ended, or
 *  - The player has already been disqualified, or
 *  - The coordinates lie outside the map, or
 *  - The coordinates lie on a hole, or
 *  - The coordinates lie on a tile already occupied by the player himself
 */
public class DefaultIllegalMove extends Move{

    public DefaultIllegalMove(int moveID, Map map, Player player, int x, int y) {
        super(moveID, map, player, x, y);
    }

    /**
     * execute a move
     */
    public void doMove(){
    }

    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        return false;
    }

}