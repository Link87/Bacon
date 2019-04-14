import java.util.ArrayList;

public class BuildMove extends Move {

    /**
     * Creates a new move from the given values.
     *
     * @param moveID       the ID of the move
     * @param map          the map on which the move is executed
     * @param player       the player of the move
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param bonusRequest
     */
    public BuildMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * Checks if this move is legal.
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on.
     *
     * @return true if the move is legal, false otherwise
     */
    @Override
    boolean isLegal() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

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
        for (int steps = 1; true;) {
            // keeps track of the number of directions where we've hit a hole/blank field and thus stop searching
            // in this direction
            int emptyOrHoleCount = 0;

            // iterating over directions
            for (int i = 0; i < surrounding.length; i++) {
                var direction = searchDirections[i];

                if (surrounding[i] != null && surrounding[i].getTransition(direction) != null) { // If the next tile isn't a hole,
                    searchDirections[i] = surrounding[i].getArrivalDirection(direction).opposite();        // update direction
                    surrounding[i] = surrounding[i].getTransition(direction);                   // increment the farthest tile in this direction.
                    if (surrounding[i].getOwner() == this.player && steps > 1)
                        return true;     // If this next tile happens to be ours AND there was someone else's stone in between (step>1), the move is legal
                    else if (surrounding[i].getOwner() == this.player && steps == 1) {          // If, on the other hand, there WASN'T someone else's stone in between, we can stop searching in this direction,
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
    void doMove() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

        ArrayList<Tile> line = new ArrayList<>();   // line keeps track of tiles in one direction. When we're done searching that direction line is reused for the next direction

        int[] turnOverLines = new int[8];   // turnOverLines keeps track of the number of stones that need to be overturned in each direction
        int searchDirection = 0;            // searchDirection keeps track of the direction we're searching in

        tile.setOwner(this.player);     // new stone is placed on the map

        for (Direction direction : Direction.values()) {
            line.clear();             // At the beginning of each loop cycle the tiles from the old direction are removed from line,
            line.add(tile);           // and our playing tile is re-added to the bottom of the stack
            int steps = 0;            // steps keeps track of the number of steps we've gone in this direction

            while (true) {
                if (line.get(steps).getTransition(direction) == null)
                    break;        // If the next tile is a hole we can stop searching in this direction,
                else {
                    line.add(line.get(steps).getTransition(direction));    // if not, we add it to line.
                    if (line.get(steps + 1).getOwner() == null && line.get(steps + 1).getProperty() != Tile.Property.EXPANSION)
                        break; // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                    else if (line.get(steps + 1).getOwner() == this.player && steps + 1 == 1)
                        break;    // If on the first step we hit our own stone, we can stop searching in this direction
                    else if (line.get(steps + 1).getOwner() == this.player && steps + 1 > 1) {  // If on other steps we hit our own stone,
                        turnOverLines[searchDirection] = steps;                                           // we get to overturn all stones on the way,
                        break;                                                              // and then we can stop searching in this direction
                    }
                }
                steps++;
            }

            for (int j = 1; j <= turnOverLines[searchDirection]; j++) {               // When we're done searching one direction, this is the
                line.get(j).setProperty(Tile.Property.DEFAULT);     // function that actually overturns the stones (including expansion fields)
                line.get(j).setOwner(this.player);
            }

            searchDirection++;
        }

    }
}
