/**
 *  A class which represents placing a bomb on a tile
 */
public class BombMove extends Move{

    public BombMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }


    /**
     * checks if a move is legal
     * We only need to check whether bonus request is 0 here since all potential failure modes have been intercepted in the superclass
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        if (this.bonusRequest != 0) return false;
        return true;
    }


    /**
     * execute a move
     */
    public void doMove(){
        if (this.isLegal() == false);

        else {
            int bombRadius = Game.getBombRadius();
            Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
            Tile[] tobeRemoved = new Tile[(2*bombRadius+1)*(2*bombRadius+1)];
            Tile t = tile;

            tobeRemoved[0] = tile;
            int i=1;

            for (Direction direction : Direction.values()) {
                for (int j=1; j<=bombRadius; j++) {
                    t = t.getTransition(direction);
                    tobeRemoved[i] = t;
                    i++;
                }
            }

            for (Tile u : tobeRemoved) {
                u.bombTile();
            }

            this.player.receiveBomb(-1);
        }

    }

}
