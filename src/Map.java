/**
 * A map on which Reversi is played.
 */
public class Map {

    private final int MAX_WIDTH = 50;
    private final int MAX_HEIGHT = 50;

    private int width;
    private int height;

    /**
     * The tiles this map consists of. This is guaranteed to be non-empty.
     */
    private Tile[][] tiles;

    /**
     * Constructs a new Map from the given Tiles.
     * Width and height are set to the x and y dimensions of the tiles array, which both are required to be positive.
     * @param tiles The tiles of the new map
     */
    private Map(Tile[][] tiles){
        this.tiles = tiles;

        if(tiles.length == 0 || tiles[0].length == 0)
            throw new IllegalArgumentException("Dimensions of tiles have to be positive");

        this.width = tiles.length;
        this.height = tiles[0].length;

    }

    /**
     * Extends the map with the given transition.
     * @param tile1         First Tile of the transition
     * @param direction1    Direction in which the transition applies on the first tile (clockwise, 0 is at the top)
     * @param tile2         Second Tile of the transition
     * @param direction2    Direction in which the transition applies on the second tile (clockwise, 0 is at the top)
     */
    private void addTransition(Tile tile1, int direction1, Tile tile2, int direction2) {
        // TODO
    }

    /**
     * Deserialize a map object from the given String.
     * The string is expected to already be ASCI and must start with <code>height</code> lines with <code>width</code>
     * characters each that represent the map tiles according to the specification.
     * The map definition can be followed with a listing of transitions that also have to follow the specification.
     * Transitions have to be separated by line breaks.
     *
     * @param width width of the map
     * @param height height of the map
     * @param data String that contains map and transition data
     * @return {@link #Map(Tile[][])} with tiles
     */
    public static Map readFromString(int width, int height, String data) {
        //data ist first broken into lines, each line is than broken into caracters
        Tile[][] tempTiles = new Tile[width][height];
        String[] lines = data.split("\n");

        int h;
        for (h=0;h<height;h++){

            String[] tile = lines[h].split(" ");
            for (int w=0;w<width;w++){
                char symbol = tile[w].charAt(0);

                if (symbol <=10 && symbol >=0){
                    tempTiles[w][h] = new Tile((byte)symbol,'n',w,h);
                }else if(symbol != '-'){
                    tempTiles[w][h] = new Tile((byte)0,symbol,w,h);
                }else{
                    tempTiles[w][h]= new Tile((byte)0,'-',w,h);
                }
            }
        }

        //setting neighbours for every Tile in map
        /*for( Tile[] rowOrCollum : tiles){
            for(Tile t : rowOrCollum){
                t.setNeighbours(computeNeighbours(t.getX(),t.getY()));
            }
        }*/

        return new Map(tempTiles);
    }

    public String toString(){
        String helper ="";
        for(int y= 0;y<height;y++){
            for (int x=0;x<width;x++){
                if(tiles[x][y].getTileProp()=='-') {
                    helper = helper.concat("-");
                }else if (tiles[x][y].getTileProp() == 'n'){
                    helper = helper.concat(tiles[x][y].getStoneColour()+"");
                }else {
                    helper = helper.concat(tiles[x][y].getTileProp()+"");
                }
            }
            helper = helper.concat("\n");
        }
        return helper;
    }

}

