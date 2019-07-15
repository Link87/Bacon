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
    /**
     * the {@code id} of the former owner of the {@code Tile}
     */
    final int ogPlayerId;
    /**
     * the {@link Tile.Property} the {@code Tile} previously had
     */
    final Tile.Property wasProp;

    /**
     * Creates a new {@code ChangeData} instance from the given values.
     *
     * @param tile       the {@link Tile} that is affected
     */
    ChangeData(Tile tile) {
        this.tile = tile;
        this.ogPlayerId = tile.getOwnerId();
        this.wasProp = tile.getProperty();
    }
}
