package bacon.move;

import bacon.*;

import java.nio.ByteBuffer;

public class MoveFactory {

    /**
     * Returns an instance of a Move subclass. The subclass is selected according to the provided data.
     *
     * @param state   the game state on which the move operates
     * @param player  the player of the move
     * @param x       the x coordinate
     * @param y       the y coordinate
     * @param request a special bonus the player asks for, applicable for choice and bonus fields
     * @throws IllegalArgumentException when the illegal data is provided
     */
    public static Move createMove(GameState state, int player, int x, int y, BonusRequest request) {
        Tile tile = state.getMap().getTileAt(x, y);
        int owner = tile.getOwnerId();
        Tile.Property tileProperty = tile.getProperty();

        assert !Game.getGame().getCurrentState().getPlayerFromId(player).isDisqualified() : "Player has already been disqualified";

        assert x >= 0 && y >= 0 && x < state.getMap().width && y < state.getMap().height : "Coordinate out of bounds";

        if (state.getGamePhase() == GamePhase.PHASE_ONE) {
            if (owner == Player.NULL_PLAYER_ID && tileProperty != Tile.Property.EXPANSION)
                return new RegularMove(state, player, x, y, request);
            else return new OverrideMove(state, player, x, y);
        } else if (state.getGamePhase() == GamePhase.PHASE_TWO)
            return new BombMove(state, player, x, y);

        else if (Game.getGame().getCurrentState().getGamePhase() == GamePhase.ENDED)
            throw new IllegalArgumentException("Game has already ended");

        throw new IllegalArgumentException("Default Illegal Move");
    }

    public static Move createMove(GameState state, int player, int x, int y) {
        return createMove(state, player, x, y, null);
    }


    /**
     * Decodes a Move from the given binary data using the given game state.
     *
     * @param data  data to decode
     * @param state state the move is for
     * @return the decoded move
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
