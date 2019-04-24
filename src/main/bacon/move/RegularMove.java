package bacon.move;

import bacon.*;

import java.nio.ByteBuffer;

/**
 * A class which represents placing a stone on a tile.
 */
public class RegularMove extends BuildMove {

    private BonusRequest request;

    /**
     * Creates an instance of RegularMove via the constructor in its superclass {@link BuildMove}.
     *
     * @param state   the game state on which the move operates
     * @param player  the player of the move
     * @param x       the x coordinate
     * @param y       the y coordinate
     * @param request a special bonus the player asks for, applicable for choice and bonus fields
     */
    RegularMove(GameState state, Player player, int x, int y, BonusRequest request) {
        super(state, player, x, y);
        this.request = request;
        if (request == null)
            this.request = new BonusRequest(BonusRequest.Type.NONE);
    }


    /**
     * Checks if this move is legal.
     * We first check whether the bonus request is valid
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on (in super Class BuildMove)
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);
        switch (tile.getProperty()) {
            case BONUS:
                if (this.request == null || this.request.type != BonusRequest.Type.BOMB_BONUS && this.request.type != BonusRequest.Type.OVERRIDE_BONUS)
                    return false;
                break;
            case CHOICE:
                if (this.request == null || this.request.type != BonusRequest.Type.SWITCH_STONES ||
                        this.request.getOtherPlayer().equals(state.getMe()))
                    return false;
                break;
            default:
                if (this.request != null && this.request.type != BonusRequest.Type.NONE)
                    return false;
                break;
        }
        return super.isLegal();
    }


    /**
     * Executes this move.
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction.
     */
    public void doMove() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);

        super.doMove();

        // After overturning captured stones, we now have to consider the bonus/special effect of our tile
        switch (tile.getProperty()) {
            case BONUS:
                if (this.request.type == BonusRequest.Type.BOMB_BONUS) this.player.receiveBomb(1);
                else if (this.request.type == BonusRequest.Type.OVERRIDE_BONUS) this.player.receiveOverrideStone(1);
                break;

            // TODO: Current approach checks every tile on the map. Increase efficiency by using TileOwnerID swap between players instead
            case INVERSION:
                int playerCount = state.getTotalPlayerCount();
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwner() != null) {
                            int oldNumber = anyTile.getOwner().getPlayerNumber();
                            int newNumber = oldNumber + 1;
                            if (newNumber > playerCount) {
                                newNumber = 1;
                            }
                            anyTile.setOwner(state.getPlayerFromNumber(newNumber));
                        }
                    }
                }
                break;

            case CHOICE:
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwner().equals(this.player))
                            anyTile.setOwner(this.request.getOtherPlayer());
                        else if (anyTile.getOwner().equals(this.request.getOtherPlayer()))
                            anyTile.setOwner(this.player);
                    }
                }
        }

        tile.setProperty(Tile.Property.DEFAULT); // After playing our move, the tile becomes default (no bonus anymore)
    }

    @Override
    public byte[] encodeBinary() {
        var data = new byte[5];
        ByteBuffer.wrap(data)
                .putShort((short) xPos)
                .putShort((short) yPos)
                .put(request.toValue());

        return data;
    }
}
