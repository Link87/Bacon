package bacon.move;

import bacon.GameState;
import bacon.Tile;

/**
 * A class which represents placing an override stone on a tile
 */
public class OverrideMove extends BuildMove {

    /**
     * Creates an instance of RegularMove via the constructor in its superclass {@link BuildMove}.
     *
     * @param state  the game state on which the move operates
     * @param player the player of the move
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    OverrideMove(GameState state, int player, int x, int y) {
        super(state, player, x, y);
        this.type = Type.OVERRIDE;
    }

    /**
     * Checks if this move is legal.
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        if (this.state.getPlayerFromId(this.playerId).getOverrideStoneCount() == 0)
            return false; // player must have at least 1 override stone to make the move
        if (this.state.getMap().getTileAt(this.xPos, this.yPos).getProperty() == Tile.Property.EXPANSION)
            return true;
        return super.isLegal();
    }

    /**
     * Undoes this move.
     */
    @Override
    public void undoMove() {
        super.undoMove();
        this.state.getPlayerFromId(this.playerId).receiveOverrideStone(1);
    }

    /**
     * Executes this move.
     */
    public void doMove() {
        Tile tile = this.state.getMap().getTileAt(this.xPos, this.yPos);

        super.doMove();

        if (tile.getProperty() == Tile.Property.EXPANSION) {
            this.state.getMap().removeExpansionStone(tile); // removes this tile from expansion stone tracker in Map
            tile.setProperty(Tile.Property.DEFAULT);    // the tile we placed our override stone on could be an expansion field
        }

        this.state.getPlayerFromId(this.playerId).receiveOverrideStone(-1);    // Subtract 1 override stone from player's inventory
    }

}
