/**
 *  A class which represents placing a bomb on a tile
 */
public class BombMove extends Move{

    /**
     * Creates instance of BombMove via the constructor in its superclass Move
     *
     * @param moveID
     * @param map
     * @param player
     * @param x
     * @param y
     * @param bonusRequest
     */
    public BombMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }


    /**
     * checks if a move is legal
     * We only need to check whether the player has a bomb and whether bonus request is 0 here since all potential
     * failure modes have been intercepted in the superclass
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        if (player.getBombCount() == 0) return false;
        if (this.bonusRequest != 0) return false;
        return true;
    }


    /**
     * execute a move
     *
     * Does nothing if isLegal() method determines the move to be illegal
     * Otherwise uses dynamic programming to calculate all tiles that need to be bombed with bombTile() method in
     * Tile class
     * m is a 2-dimensional array of tiles, where m[1] contains all tiles (at least) 1 step away from t, m[2] contains
     * all tiles (at least) 2 steps away from t etc.
     * We start at radius 0 and work our way up to radius r. We consider every transition of every tile in the previous
     * radius-layer i-1 and check whether this entry has already appeared. If not, we stack this entry onto m[i]
     *
     */
    public void doMove(){
        if (this.isLegal() == false);

        else {
            int r = Game.getBombRadius();
            Tile t = map.getTileAt(this.xCoordinate, this.yCoordinate);
            Tile[][] m = new Tile[r+1][];

            m[0][0] = t;

            for (int i=1; i<=r; i++){
                int k=0;
                for (int j=0; j<m[i-1].length; j++) {
                    for (Direction direction : Direction.values()) {
                        boolean redundant = false;
                        for (Tile[] s : m) {
                            for (Tile v : s){
                                if (m[i-1][j].getTransition(direction) == v) redundant=true;
                            }
                        }

                        if (redundant == false) {
                            m[i][k] = m[i-1][j].getTransition(direction);
                            k++;
                        }
                    }
                }
            }


            for (Tile[] u : m) {
                for(Tile w : u) {
                    w.bombTile();
                }
            }
            //"Bomb away" tiles, i.e. turning them into holes and removing transitions

            this.player.receiveBomb(-1);
            // Subtract 1 bomb from player's inventory
        }

    }

}
