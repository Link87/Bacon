package bacon.move;

import bacon.Map;
import bacon.Player;
import bacon.Tile;

/**
 * A class which represents placing an override stone on a tile
 */
public class OverrideMove extends BuildMove {

    public OverrideMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * Checks if this move is legal.
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        if (this.player.getOverrideStoneCount() == 0)
            return false; // player must have at least 1 override stone to make the move
        if (this.bonusRequest != 0) return false;

        return super.isLegal();
    }


    /**
     * Executes this move.
     */
    public void doMove() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

        super.doMove();

        tile.setProperty(Tile.Property.DEFAULT);    // the tile we placed our override stone on could be an expansion field
        this.player.receiveOverrideStone(-1);    // Subtract 1 override stone from player's inventory
    }

}
