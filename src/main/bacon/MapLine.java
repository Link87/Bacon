package bacon;

import java.util.HashSet;
import java.util.Set;

/**
 * Object type for rows, columns, diagonals and indiagonals of the map. Important for calculating stability and override stability
 * (Completely filled rows are stable, rows filled completely with player's own stones are override stable)
 */
public class MapLine {

    private Set<Tile> lineTiles;
    private int lineSize;
    private int fillLevel;
    private int playerShare;

    /**
     * Constructor for MapLine Object. Used at the beginning of the game as part of map analysis
     */
    public MapLine() {
        this.lineTiles = new HashSet<>();
        this.lineSize = 0;
        this.fillLevel = 0;
        this.playerShare = 0;
    }

    /**
     * Adds tile to MapLine Object and updates lineSize/fillLevel
     * @param tile to be added
     */
    public void addTile(Tile tile){
        if (!this.lineTiles.contains(tile)) {
            this.lineTiles.add(tile);
            this.lineSize++;
            if (tile.getOwnerId() != Player.NULL_PLAYER_ID) this.fillLevel++;
        }
    }

    /**
     * Initializes Player Share of every MapLine after we have been assigned our player number by the server
     */
    public void initializePlayerShare(){
        for (Tile t : this.lineTiles) {
            if (t.getOwnerId() != Player.NULL_PLAYER_ID) {
                if (t.getOwnerId() == Game.getGame().getCurrentState().getMe()) this.playerShare++;
            }
        }
    }


    /**
     * Updates playerShare after a move
     * Must be updated after every doMove() and undoMove()
     *
     * @param count number of new player's stones added
     */
    public void changePlayerShare(int count) {
        playerShare += count;
    }

    /**
     * Updates fillLevel after a move
     * Must be updated after every doMove() and undoMove()
     *
     * @param count number of new opponent's stones added
     */
    public void changeFillLevel(int count) {
        fillLevel += count;
    }

    public Set<Tile> getLineTiles() {
        return lineTiles;
    }

    public int getLineSize() {
        return lineSize;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public int getPlayerShare() {
        return playerShare;
    }

}
