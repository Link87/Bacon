package bacon.move;

import bacon.Direction;
import bacon.GameState;
import bacon.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class which represents a move placing a bomb on a {@link Tile}.
 */
public class BombMove extends Move {

    /**
     * Creates a new instance of {@code BombMove} from the given values.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link bacon.Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     */
    public BombMove(GameState state, int playerId, int x, int y) {
        super(state, playerId, x, y);
        this.type = Type.BOMB;
    }

    /**
     * Returns the {@link Tile}s that are affected by a bomb thrown onto the given tile.
     *
     * @param target the {@code Tile} whose surroundings is to be examined
     * @return the {@link Set} of {@code Tile}s within bomb radius of the {@code Tile}
     */
    public static Set<Tile> getAffectedTiles(Tile target, int radius) {
        // set of already examined tiles
        Set<Tile> bombSet = new HashSet<>();
        // initializing ArrayList to examine the tiles which are i away from the tile which is bombed
        List<Tile> currentTiles = new ArrayList<>();
        // initializing ArrayList to save the tiles which are i+1 away from the tile which is bombed
        List<Tile> nextTiles = new ArrayList<>();

        bombSet.add(target);
        currentTiles.add(target);

        // Searches for all neighbours that need to be bombed out.
        // Starts at radius 0 and works its way up to the bomb radius. Considers every transition of every tile in the previous
        // radius-layer i-1 and checks whether this entry has already appeared. If not, stacks this entry onto m[i]
        for (int i = 0; i < radius; i++) {
            for (Tile t : currentTiles) {
                for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
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

        return bombSet;
    }

    /**
     * Checks if this {@code BombMove} is legal.
     * <p>
     * Returns {@code false} if destination {@link Tile} is a hole
     * or the {@link bacon.Player} has not enough bombs, otherwise {@code true}.
     *
     * @return {@code true} if the move is legal, {@code false} otherwise
     */
    public boolean isLegal() {
        if (this.state.getMap().getTileAt(this.xPos, this.yPos).getProperty() == Tile.Property.HOLE) return false;
        return this.state.getPlayerFromId(this.playerId).getBombCount() != 0;
    }

    /**
     * Executes this {@code BombMove}.
     * <p>
     * This method calculates all {@link Tile}s that need to be bombed with {@link Tile#bombTile()}.
     * Does nothing instead, if {@link #isLegal()} method determines the move to be illegal.
     */
    public void doMove() {
        int radius = state.getBombRadius();
        Tile target = state.getMap().getTileAt(this.xPos, this.yPos);

        Set<Tile> bombSet = target.getBombEffect();

        //"Bomb away" tiles, i.e. turning them into holes and removing transitions
        bombSet.forEach(Tile::bombTile);

        // Subtract 1 bomb from player's inventory
        this.state.getPlayerFromId(this.playerId).receiveBomb(-1);
    }

    /**
     * {@code BombMove}s cannot be undone. This method throws an {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void undoMove() {
        throw new UnsupportedOperationException();
    }
}
