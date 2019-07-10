package bacon.move;

import bacon.GameState;
import bacon.Player;
import bacon.Tile;

import java.nio.ByteBuffer;

/**
 * A class which represents a move placing a stone on a {@link Tile}.
 */
public class RegularMove extends BuildMove {

    private BonusRequest request;

    /**
     * Creates an instance of {@code RegularMove} via the constructor in its superclass {@link BuildMove}.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     * @param request  a special bonus the player asks for, applicable for choice and bonus {@link Tile}s
     */
    RegularMove(GameState state, int playerId, int x, int y, BonusRequest request) {
        super(state, playerId, x, y);
        this.type = Type.REGULAR;
        this.request = request;
        if (request == null)
            this.request = new BonusRequest(BonusRequest.Type.NONE);
    }


    /**
     * Checks if the {@code RegularMove} is legal.
     * <p>
     * This method first checks whether the bonus request is valid.
     * Further checks are done in {@link BuildMove#isLegal()}.
     *
     * @return {@code true} if the {@code RegularMove} is legal, {@code false} otherwise
     */
    public boolean isLegal() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);
        switch (tile.getProperty()) {
            case BONUS:
                if (this.request == null || this.request.type != BonusRequest.Type.BOMB_BONUS && this.request.type != BonusRequest.Type.OVERRIDE_BONUS)
                    return false;
                break;
            case CHOICE:
                if (this.request == null || this.request.type != BonusRequest.Type.CHOOSE_PLAYER)
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
     * Executes the {@code RegularMove}.
     * <p>
     * This method handles the special {@link Tile.Property}. Other computations are then done in {@link BuildMove#isLegal()}.
     */
    public void doMove() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);
        var property = tile.getProperty();

        super.doMove();

        // After overturning captured stones, we now have to consider the bonus/special effect of our tile
        switch (property) {
            case BONUS:
                if (this.request.type == BonusRequest.Type.BOMB_BONUS)
                    this.state.getPlayerFromId(this.playerId).receiveBomb(1);
                else if (this.request.type == BonusRequest.Type.OVERRIDE_BONUS)
                    this.state.getPlayerFromId(this.playerId).receiveOverrideStone(1);
                break;
            case INVERSION:
                int playerCount = state.getTotalPlayerCount();
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwnerId() != Player.NULL_PLAYER_ID) {
                            int oldNumber = anyTile.getOwnerId();
                            int newNumber = oldNumber + 1;
                            if (newNumber > playerCount) {
                                newNumber = 1;
                            }
                            anyTile.setOwnerId(newNumber);
                        }
                    }
                }
                break;

            case CHOICE:
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwnerId() == Player.NULL_PLAYER_ID) continue;
                        if (anyTile.getOwnerId() == this.playerId)
                            anyTile.setOwnerId(this.request.getOtherPlayerId());
                        else if (anyTile.getOwnerId() == this.request.getOtherPlayerId())
                            anyTile.setOwnerId(this.playerId);
                    }
                }
        }

        tile.setProperty(Tile.Property.DEFAULT); // After playing our move, the tile becomes default (no bonus anymore)
        state.getMap().addOccupiedTiles(1);
    }

    /**
     * Undoes the {@code RegularMove}.
     * <p>
     * Requires the {@code RegularMove} to previously be done.
     */
    public void undoMove() {
        //last entry in changeData is always the Tile the move was made on
        switch (changeData[changeData.length - 1].wasProp) {
            case BONUS:
                if (this.request.type == BonusRequest.Type.BOMB_BONUS)
                    this.state.getPlayerFromId(this.playerId).receiveBomb(-1);
                else if (this.request.type == BonusRequest.Type.OVERRIDE_BONUS)
                    this.state.getPlayerFromId(this.playerId).receiveOverrideStone(-1);
                break;

            // TODO: Current approach checks every tile on the map. Increase efficiency by using TileOwnerID swap between players instead
            case INVERSION:
                int playerCount = state.getTotalPlayerCount();
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwnerId() != Player.NULL_PLAYER_ID) {
                            int oldNumber = anyTile.getOwnerId();
                            int newNumber = oldNumber - 1;
                            if (newNumber < 1) {
                                newNumber = playerCount;
                            }
                            anyTile.setOwnerId(newNumber);
                        }
                    }
                }
                break;

            case CHOICE:
                for (int x = 0; x < state.getMap().width; x++) {
                    for (int y = 0; y < state.getMap().height; y++) {
                        Tile anyTile = state.getMap().getTileAt(x, y);
                        if (anyTile.getOwnerId() == Player.NULL_PLAYER_ID) continue;
                        if (anyTile.getOwnerId() == this.playerId)
                            anyTile.setOwnerId(this.request.getOtherPlayerId());
                        else if (anyTile.getOwnerId() == this.request.getOtherPlayerId())
                            anyTile.setOwnerId(this.playerId);
                    }
                }
        }

        super.undoMove();
        state.getMap().addOccupiedTiles(-1);
    }

    /**
     * Returns the {@code RegularMove} in binary representation.
     *
     * @return byte array containing the {@code Move}s binary representation
     */
    @Override
    public byte[] encodeBinary() {
        var data = new byte[5];
        ByteBuffer.wrap(data)
                .putShort((short) xPos)
                .putShort((short) yPos)
                .put(request.toValue());

        return data;
    }

    @Override
    public boolean equals(Object obj) {
        // use equals implementation in Move
        return super.equals(obj) && this.request.equals(((RegularMove) obj).request);
    }

    @Override
    public int hashCode() {
        // use hashCode implementation in Move
        return super.hashCode() + 8387 * this.request.hashCode();
    }

}
