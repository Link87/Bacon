package bacon.move;

import bacon.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A class which represents placing a bomb on a tile.
 */
public class BombMove extends Move {

    /**
     * Creates instance of BombMove via the constructor in its superclass {@link BuildMove}
     *
     * @param state  the game state on which the move operates
     * @param player the player of the move
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    public BombMove(GameState state, Player player, int x, int y) {
        super(state, player, x, y);
        this.type = Type.BOMB;
    }


    /**
     * Checks if this move is legal.
     * Returns false if destination tile is a hole or the player has not enough bombs, otherwise true.
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        if (state.getMap().getTileAt(xPos, yPos).getProperty() == Tile.Property.HOLE) return false;
        return player.getBombCount() != 0;
    }


    /**
     * Executes this move.
     * <p>
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses dynamic programming to calculate all tiles that need to be bombed with bombTile() method in
     * Tile class.
     */
    public void doMove() {
        // m is an 2D ArrayList of tiles, where m.get(1) contains all tiles (at least) 1 step away from t, m.get(2) contains
        // all tiles (at least) 2 steps away from t etc.
        // We start at radius 0 and work our way up to radius r. We consider every transition of every tile in the previous
        // radius-layer i-1 and check whether this entry has already appeared. If not, we stack this entry onto m[i]

        int radius = state.getBombRadius();
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);

        // set of already examined tiles
        Set<Tile> bombSet = new HashSet<>();
        // initializing ArrayList to examine the tiles which are i away from the tile which is bombed
        var currentTiles = new ArrayList<Tile>();
        // initializing ArrayList to save the tiles which are i+1 away from the tile which is bombed
        var nextTiles = new ArrayList<Tile>();

        bombSet.add(tile);
        currentTiles.add(tile);

        //searches for all neighbours that need to be bombed out
        for (int i = 0; i < radius; i++) {
            for (Tile t : currentTiles) {
                for (int direction = 0; direction < Direction.values().length; direction++) {
                    if (t.getTransition(direction) != null) {
                        if (!bombSet.contains(t.getTransition(direction))) {
                            bombSet.add(t.getTransition(direction));
                            nextTiles.add(t.getTransition(direction));
                        }
                    }
                }
            }
            currentTiles = nextTiles;
            nextTiles = new ArrayList<>((i + 1) * 8);
        }

        //"Bomb away" tiles, i.e. turning them into holes and removing transitions
        bombSet.forEach(Tile::bombTile);

        // Subtract 1 bomb from player's inventory
        this.player.receiveBomb(-1);
    }

    @Override
    public void undoMove() {

    }
}
