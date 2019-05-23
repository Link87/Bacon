package bacon.move;

import bacon.Tile;

class ChangeData {

    final Tile tile;
    final int ogPlayerId;
    final Tile.Property wasProp;

    ChangeData(Tile tile, int ogPlayerId, Tile.Property wasProp) {
        this.tile = tile;
        this.ogPlayerId = ogPlayerId;
        this.wasProp= wasProp;
    }
}
