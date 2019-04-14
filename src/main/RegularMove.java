/**
 *  A class which represents placing a stone on a tile
 */
public class RegularMove extends BuildMove{

    /**
     * Creates instance of RegularMove via the constructor in its superclass Move
     *
     * @param moveID
     * @param map
     * @param player
     * @param x
     * @param y
     * @param bonusRequest
     */
    public RegularMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }


    /**
     * checks if a move is legal
     * We first check whether the bonus request is valid
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on (in super Class BuildMove)
     *
     * @return whether the move is legal
     */
    public boolean isLegal(){
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        if(tile.getOwner()!=null){
            return false;
        }
        Tile.Property property = tile.getProperty();
        switch (property) {
            case BONUS:
                if (this.bonusRequest != 20 && this.bonusRequest != 21) return false;
            case CHOICE:
                if (this.bonusRequest < 1 || this.bonusRequest > 8) return false;
            default:
                if (this.bonusRequest != 0) return false;
        }
        return super.isLegal();
    }


    /**
     * execute a move
     * Does nothing if isLegal() method determines the move to be illegal
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction
     *
     */
    public void doMove(){
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        Tile.Property property = tile.getProperty();

        super.doMove();

        // After overturning captured stones, we now have to consider the bonus/special effect of our tile
        switch (property) {
            case BONUS:
                if (this.bonusRequest == 20) this.player.receiveBomb(1);
                else this.player.receiveOverrideStone(1);
            break;
                //TODO: Optimize Inversion and Choice fields, they currently check every field on the map and assign each a new owner according to the rules
            case INVERSION:
                int n = Game.getGame().getTotalPlayerNumber();
                    for (int a=0; a<map.width; a++) {
                        for (int b=0; b<map.height; b++) {
                            Tile anyTile = map.getTileAt(a, b);
                            if(anyTile.getOwner() != null) {
                                int oldNumber = anyTile.getOwner().getPlayerNumber();
                                int newNumber = (oldNumber + 1) % n;
                                anyTile.setOwner(Game.getGame().playerFromNumber(newNumber));
                            }
                        }
                    }
                    break;

            case CHOICE:
                for (int a=0; a<map.width; a++) {
                    for (int b=0; b<map.height; b++) {
                        Tile anyTile = map.getTileAt(a, b);
                        if (anyTile.getOwner() == this.player) anyTile.setOwner(Game.getGame().playerFromNumber(this.bonusRequest));
                        else if (anyTile.getOwner() == Game.getGame().playerFromNumber(this.bonusRequest)) anyTile.setOwner(this.player);
                    }
                }
        }

        tile.setProperty(Tile.Property.DEFAULT); // After playing our move, the tile becomes default (no bonus anymore)
    }

}
