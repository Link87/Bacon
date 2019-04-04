public class PlayingField {

    final int width = 50;
    final int higth = 50;
    Tile map[][];

    public PlayingField(){
        map = new Tile[width][higth];

        //TODO: placing Tiles inside the Field / creating Objekts

        //setting neighbours for every Tile in map
        for( Tile[] rowOrCollum :map){
            for(Tile t : rowOrCollum){
                t.setNeighbours(computeNeighbours(t.getX(),t.getY()));
            }
        }
    }

    //TODO
    public Tile[] computeNeighbours(int x, int y){
        return null;
    }
}
