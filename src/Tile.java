public class Tile {

    // direction of neighbours: starting from north = index 0, clockwise - null represents no neighbour in that direction
    Tile neighbours[];
    //0 empty Tile, 1-8 player Stones, 10 expension Stone
    byte stoneColour;
    TileProp tileProp;

    final int x;
    final int y;

    public Tile(byte sC, TileProp tP, int xPos, int yPos){
        stoneColour = sC;
        tileProp = tP;

        x = xPos;
        y = yPos;
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
}

enum TileProp{
    CHOICE,
    NONE,
    INVERSION,
    BONUS
}

