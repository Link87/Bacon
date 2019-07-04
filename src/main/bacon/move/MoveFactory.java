package bacon.move;

import bacon.*;

import java.nio.ByteBuffer;

/**
 * Used to create instances of {@link Move} or subclasses, if the specific type is unknown.
 */
public class MoveFactory {

    /**
     * Returns an instance of a {@link Move} subclass. The subclass is selected according to the provided data.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     * @param request  a special bonus the player asks for, applicable for choice and bonus {@link Tile}s
     * @return an instance of a {@code Move} subclass with the given data
     * @throws IllegalArgumentException when the illegal data is provided
     */
    public static Move createMove(GameState state, int playerId, int x, int y, BonusRequest request) {
        Tile tile = state.getMap().getTileAt(x, y);
        int owner = tile.getOwnerId();
        Tile.Property tileProperty = tile.getProperty();

        assert !Game.getGame().getCurrentState().getPlayerFromId(playerId).isDisqualified() : "Player has already been disqualified";

        assert x >= 0 && y >= 0 && x < state.getMap().width && y < state.getMap().height : "Coordinate out of bounds";

        if (state.getGamePhase() == GamePhase.PHASE_ONE) {
            if (owner == Player.NULL_PLAYER_ID && tileProperty != Tile.Property.EXPANSION)
                return new RegularMove(state, playerId, x, y, request);
            else return new OverrideMove(state, playerId, x, y);
        } else if (state.getGamePhase() == GamePhase.PHASE_TWO)
            return new BombMove(state, playerId, x, y);

        else if (Game.getGame().getCurrentState().getGamePhase() == GamePhase.ENDED)
            throw new IllegalArgumentException("Game has already ended");

        throw new IllegalArgumentException("Default Illegal Move");
    }

    /**
     * Returns an instance of a {@link Move} subclass. The subclass is selected according to the provided data.
     *
     * @param state  the {@link GameState} on which the move operates
     * @param player the {@link Player} of the move
     * @param x      the horizontal coordinate
     * @param y      the vertical coordinate
     * @return an instance of a {@code Move} subclass with the given data
     * @throws IllegalArgumentException when the illegal data is provided
     */
    public static Move createMove(GameState state, int player, int x, int y) {
        return createMove(state, player, x, y, null);
    }

    /**
     * Decodes a {@link Move} from the given binary data using the given {@link GameState}.
     *
     * @param data  the binary data to decode
     * @param state the {@link GameState} the move is for
     * @return the decoded {@code Move}
     */
    public static Move decodeBinary(byte[] data, GameState state) {
        var buffer = ByteBuffer.wrap(data);

        int x = buffer.getShort();
        int y = buffer.getShort();
        byte special = buffer.get();
        byte player = buffer.get();

        return MoveFactory.createMove(state, player, x, y, BonusRequest.fromValue(special, state));
    }
}
