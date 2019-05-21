package bacon.move;

import bacon.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildMove extends Move {

    ChangeData[] changeData;

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
        int[] searchDirections = new int[Direction.values().length];
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
     * undoes a Move
     * should only be called after the move has been "done"
     */
    public void undoMove() {

        assert changeData != null : "Move has to be done before undo!";

        for (int i = 0; i < changeData.length; i++) {
            changeData[i].tile.setOwner(changeData[i].ogPlayer);
            changeData[i].tile.setProperty(changeData[i].wasProp);

            if (changeData[i].wasProp == Tile.Property.EXPANSION) {
                Game.getGame().getCurrentState().getMap().addExpansionStone(changeData[i].tile); // adds expansion stone back to expansion stone tracker in Map
            }
        }

        changeData = null;
    }

    /**
     * Executes this move.
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction.
     */
    @Override
    public void doMove() {
        Tile originTile = state.getMap().getTileAt(this.xPos, this.yPos);

        Set<Tile> turnOver = new HashSet<>();

        for (int direction = 0; direction < Direction.values().length; direction++) {
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

                    if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION)
                        // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        break;
                    else if (this.player.equals(last.getOwner()) && path.size() == 0)
                        // If on the first step we hit our own stone, we can stop searching in this direction
                        break;
                    else if (this.player.equals(last.getOwner()) && path.size() > 0) {
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

        //save previous owner information
        changeData = new ChangeData[turnOver.size() + 1];
        int index = 0;
        for (Tile tile : turnOver) {
            boolean isExpansion = (tile.getProperty() == Tile.Property.EXPANSION);
            changeData[index] = new ChangeData(tile, tile.getOwner(), tile.getProperty());
            index++;
        }

        boolean isExpansion = (originTile.getProperty() == Tile.Property.EXPANSION);
        changeData[index] = new ChangeData(originTile, originTile.getOwner(), originTile.getProperty());

        // now actually turn all stones over
        for (Tile t : turnOver) {
            if (t.getProperty() == Tile.Property.EXPANSION) {
                Game.getGame().getCurrentState().getMap().removeExpansionStone(t); // Removes expansion stones from expansion stone tracker in Map
            }
            t.setProperty(Tile.Property.DEFAULT);
            t.setOwner(this.player);
        }

        // new stone is placed on the map
        originTile.setOwner(this.player);
    }
}
