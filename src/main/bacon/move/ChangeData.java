package bacon.move;

import bacon.Player;
import bacon.Tile;

class ChangeData {

    Tile tile;
    Player ogPlayer;
    Tile.Property wasProp;

    public ChangeData(Tile tile, Player ogPlayer, Tile.Property wasProp) {
        this.tile = tile;
        this.ogPlayer = ogPlayer;
        this.wasProp= wasProp;
    }
}
