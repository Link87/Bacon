package bacon.move;

import bacon.*;

import java.util.ArrayList;
import java.util.HashSet;

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
    public BombMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
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
        return this.bonusRequest == 0;
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

        int radius = Game.getGame().getBombRadius();
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

        // set of already examined tiles
        var bombSet = new HashSet<Tile>();
        // initializing ArrayList to examine the tiles which are i away from the tile which is bombed
        var curRadiusList = new ArrayList<Tile>();
        // initializing ArrayList to save the tiles which are i+1 away from the tile which is bombed
        var nextRadiusList = new ArrayList<Tile>();

        bombSet.add(tile);
        curRadiusList.add(tile);

        //searches for all neighbours that need to be bombed out
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < curRadiusList.size(); j++) {
                for (Direction direction : Direction.values()) {
                    if(curRadiusList.get(j).getTransition(direction) != null) {
                        if (!bombSet.contains(curRadiusList.get(j).getTransition(direction))) {
                            bombSet.add(curRadiusList.get(j).getTransition(direction));
                            nextRadiusList.add(curRadiusList.get(j).getTransition(direction));
                        }
                    }
                }
            }
            curRadiusList.clear();
            for (int k=0; k<nextRadiusList.size(); k++) {
                curRadiusList.add(nextRadiusList.get(k));
            }
            nextRadiusList.clear();
        }

        //"Bomb away" tiles, i.e. turning them into holes and removing transitions
        for (Tile u: bombSet) {
            u.bombTile();
        }

        // Subtract 1 bomb from player's inventory
        this.player.receiveBomb(-1);
    }
}
