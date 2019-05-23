package bacon;

import java.util.HashSet;
import java.util.Set;

/**
 * A map on which Reversi is played.
 */
public class Map {

    public final int width;
    public final int height;

    private int occupiedTiles;
    private int totalTiles;

    /**
     * The tiles that have expansion stone on them
     */
    private static Set<Tile> expansionTiles;

    /**
     * The tiles this map consists of. This is guaranteed to be non-empty.
     */
    private final Tile[][] tiles;

    /**
     * Constructs a new Map from the given Tiles.
     * Width and height are set to the x and y dimensions of the tiles array, which both are required to be positive.
     *
     * @param tiles The tiles of the new map
     */
    private Map(Tile[][] tiles, int initOccupied, int totalTiles, Set<Tile> expansionTiles) {
        this.tiles = tiles;

        assert tiles.length > 0 && tiles[0].length > 0 : "Dimensions of tiles have to be positive";

        this.width = tiles.length;
        this.height = tiles[0].length;

        this.occupiedTiles = initOccupied;
        this.totalTiles = totalTiles;

        Map.expansionTiles = expansionTiles;
    }

    /**
     * Makes a copy of the map where tiles are also copies and transitions point to tiles inside the map instance.
     * The owner pointer (of Tile) however still points to the original player objects.
     *
     * @return a one level deep copy
     */
    public Map semiDeepCopy() {
        Tile[][] copyTiles = new Tile[this.width][this.height];

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Tile original = this.getTileAt(x, y);
                copyTiles[x][y] = new Tile(original.getOwnerId(), original.getProperty(), original.x, original.y);
            }
        }

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Tile original = this.getTileAt(x, y);

                Tile currentTile = copyTiles[x][y];
                for (int direction = 0; direction < Direction.values().length; direction++) {
                    if (original.getTransition(direction) != null) {
                        int xOfTrans = original.getTransition(direction).x;
                        int yOfTrans = original.getTransition(direction).y;
                        currentTile.setTransition(copyTiles[xOfTrans][yOfTrans],
                                direction, original.getArrivalDirection(direction));
                    }
                }
            }
        }

        //setting tile transition pointers to point to tiles in copyTiles
        return new Map(copyTiles, this.occupiedTiles, this.totalTiles, Map.expansionTiles);
    }

    /**
     * Extends the map with the given transition.
     *
     * @param tile1      First Tile of the transition
     * @param direction1 Direction in which the transition applies on the first tile
     * @param tile2      Second Tile of the transition
     * @param direction2 Direction in which the transition applies on the second tile
     */
    private void addTransition(Tile tile1, int direction1, Tile tile2, int direction2) {
        tile1.setTransition(tile2, direction1, direction2);
        tile2.setTransition(tile1, direction2, direction1);
    }

    /**
     * Deserialize a map object from the given String lines.
     * The lines are expected to already be ASCII and must start with <code>height</code> lines with <code>width</code>
     * characters each that represent the map tiles according to the specification.
     * The map definition can be followed with a listing of transitions that also have to follow the specification.
     * Transitions have to be in separated lines.
     *
     * @param width  width of the map
     * @param height height of the map
     * @param lines  String Array that contains map and transition data split into lines
     * @return {@link #Map(Tile[][], int, int, Set<Tile>)} with tiles
     */
    public static Map readFromString(final int width, final int height, String[] lines) {
        Tile[][] tiles = new Tile[width][height];
        int occupiedCount = 0;
        int totalCount = 0;
        Set<Tile> expansionTiles = new HashSet<>();

        // putting tile information into the array
        for (int h = 0; h < height; h++) {
            String[] tile = lines[h].split(" ");
            for (int w = 0; w < width; w++) {
                char symbol = tile[w].charAt(0);

                totalCount++;
                if (symbol == '0') {
                    //Tile is empty
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.DEFAULT, w, h);
                } else if (symbol <= '8' && symbol > '0') {
                    //Tile has a Stone (an owner)
                    int playerId = Character.getNumericValue(symbol);
                    tiles[w][h] = new Tile(playerId, Tile.Property.DEFAULT, w, h);
                    Game.getGame().getCurrentState().getPlayerFromId(playerId).addStone(tiles[w][h]);
                    occupiedCount++;
                } else if (symbol != '-') {
                    //Tile is not a hole --> Tile has Property
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.fromChar(symbol), w, h);

                    if (symbol == 'x') {
                        occupiedCount++;
                        expansionTiles.add(tiles[w][h]);
                    }
                } else {
                    //Tile is a hole
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.HOLE, w, h);
                    totalCount--;
                }
            }
        }

        //setting ordinary transitions (neighbours) while avoiding ArrayIndexOutOfBounds
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y].getProperty() == Tile.Property.HOLE) {
                    continue;
                }
                if (y != 0) {
                    if (tiles[x][y - 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x][y - 1], Direction.UP.id, Direction.UP.opposite().id);
                    }
                    if (x != 0 && tiles[x - 1][y - 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x - 1][y - 1], Direction.UP_LEFT.id, Direction.UP_LEFT.opposite().id);
                    }
                    if (x != width - 1 && tiles[x + 1][y - 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x + 1][y - 1], Direction.UP_RIGHT.id, Direction.UP_RIGHT.opposite().id);
                    }
                }
                if (y != height - 1) {
                    if (tiles[x][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x][y + 1], Direction.DOWN.id, Direction.DOWN.opposite().id);
                    }
                    if (x != 0 && tiles[x - 1][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x - 1][y + 1], Direction.DOWN_LEFT.id, Direction.DOWN_LEFT.opposite().id);
                    }
                    if (x != width - 1 && tiles[x + 1][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x + 1][y + 1], Direction.DOWN_RIGHT.id, Direction.DOWN_RIGHT.opposite().id);
                    }
                }
                if (x != width - 1 && tiles[x + 1][y].getProperty() != Tile.Property.HOLE) {
                    tiles[x][y].setTransition(tiles[x + 1][y], Direction.RIGHT.id, Direction.RIGHT.opposite().id);
                }
                if (x != 0 && tiles[x - 1][y].getProperty() != Tile.Property.HOLE) {
                    tiles[x][y].setTransition(tiles[x - 1][y], Direction.LEFT.id, Direction.LEFT.opposite().id);
                }

            }
        }

        Map map = new Map(tiles, occupiedCount, totalCount, expansionTiles);

        //adding additional transitions from map specification
        for (int l = height; l < lines.length; l++) {
            String[] elements = lines[l].split(" ");
            map.addTransition(tiles[Integer.parseInt(elements[0])][Integer.parseInt(elements[1])],
                    Integer.parseInt(elements[2]),
                    tiles[Integer.parseInt(elements[4])][Integer.parseInt(elements[5])],
                    Integer.parseInt(elements[6])
            );
        }

        return map;
    }

    /**
     * Returns a new String representing the Map.
     *
     * @return a String representation of the this map
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (getTileAt(x, y).getProperty()) {
                    case DEFAULT:
                        if (getTileAt(x, y).getOwnerId() == Player.NULL_PLAYER_ID) builder.append("0 ");
                        else builder.append(getTileAt(x, y).getOwnerId()).append(" ");
                        break;
                    case HOLE:
                        builder.append("- ");
                        break;
                    case CHOICE:
                        builder.append("c ");
                        break;
                    case INVERSION:
                        builder.append("i ");
                        break;
                    case BONUS:
                        builder.append("b ");
                        break;
                    case EXPANSION:
                        builder.append("x ");
                        break;
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Returns the tile at the given position. This operation is unchecked.
     *
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     * @return Tile at given position
     * @throws ArrayIndexOutOfBoundsException when tile position is out of bounds
     */
    public Tile getTileAt(int x, int y) {
        return tiles[x][y];
    }

    public int getTotalTileCount() {
        return totalTiles;
    }

    /**
     * Gets amount of occupied tiles
     *
     * @return number of occupied tiles
     */
    public int getOccupiedTileCount() {
        return occupiedTiles;
    }

    /**
     * increase/decrease occupiedTileCount
     *
     * @param d amount to add to occupiedTileCount
     */
    public void addOccupiedTiles(int d) {
        this.occupiedTiles = this.occupiedTiles + d;
    }

    /**
     * Adds expansion stone back onto the tile in case of undo move
     *
     * @param tile that the expansion stone should be placed on
     */
    public void addExpansionStone(Tile tile) {
        expansionTiles.add(tile);
    }

    /**
     * Removes expansion stone from the tile
     *
     * @param tile that has an expansion stone
     */
    public void removeExpansionStone(Tile tile) {
        expansionTiles.remove(tile);
    }

    /**
     * Returns current expansion tiles on the map
     *
     * @return current expansion tiles on the map
     */
    public Set<Tile> getExpansionTiles() {
        return expansionTiles;
    }
}

