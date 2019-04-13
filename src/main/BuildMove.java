import java.util.ArrayList;

public class BuildMove extends Move {
    /**
     * Constructor of a move
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
     * checks if a move is legal
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on
     *
     * @return whether the move is legal
     */
    @Override
    boolean isLegal() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

        Tile[] surrounding = new Tile[8];
        for (int j=0; j<8; j++) {
            surrounding[j] = tile;
        }
        int steps = 1;

        // Going radially outward in 8 straight lines, one step for each while loop cycle, beginning with the same center tile
        // surrounding keeps track of the farthest field we've gone in each direction

        while (true) {
            int i=0;
            int emptyOrHole = 0;
            // i keeps track of the directions we're going through in each for loop cycle
            // emptyOrHole keeps track of the number of directions where we've hit a hole/blank field and thus stop searching
            // in this direction

            for (Direction direction : Direction.values()) {

                if(surrounding[i].getTransition(direction) != null) {                           // If the next tile isn't a hole,
                    surrounding[i] = surrounding[i].getTransition(direction);                   // increment the farthest tile in this direction.
                    if (surrounding[i].getOwner() == this.player && steps > 1) return true;     // If this next tile happens to be ours AND there was someone else's stone in between (step>1), the move is legal
                    else if (surrounding[i].getOwner() == this.player && steps == 1) {          // If, on the other hand, there WASN'T someone else's stone in between, we can stop searching in this direction,
                        surrounding[i] = null;                                                  // so set this tile to null and increment emptyOrHole
                        emptyOrHole++;
                    }
                    else if (surrounding[i].getOwner() == null && surrounding[i].getProperty() != Tile.Property.EXPANSION) {
                        surrounding[i] = null;  // If this next tile is unoccupied AND not an expansion field (i.e. empty), we can stop searching in this direction
                        emptyOrHole++;
                    }
                }

                else emptyOrHole++;     // If this next tile is a hole, we can stop searching in this direction
                i++;
            }

            if (emptyOrHole >= 8) break;    // If we've hit a barrier in all 8 directions, we can stop searching altogether and declare the move illegal
            steps++;    // Increment radius from the center
        }

        return false;
    }

    /**
     * execute a move
     * Does nothing if isLegal() method determines the move to be illegal
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction
     *
     */
    @Override
    void doMove() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        Tile.Property property = tile.getProperty();

        ArrayList<Tile> line = new ArrayList<>();   // line keeps track of tiles in one direction. When we're done searching
        line.set(0, tile);                          // that direction line is reused for the next direction

        int[] turnOverLines = new int[8];   // turnOverLines keeps track of the number of stones that need to be overturned in each direction
        int searchDirection = 0;                            // i keeps track of the direction we're searching in


        if (!this.isLegal()) ;

        else {
            tile.setOwner(this.player);     // new stone is placed on the map

            for (Direction direction : Direction.values()) {
                int steps = 0;            // steps keeps track of the number of steps we've gone in this direction
                while (true) {
                    if (line.get(steps).getTransition(direction) == null)
                        break;        // If the next tile is a hole we can stop searching in this direction,
                    else {
                        line.set(steps + 1, line.get(steps).getTransition(direction));    // if not, we add it to line.
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
}
