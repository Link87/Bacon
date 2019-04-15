import java.util.ArrayList;

/**
 * A class which represents placing a bomb on a tile.
 */
public class BombMove extends Move {
    /**
     * Creates instance of BombMove via the constructor in its superclass {@link BuildMove}
     *
     * @param moveID       the ID of the move
     * @param map          the map on which the move is executed
     * @param player       the player of the move
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param bonusRequest
     */
    public BombMove(int moveID, Map map, Player player, int x, int y, BonusRequest bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * Checks if this move is legal.
     * We only need to check whether the player has a bomb and whether bonus request is 0 here since all potential
     * failure modes have been intercepted in the superclass
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        if (player.getBombCount() == 0) return false;
        return this.bonusRequest == BonusRequest.NONE;
    }


    /**
     * Executes this move.
     * <p>
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses dynamic programming to calculate all tiles that need to be bombed with bombTile() method in
     * Tile class.
     */
    public void doMove() {
        // m is an 2D ArrayList of tiles, where m.get(1) contains all tiles (at least) 1 step away from t, m.get(2) contains
        // all tiles (at least) 2 steps away from t etc.
        // We start at radius 0 and work our way up to radius r. We consider every transition of every tile in the previous
        // radius-layer i-1 and check whether this entry has already appeared. If not, we stack this entry onto m[i]

        int r = Game.getGame().getBombRadius();
        Tile t = map.getTileAt(this.xCoordinate, this.yCoordinate);

        // initializing ArrayList to save the tiles which are i away from the tile which is bombed
        var m = new ArrayList<ArrayList<Tile>>(r + 1);
        m.add(new ArrayList<>());
        m.get(0).add(t);

        //searches for all neighbours that need to be bombed out
        for (int i = 1; i <= r; i++) {
            m.add(new ArrayList<>());
            for (int j = 0; j < m.get(i - 1).size(); j++) {
                for (Direction direction : Direction.values()) {
                    boolean redundant = false;
                    for (ArrayList<Tile> s : m) { //detects whether a tiles is named in the ArrayList
                        for (Tile v : s) {
                            if (m.get(i - 1).get(j).getTransition(direction) == v) redundant = true;
                        }
                    }
                    //adding a tile in the ArrayList
                    if (m.get(i - 1).get(j).getTransition(direction) != null) {
                        if (!redundant) m.get(i).add(m.get(i - 1).get(j).getTransition(direction));
                    }

                }
            }
        }

        //"Bomb away" tiles, i.e. turning them into holes and removing transitions
        for (ArrayList<Tile> u : m) {
            for (Tile w : u) {
                w.bombTile();
            }
        }

        // Subtract 1 bomb from player's inventory
        this.player.receiveBomb(-1);
    }
}
