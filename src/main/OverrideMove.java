/**
 * A class which represents placing an override stone on a tile
 */
public class OverrideMove extends Move {

    public OverrideMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * execute a move
     */
    public void doMove(){
        //TODO
    }

    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        //TODO
        return true;
    }
}
