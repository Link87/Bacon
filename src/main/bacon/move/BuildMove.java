package bacon.move;

import bacon.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BuildMove extends Move {

    /**
     * Creates a new move from the given values.
     *
     * @param state  the game state on which the move operates
     * @param player the player of the move
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    BuildMove(GameState state, Player player, int x, int y) {
        super(state, player, x, y);
    }

    /**
     * Checks if this move is legal.
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on.
     *
     * @return true if the move is legal, false otherwise
     */
    @Override
    public boolean isLegal() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);

        // cannot put a stone on a hole
        if (tile.getProperty() == Tile.Property.HOLE) return false;

        // farthest reachable tile in each direction
        Tile[] surrounding = new Tile[Direction.values().length];
        // direction in which to walk for each starting direction
        Direction[] searchDirections = new Direction[Direction.values().length];
        for (int i = 0; i < surrounding.length; i++) {
            surrounding[i] = tile;
            searchDirections[i] = Direction.values()[i];
        }

        // Going radially outward in 8 straight lines, one step for each while loop cycle, beginning with the same center tile
        // surrounding keeps track of the farthest field we've gone in each direction
        for (int steps = 1; true; ) {
            // keeps track of the number of directions where we've hit a hole/blank field and thus stop searching
            // in this direction
            int emptyOrHoleCount = 0;

            // iterating over directions
            for (int i = 0; i < surrounding.length; i++) {
                var direction = searchDirections[i];

                if (surrounding[i] != null && surrounding[i].getTransition(direction) != null) { // If the next tile isn't a hole,
                    searchDirections[i] = surrounding[i].getArrivalDirection(direction).opposite();        // update direction
                    surrounding[i] = surrounding[i].getTransition(direction);                   // increment the farthest tile in this direction.
                    if (this.player.equals(surrounding[i].getOwner()) && steps > 1)
                        return true;     // If this next tile happens to be ours AND there was someone else's stone in between (step>1), the move is legal
                    else if (this.player.equals(surrounding[i].getOwner()) && steps == 1) {          // If, on the other hand, there WASN'T someone else's stone in between, we can stop searching in this direction,
                        surrounding[i] = null;                                                  // so set this tile to null and increment emptyOrHoleCount
                        emptyOrHoleCount++;
                    } else if (surrounding[i].getOwner() == null && surrounding[i].getProperty() != Tile.Property.EXPANSION) {
                        surrounding[i] = null;  // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        emptyOrHoleCount++;
                    }
                } else emptyOrHoleCount++;     // If this next tile is a hole, we can stop searching in this direction
            }

            if (emptyOrHoleCount >= Direction.values().length)
                break;    // If we've hit a barrier in all 8 directions, we can stop searching altogether and declare the move illegal
            steps++;    // Increment radius from the center
        }

        return false;
    }

    /**
     * Executes this move.
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction.
     */
    @Override
    public void doMove() {
        Tile tile = state.getMap().getTileAt(this.xPos, this.yPos);

        Set<Tile> turnOver = new HashSet<>();

        for (Direction direction : Direction.values()) {
            var path = new ArrayList<Tile>();   // path in the given direction
            path.add(tile);
            Tile last = tile;                   // last tile of the path
            var searchDirection = direction;    // the direction we're searching in


            while (true) {
                if (last.getTransition(searchDirection) == null)
                    // If the next tile is a hole we can stop searching in this direction
                    break;
                else {
                    // if not, we add it to path
                    // determine new search direction, is opposite to arrival direction
                    Direction oldDirection = searchDirection;
                    searchDirection = last.getArrivalDirection(searchDirection).opposite();
                    last = last.getTransition(oldDirection);
                    path.add(last);

                    if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION)
                        // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        break;
                    else if (this.player.equals(last.getOwner()) && path.size() == 1)
                        // If on the first step we hit our own stone, we can stop searching in this direction
                        break;
                    else if (this.player.equals(last.getOwner()) && path.size() > 1) {
                        // If on other steps we hit our own stone, we get to overturn all stones on the way
                        // and then we can stop searching in this direction
                        turnOver.addAll(path);
                        break;
                    }
                }
            }
        }

        // now actually turn all stones over
        turnOver.forEach(t -> t.setProperty(Tile.Property.DEFAULT));
        turnOver.forEach(t -> t.setOwner(this.player));

        // new stone is placed on the map
        tile.setOwner(this.player);
    }
}
