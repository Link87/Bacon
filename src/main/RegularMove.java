/**
 *  A class which represents placing a stone on a tile
 */
public class RegularMove extends Move{

    int bonusRequest;

    public RegularMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y);
        this.bonusRequest = bonusRequest;
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
