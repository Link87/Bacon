import java.util.ArrayList;

/**
 * A class which represents placing an override stone on a tile
 */
public class OverrideMove extends Move {

    public OverrideMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }

    /**
     * checks if a move is legal
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        if (this.player.getOverrideStoneCount() == 0) return false;

        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        Tile[] surrounding = new Tile[8];
        for (int j=0; j<8; j++) {
            surrounding[j] = tile;
        }
        int steps = 1;

        while (true) {
            int i=0;
            int emptyOrHole = 0;

            for (Direction direction : Direction.values()) {

                if(surrounding[i].getTransition(direction) != null) {
                    surrounding[i] = surrounding[i].getTransition(direction);
                    if (surrounding[i].getOwner() == this.player && steps > 1) return true;
                    else if (surrounding[i].getOwner() == this.player && steps == 1) {
                        surrounding[i] = null;
                        emptyOrHole++;
                    }
                    else if (surrounding[i].getOwner() == null && surrounding[i].getProperty() != Tile.Property.EXPANSION) {
                        surrounding[i] = null;
                        emptyOrHole++;
                    }
                }

                else emptyOrHole++;
                i++;
            }

            if (emptyOrHole >= 8) break;
            steps++;
        }

        return false;
    }


    /**
     * execute a move
     */
    public void doMove(){
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        Tile.Property property = tile.getProperty();

        ArrayList<Tile> line = new ArrayList<>();
        line.set(0, tile);

        int[] turnOverLines = new int[8];
        int i=0;


        if (this.isLegal() == false);

        else {
            tile.setOwner(this.player);

            for (Direction direction : Direction.values()) {
                while (true) {
                    int steps=0;
                    if (line.get(steps).getTransition(direction) == null) break;
                    else {
                        line.set(steps+1, line.get(steps).getTransition(direction));
                        if (line.get(steps+1).getOwner() == null && line.get(steps+1).getProperty() != Tile.Property.EXPANSION) break;
                        else if (line.get(steps+1).getOwner() == this.player && steps+1 == 1) break;
                        else if (line.get(steps+1).getOwner() == this.player && steps+1 > 1) {
                            turnOverLines[i] = steps;
                            break;
                        }
                    }
                    steps++;
                }

                for (int j=1; j<=turnOverLines[i]; j++) {
                    line.get(j).setProperty(Tile.Property.DEFAULT);
                    line.get(j).setOwner(this.player);
                }

                i++;
            }

            tile.setProperty(Tile.Property.DEFAULT);
            player.receiveOverrideStone(-1);
        }
    }
}
