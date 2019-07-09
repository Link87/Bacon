package bacon;

import bacon.move.BombMove;

import java.util.*;

/**
 * A map on which Reversi is played.
 */
public class Map {

    public final int width;
    public final int height;

    /**
     * Amount of {@link Tile}s occupied by any {@link Player}.
     */
    private int occupiedTiles;
    /**
     * Amount of {@link Tile}s not being a hole, i.e. {@code Tile}s that can be occupied by a {@link Player}.
     */
    private int totalTiles;

    /**
     * The {@link Tile}s that have an expansion stone on them
     */
    private static Set<Tile> expansionTiles;

    /**
     * The {@link Tile}s this map consists of. This is guaranteed to be non-empty.
     */
    private final Tile[][] tiles;

    /**
     * Precomputed lookup table that contains, for each tile on the {@code Map},
     * the {@link Tile}s which are within its bomb radius.
     */
    private BombGeometry bombGeometry;

    /**
     * Keeps track of all the {@link TileLine} in the {@code Map}.
     */
    private LineGeometry lineGeometry;


    /**
     * Constructs a new {@code Map} from the given {@link Tile}s.
     * {@code width} and {@code height} are set to the x and y dimensions of the {@code tiles} array,
     * both being required to be positive.
     *
     * @param tiles          an array containing the {@code Tile}s of the new {@code Map}
     * @param initOccupied   the initial count of occupied {@code Tile}s
     * @param totalTiles     the initial count of non-hole {@code Tile}s
     * @param expansionTiles a set containing those tiles, that have an expansion stone on them.
     *                       This is required and asserted to be a subset of {@code tiles}.
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
     * Makes a semi-deep copy of the {@code Map}.
     * <p>
     * {@code Tile}s are copied and transitions point to {@code Tile}s inside the {@code Map} instance.
     * The owner pointer (of {@code Tile}) however still points to the original {@code Player} objects.
     *
     * @return a one level deep copy of the {@code Map}
     */
    Map semiDeepCopy() {
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

        // setting tile transition pointers to point to tiles in copyTiles
        return new Map(copyTiles, this.occupiedTiles, this.totalTiles, Map.expansionTiles);
    }

    /**
     * Extends the {@code Map} with the given transition.
     *
     * @param tile1      First {@code Map} of the transition
     * @param direction1 {@code Direction} in integer representation in which the transition applies on the first tile
     * @param tile2      Second {@code Map} of the transition
     * @param direction2 {@code Direction} in integer representation in which the transition applies on the second tile
     */
    private void addTransition(Tile tile1, int direction1, Tile tile2, int direction2) {
        tile1.setTransition(tile2, direction1, direction2);
        tile2.setTransition(tile1, direction2, direction1);
    }

    /**
     * Deserialize a {@code Map} object from the given {@code String} lines.
     * The lines are expected to already be ASCII and must start with {@code height} lines with {@code width}
     * characters each that represent the {@code Map} tiles according to the specification.
     * The {@code Map} definition can be followed with a listing of transitions that also have to follow the specification.
     * Transitions have to be in separated lines.
     * <p>
     * The {@link LineGeometry} and {@link BombGeometry} are calculated.
     *
     * @param width  width of the {@code Map}
     * @param height height of the {@code Map}
     * @param lines  {@code String} array that contains map and transition data split into lines
     * @return a new {@code Map} instance containing new {@code Tile} instances
     */
    static Map readFromString(final int width, final int height, String[] lines) {
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
                    // Tile is empty
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.DEFAULT, w, h);
                } else if (symbol <= '8' && symbol > '0') {
                    // Tile has a Stone (an owner)
                    int playerId = Character.getNumericValue(symbol);
                    tiles[w][h] = new Tile(playerId, Tile.Property.DEFAULT, w, h);
                    Game.getGame().getCurrentState().getPlayerFromId(playerId).addStone(tiles[w][h]);
                    occupiedCount++;
                } else if (symbol != '-') {
                    // Tile is not a hole --> Tile has Property
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.fromChar(symbol), w, h);

                    if (symbol == 'x') {
                        occupiedCount++;
                        expansionTiles.add(tiles[w][h]);
                    }
                } else {
                    // Tile is a hole
                    tiles[w][h] = new Tile(Player.NULL_PLAYER_ID, Tile.Property.HOLE, w, h);
                    totalCount--;
                }
            }
        }

        // setting ordinary transitions (neighbours) while avoiding ArrayIndexOutOfBounds
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

        // adding additional transitions from map specification
        for (int l = height; l < lines.length; l++) {
            String[] elements = lines[l].split(" ");
            map.addTransition(tiles[Integer.parseInt(elements[0])][Integer.parseInt(elements[1])],
                    Integer.parseInt(elements[2]),
                    tiles[Integer.parseInt(elements[4])][Integer.parseInt(elements[5])],
                    Integer.parseInt(elements[6])
            );
        }

        // compute the static bomb geometry
        map.bombGeometry = map.new BombGeometry();

        // compute the static line geometry
        map.lineGeometry = map.new LineGeometry();

        return map;
    }

    /**
     * Updates player share of {@link TileLine}s in the {@link LineGeometry}.
     * <p>
     * Call this method after the player {@code id} has been assigned.
     */
    void assignLineGeometryPlayers() {
        lineGeometry.assignTileLinePlayers();
    }

    /**
     * Returns a new {@code String} representing the {@code Map}.
     *
     * @return a {@code String} representation of the this {@code Map}
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

    /**
     * Returns the total amount of {@code Tile}s that are no holes.
     *
     * @return number of non-hole {@code Tile}s.
     */
    public int getTotalTileCount() {
        return totalTiles;
    }

    /**
     * Returns the amount of occupied {@code Tile}s.
     *
     * @return number of occupied {@code Tile}s.
     */
    public int getOccupiedTileCount() {
        return occupiedTiles;
    }

    /**
     * Changes the amount of stones that are occupied by any {@code Player}.
     * Provide a negative number to remove stones from the counter.
     *
     * @param d amount of stones that are added, if value is positive, or removed, if value is negative
     */
    public void addOccupiedTiles(int d) {
        this.occupiedTiles += d;
    }

    /**
     * Adds an expansion stone onto the given {@code Tile}.
     * <p>
     * Use this to undo certain moves.
     *
     * @param tile the {@code Tile} that the expansion stone should be placed on
     */
    public void addExpansionStone(Tile tile) {
        expansionTiles.add(tile);
    }

    /**
     * Removes an expansion stone from the given {@code Tile}.
     *
     * @param tile the {@code Tile} that previously had an expansion stone
     */
    public void removeExpansionStone(Tile tile) {
        expansionTiles.remove(tile);
    }

    /**
     * Returns current expansion tiles on the {@code Map}.
     *
     * @return a set containing the current expansion tiles on the {@code Map}
     */
    public Set<Tile> getExpansionTiles() {
        return expansionTiles;
    }

    /**
     * Returns the {@link Tile}s that are affected by a bomb thrown onto the given tile.
     * <p>
     * In contrast to {@link BombMove#getAffectedTiles(Tile, int)}, this method uses precomputed lookup tables
     * to return a set of tiles. Using this method is generally faster than computing the set of affected tiles again.
     *
     * @param target the {@code Tile} whose surroundings is to be examined
     * @return the {@link Set} of {@code Tile}s within bomb radius of the {@code Tile}
     */
    Set<Tile> getAffectedTiles(Tile target) {
        return bombGeometry.getAffectedTilesAt(target.x, target.y);
    }

    /**
     * Returns the {@link TileLine}s of the {@code Map}.
     *
     * @return a {@link List} of {@code TileLine}s of the {@code Map}
     */
    List<TileLine> getTileLines() {
        return lineGeometry.getTileLines();
    }

    /**
     * Contains lookup tables for the {@link Tile}s affected by a {@link BombMove}.
     */
    private class BombGeometry {

        /**
         * Contains the {@link Tile}s that are affected by a {@link BombMove},
         * for each of the {@code Tile}s on the {@link Map}.
         * <p>
         * The two-dimensional list index refers to the target coordinates.
         */
        List<List<Set<Tile>>> affectedTiles;

        /**
         * Creates a new {@code BombGeometry} instance.
         *
         * Computes the static map bomb geometry. The affected areas are not computed, if the map is too large.
         */
        private BombGeometry() {
            this.affectedTiles = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                List<Set<Tile>> column = new ArrayList<>();
                for (int y = 0; y < height; y++) {
                    if (width * height * Math.pow(2 * Game.getGame().getBombRadius() + 1, 2) > 100000) {
                        column.add(new HashSet<>());
                    } else {
                        column.add(y, BombMove.getAffectedTiles(Map.this.getTileAt(x, y), Game.getGame().getBombRadius()));
                    }
                }
                this.affectedTiles.add(x, column);
            }
        }

        /**
         * Returns the {@link Tile}s that are affected by a bomb thrown onto the given coordinates.
         *
         * @param x the horizontal coordinate
         * @param y the vertical coordinate
         * @return a {@link Set} of {@code Tile}s that are affected
         */
        private Set<Tile> getAffectedTilesAt(int x, int y) {
            return this.affectedTiles.get(x).get(y);
        }

    }

    /**
     * Contains the {@link TileLine}s of the {@link Map}.
     */
    private class LineGeometry {

        /**
         * The {@link TileLine}s of the {@link Map}.
         */
        List<TileLine> tileLines;

        /**
         * Creates a new {@code LineGeometry} instance. Calculates all {@link TileLine}s of the {@link Map}.
         */
        private LineGeometry() {
            // The following section determines the map line geometry (stateless)
            tileLines = new ArrayList<>();

            for (int x = 0; x < Map.this.width; x++) {
                for (int y = 0; y < Map.this.height; y++) {
                    Tile originTile = Map.this.getTileAt(x, y);

                    if (originTile.getProperty() == Tile.Property.HOLE) {
                        continue;
                    }

                    for (int lineDirection = 0; lineDirection < Direction.values().length; lineDirection++) {
                        TileLine tileLine;
                        if (lineDirection == Direction.UP.id || lineDirection == Direction.DOWN.id) {
                            if (originTile.getColumn() != null) {
                                tileLine = originTile.getColumn();
                            } else {
                                tileLine = new TileLine();
                                tileLines.add(tileLine);
                                originTile.setColumn(tileLine);
                                tileLine.lineSearch(originTile, Direction.UP.id);
                                tileLine.lineSearch(originTile, Direction.DOWN.id);
                            }
                        } else if (lineDirection == Direction.RIGHT.id || lineDirection == Direction.LEFT.id) {
                            if (originTile.getRow() != null) {
                                tileLine = originTile.getRow();
                            } else {
                                tileLine = new TileLine();
                                tileLines.add(tileLine);
                                originTile.setRow(tileLine);
                                tileLine.lineSearch(originTile, Direction.LEFT.id);
                                tileLine.lineSearch(originTile, Direction.RIGHT.id);
                            }
                        } else if (lineDirection == Direction.UP_LEFT.id || lineDirection == Direction.DOWN_RIGHT.id) {
                            if (originTile.getIndiagonal() != null) {
                                tileLine = originTile.getIndiagonal();
                            } else {
                                tileLine = new TileLine();
                                tileLines.add(tileLine);
                                originTile.setIndiagonal(tileLine);
                                tileLine.lineSearch(originTile, Direction.UP_LEFT.id);
                                tileLine.lineSearch(originTile, Direction.DOWN_RIGHT.id);
                            }
                        } else if (lineDirection == Direction.UP_RIGHT.id || lineDirection == Direction.DOWN_LEFT.id) {
                            if (originTile.getDiagonal() != null) {
                                tileLine = originTile.getDiagonal();
                            } else {
                                tileLine = new TileLine();
                                tileLines.add(tileLine);
                                originTile.setDiagonal(tileLine);
                                tileLine.lineSearch(originTile, Direction.UP_RIGHT.id);
                                tileLine.lineSearch(originTile, Direction.DOWN_LEFT.id);
                            }
                        } else {
                            tileLine = new TileLine();
                        }

                        tileLine.lineSearch(originTile, lineDirection);
                    }
                }
            }
        }

        /**
         * Assigns the right {@link Player}s.
         */
        private void assignTileLinePlayers() {
            for (TileLine m : tileLines) {
                m.initializePlayerShare();
            }
        }

        /**
         * Returns all {@link TileLine}s of the {@link Map}.
         *
         * @return a {@link Set} of {@code TileLine}s of the {@code Map}.
         */
        private List<TileLine> getTileLines() {
            return Collections.unmodifiableList(tileLines);
        }
    }

}

