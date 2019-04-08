/**
 * A map on which Reversi is played.
 */
public class Map {

    private int width;
    private int height;

    /**
     * The tiles this map consists of. This is guaranteed to be non-empty.
     */
    private Tile[][] tiles;

    /**
     * Constructs a new Map from the given Tiles.
     * Width and height are set to the x and y dimensions of the tiles array, which both are required to be positive.
     *
     * @param tiles The tiles of the new map
     */
    private Map(Tile[][] tiles) {
        this.tiles = tiles;

        if (tiles.length == 0 || tiles[0].length == 0)
            throw new IllegalArgumentException("Dimensions of tiles have to be positive");

        this.width = tiles.length;
        this.height = tiles[0].length;

    }

    /**
     * Places a stone from the given Player on the given Tile. This method checks whether the move is possible
     * and calculates the resulting Map and Tile state changes.
     *
     * @param player the {@link Player} that executes the move
     * @param x      the x coordinate of the tile the stone is placed on
     * @param y      the y coordinate of the tile the stone is placed on
     */
    public void placeStone(Player player, int x, int y) {

    }

    /**
     * Places an override stone from the given Player on the given Tile. This method checks whether the move is possible
     * and calculates the resulting Map and Tile state changes.
     *
     * @param player the {@link Player} that executes the move
     * @param x      the x coordinate of the tile the stone is placed on
     * @param y      the y coordinate of the tile the stone is placed on
     */
    public void placeOverrideStone(Player player, int x, int y) {

    }

    /**
     * Throws a bomb from the given Player on the given Tile. This method checks whether the move is possible
     * and calculates the resulting Map and Tile state changes.
     *
     * @param player the {@link Player} that executes the move
     * @param x      the x coordinate of the tile the stone is placed on
     * @param y      the y coordinate of the tile the stone is placed on
     */
    public void throwBomb(Player player, int x, int y) {

    }

    /**
     * Extends the map with the given transition.
     *
     * @param tile1      First Tile of the transition
     * @param direction1 Direction in which the transition applies on the first tile (clockwise, 0 is at the top)
     * @param tile2      Second Tile of the transition
     * @param direction2 Direction in which the transition applies on the second tile (clockwise, 0 is at the top)
     */
    private void addTransition(Tile tile1, int direction1, Tile tile2, int direction2) {
        tile1.setTransition(tile2, Direction.values()[direction1]);
        tile2.setTransition(tile1, Direction.values()[direction2]);
    }

    /**
     * Deserialize a map object from the given String lines.
     * The lines is expected to already be ASCII and must start with <code>height</code> lines with <code>width</code>
     * characters each that represent the map tiles according to the specification.
     * The map definition can be followed with a listing of transitions that also have to follow the specification.
     * Transitions have to be separated by line breaks. "\n" is the only excepted line brake.
     *
     * @param width  width of the map
     * @param height height of the map
     * @param lines  String Array that contains map and transition data split into lines
     * @return {@link #Map(Tile[][])} with tiles
     */
    public static Map readFromString(int width, int height, String[] lines) {
        Tile[][] tiles = new Tile[width][height];
        int h;

        //putting tile information into the array
        for (h = 0; h < height; h++) {
            String[] tile = lines[h].split(" ");
            for (int w = 0; w < width; w++) {
                char symbol = tile[w].charAt(0);

                if (symbol == '0') {
                    //Tile is empty
                    tiles[w][h] = new Tile(null, Tile.Property.fromChar('n'), w, h);
                } else if (symbol <= 8 && symbol > 0) {
                    //Tile has a Stone (an owner)
                    //TODO: adjust the method of deriving Player from his Playernumber
                    tiles[w][h] = new Tile(Main.playerFromNumber(symbol), Tile.Property.fromChar('n'), w, h);
                } else if (symbol != 8722) {
                    //8722=='-' but Java behaved unexpected with (symbol != '-')
                    //Tile is not a hole --> Tile has Property
                    tiles[w][h] = new Tile(null, Tile.Property.fromChar(symbol), w, h);
                } else {
                    //Tile is a hole
                    tiles[w][h] = new Tile(null, Tile.Property.fromChar('-'), w, h);
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
                        tiles[x][y].setTransition(tiles[x][y - 1], Direction.UP);
                    }
                    if (x != 0 && tiles[x - 1][y - 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x - 1][y - 1], Direction.UP_LEFT);
                    }
                    if (x != width - 1 && tiles[x + 1][y - 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x + 1][y - 1], Direction.UP_RIGHT);
                    }
                }
                if (y != height - 1) {
                    if (tiles[x][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x][y + 1], Direction.DOWN);
                    }
                    if (x != 0 && tiles[x - 1][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x - 1][y + 1], Direction.DOWN_LEFT);
                    }
                    if (x != width - 1 && tiles[x + 1][y + 1].getProperty() != Tile.Property.HOLE) {
                        tiles[x][y].setTransition(tiles[x + 1][y + 1], Direction.DOWN_RIGHT);
                    }
                }
                if (x != width - 1 && tiles[x + 1][y].getProperty() != Tile.Property.HOLE) {
                    tiles[x][y].setTransition(tiles[x + 1][y], Direction.RIGHT);
                }
                if (x != 0 && tiles[x - 1][y].getProperty() != Tile.Property.HOLE) {
                    tiles[x][y].setTransition(tiles[x - 1][y], Direction.LEFT);
                }

            }
        }

        Map map = new Map(tiles);

        //adding additional transitions from map specification
        for (int l = h; l < lines.length; l++) {
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
}

