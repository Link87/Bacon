public class Tile {

    // direction of neighbours: starting from north = index 0, clockwise - null represents no neighbour in that direction
    Tile neighbours[];
    //0 empty Tile, 1-8 player Stones, 10 expension Stone
    byte stoneColour;
    char tileProp;

    final int x;
    final int y;

    public Tile(byte sC, char tP, int xPos, int yPos){
        stoneColour = sC;
        tileProp = tP;

        x = xPos;
        y = yPos;
    }

    public void setStone(byte stoneColour) {
        this.stoneColour = stoneColour;
    }

    public void setTileProp(char tP) {
        this.tileProp = tP;
    }


    public void setNeighbours(Tile[] neighbours) {
        this.neighbours = neighbours;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Tile getNeighbour(int direction) { return neighbours[direction]; }

    public byte getStoneColour() { return stoneColour; }

    public char getTileProp() { return tileProp; }

}


