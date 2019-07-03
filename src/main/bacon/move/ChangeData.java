package bacon.move;

import bacon.Tile;

/**
 * Changes that are made by a {@link Move#doMove()}.
 * <p>
 * These are saved to enable a later {@link Move#undoMove()} by simply
 * reverting the changes in the {@code ChangeData} instances.
 * <p>
 * A single {@code ChangeData} instance represents the changes affecting a single {@link Tile}.
 */
class ChangeData {

    final Tile tile;
    final int ogPlayerId;
    final Tile.Property wasProp;

    /**
     * Creates a new {@code ChangeData} instance from the given values.
     *
     * @param tile       the {@link Tile} that is affected
     * @param ogPlayerId the {@code id} of the former owner of the {@code Tile}
     * @param wasProp    the {@link Tile.Property} the {@code Tile} previously had
     */
    ChangeData(Tile tile, int ogPlayerId, Tile.Property wasProp) {
        this.tile = tile;
        this.ogPlayerId = ogPlayerId;
        this.wasProp = wasProp;
    }
}
