package bacon.move;

import bacon.GameState;
import bacon.Tile;

/**
 * A class which represents a move placing an override stone on a {@link Tile}.
 */
public class OverrideMove extends BuildMove {

    /**
     * Creates an instance of {@code OverrideMove} via the constructor in its superclass {@link BuildMove}.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link bacon.Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     */
    OverrideMove(GameState state, int playerId, int x, int y) {
        super(state, playerId, x, y);
        this.type = Type.OVERRIDE;
    }

    /**
     * Checks if the {@code OverrideMove} is legal.
     *
     * @return {@code true} if the {@code OverrideMove} is legal, {@code false} otherwise
     */
    public boolean isLegal() {
        if (this.state.getPlayerFromId(this.playerId).getOverrideStoneCount() == 0)
            return false; // player must have at least 1 override stone to make the move
        if (this.state.getMap().getTileAt(this.xPos, this.yPos).getProperty() == Tile.Property.EXPANSION)
            return true;
        return super.isLegal();
    }

    /**
     * Executes the {@code OverrideMove}.
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

    /**
     * Undoes the {@code OverrideMove}.
     * <p>
     * Requires the {@code OverrideMove} to previously be done.
     */
    @Override
    public void undoMove() {
        super.undoMove();
        this.state.getPlayerFromId(this.playerId).receiveOverrideStone(1);
    }

}
