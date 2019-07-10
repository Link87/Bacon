package bacon.move;

import bacon.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link Move} that places a stone on a {@link Tile}.
 */
public class BuildMove extends Move {

    /**
     * Changes made by {@link #doMove()}. Used by {@link #undoMove()}.
     */
    ChangeData[] changeData;

    /**
     * Creates a new {@code BuildMove} from the given values.
     *
     * @param state    the {@link GameState} on which the move operates
     * @param playerId the {@code id} of the {@link Player} of the move
     * @param x        the horizontal coordinate
     * @param y        the vertical coordinate
     */
    BuildMove(GameState state, int playerId, int x, int y) {
        super(state, playerId, x, y);
    }

    /**
     * Checks if this {@code BuildMove} is legal.
     * <p>
     * The method is implemented using breadth-first search to find a {@link Tile} already occupied by the {@link Player}
     * on a straight line from the {@code Tile} we're playing on.
     *
     * @return {@code true} if the move is legal, {@code false} otherwise
     */
    @Override
    public boolean isLegal() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);

        // cannot put a stone on a hole
        if (tile.getProperty() == Tile.Property.HOLE) return false;

        // farthest reachable tile in each direction
        Tile[] surrounding = new Tile[Direction.DIRECTION_COUNT];
        // direction in which to walk for each starting direction
        int[] searchDirections = new int[Direction.DIRECTION_COUNT];
        for (int i = 0; i < surrounding.length; i++) {
            surrounding[i] = tile;
            searchDirections[i] = i;
        }

        // Going radially outward in 8 straight lines, one step for each outer for-loop cycle, beginning with the same center tile
        // surrounding keeps track of the farthest field we've gone in each direction
        for (int steps = 1; true; ) {
            // keeps track of the number of directions where we've hit a hole/blank field/origin tile and thus stop searching
            // in this direction
            int emptyOrHoleCount = 0;

            // iterating over directions
            for (int i = 0; i < surrounding.length; i++) {
                int direction = searchDirections[i];

                if (surrounding[i] != null && surrounding[i].getTransition(direction) != null && surrounding[i].getTransition(direction) != tile) { // If the next tile isn't
                    searchDirections[i] = Direction.oppositeOf(surrounding[i].getArrivalDirection(direction));        // a hole or the origin tile, update direction
                    surrounding[i] = surrounding[i].getTransition(direction);                   // increment the farthest tile in this direction.
                    if (this.playerId == surrounding[i].getOwnerId() && steps > 1)
                        return true;     // If this next tile happens to be ours AND there was someone else's stone in between (step>1), the move is legal
                    else if (this.playerId == surrounding[i].getOwnerId() && steps == 1) {          // If, on the other hand, there WASN'T someone else's stone in between, we can stop searching in this direction,
                        surrounding[i] = null;                                                  // so set this tile to null and increment emptyOrHoleCount
                        emptyOrHoleCount++;
                    } else if (surrounding[i].getOwnerId() == Player.NULL_PLAYER_ID && surrounding[i].getProperty() != Tile.Property.EXPANSION) {
                        surrounding[i] = null;  // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        emptyOrHoleCount++;
                    }
                } else emptyOrHoleCount++;     // If this next tile is a hole, we can stop searching in this direction
            }

            if (emptyOrHoleCount >= Direction.DIRECTION_COUNT)
                break;    // If we've hit a barrier in all 8 directions, we can stop searching altogether and declare the move illegal
            steps++;    // Increment radius from the center
        }

        return false;
    }

    /**
     * Executes the {@code BuildMove}.
     * <p>
     * The method uses depth-first search to find the number of stones that need to be overturned in each direction.
     * Does nothing instead, if {@link #isLegal()} method determines the move to be illegal.
     */
    @Override
    public void doMove() {
        Tile originTile = state.getMap().getTileAt(this.xPos, this.yPos);

        Set<Tile> turnOver = new HashSet<>();

        for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
            List<Tile> path = new ArrayList<>();   // path in the given direction
            Tile last = originTile;                   // last tile of the path
            int searchDirection = direction;    // the direction we're searching in


            while (true) {
                if (last.getTransition(searchDirection) == null)
                    // If the next tile is a hole we can stop searching in this direction
                    break;
                else {
                    // if not, we add it to path
                    // determine new search direction, is opposite to arrival direction
                    int oldDirection = searchDirection;
                    searchDirection = Direction.oppositeOf(last.getArrivalDirection(searchDirection));
                    last = last.getTransition(oldDirection);
                    if (last == originTile) break;

                    if (last.getOwnerId() == Player.NULL_PLAYER_ID && last.getProperty() != Tile.Property.EXPANSION)
                        // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        break;
                    else if (this.playerId == last.getOwnerId() && path.size() == 0)
                        // If on the first step we hit our own stone, we can stop searching in this direction
                        break;
                    else if (this.playerId == last.getOwnerId() && path.size() > 0) {
                        // If on other steps we hit our own stone, we get to overturn all stones on the way
                        // and then we can stop searching in this direction
                        turnOver.addAll(path);
                        break;
                    } else {
                        path.add(last);
                    }
                }
            }
        }

        // save previous owner information
        changeData = new ChangeData[turnOver.size() + 1];
        int index = 0;
        for (Tile tile : turnOver) {
            changeData[index] = new ChangeData(tile, tile.getOwnerId(), tile.getProperty());
            index++;
        }

        changeData[index] = new ChangeData(originTile, originTile.getOwnerId(), originTile.getProperty());

        // now actually turn all stones over
        for (Tile t : turnOver) {
            if (t.getProperty() == Tile.Property.EXPANSION) {
                this.state.getMap().removeExpansionStone(t); // Removes expansion stones from expansion stone tracker in Map
            }
            t.setProperty(Tile.Property.DEFAULT);
            t.setOwnerId(this.playerId);
        }

        // new stone is placed on the map
        originTile.setOwnerId(this.playerId);
    }

    /**
     * Undoes the {@code BuildMove}.
     * <p>
     * Requires the {@code BuildMove} to previously be done.
     */
    public void undoMove() {

        assert changeData != null : "Move has to be done before undo!";

        for (ChangeData datum : changeData) {
            datum.tile.setOwnerId(datum.ogPlayerId);
            datum.tile.setProperty(datum.wasProp);

            if (datum.wasProp == Tile.Property.EXPANSION) {
                // adds expansion stone back to expansion stone tracker in Map
                this.state.getMap().addExpansionStone(datum.tile);
            }
        }

        changeData = null;
    }
}
