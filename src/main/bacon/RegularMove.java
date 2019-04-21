package bacon;

/**
 * A class which represents placing a stone on a tile.
 */
public class RegularMove extends BuildMove {

    /**
     * Creates an instance of RegularMove via the constructor in its superclass {@link BuildMove}.
     *
     * @param moveID       the ID of the move
     * @param map          the map on which the move is executed
     * @param player       the player of the move
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param bonusRequest
     */
    public RegularMove(int moveID, Map map, Player player, int x, int y, int bonusRequest) {
        super(moveID, map, player, x, y, bonusRequest);
    }


    /**
     * Checks if this move is legal.
     * We first check whether the bonus request is valid
     * We then use breadth-first search to find a tile already occupied by the player on a straight line from the tile
     * we're playing on (in super Class BuildMove)
     *
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegal() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);
        switch (tile.getProperty()) {
            case BONUS:
                if (this.bonusRequest != 20 && this.bonusRequest != 21) return false;
                break;
            case CHOICE:
                if (this.bonusRequest < 1 || this.bonusRequest > 8) return false;
                break;
            default:
                if (this.bonusRequest != 0) return false;
                break;
        }
        return super.isLegal();
    }


    /**
     * Executes this move.
     * Does nothing if isLegal() method determines the move to be illegal.
     * Otherwise uses depth-first search to find the number of stones that need to be overturned in each direction.
     */
    public void doMove() {
        Tile tile = map.getTileAt(this.xCoordinate, this.yCoordinate);

        super.doMove();

        // After overturning captured stones, we now have to consider the bonus/special effect of our tile
        switch (tile.getProperty()) {
            case BONUS:
                if (this.bonusRequest == 20) this.player.receiveBomb(1);
                else this.player.receiveOverrideStone(1);
                break;

            // TODO: Current approach checks every tile on the map. Increase efficiency by using TileOwnerID swap between players instead
            case INVERSION:
                int playerCount = Game.getGame().getTotalPlayerCount();
                for (int x = 0; x < map.width; x++) {
                    for (int y = 0; y < map.height; y++) {
                        Tile anyTile = map.getTileAt(x, y);
                        if (anyTile.getOwner() != null) {
                            int oldNumber = anyTile.getOwner().getPlayerNumber();
                            int newNumber = oldNumber+1;
                            if(newNumber > playerCount){
                                newNumber=1;
                            }
                            anyTile.setOwner(Game.getGame().getCurrentState().getPlayerFromNumber(newNumber));
                        }
                    }
                }
                break;

            case CHOICE:
                for (int x = 0; x < map.width; x++) {
                    for (int y = 0; y < map.height; y++) {
                        Tile anyTile = map.getTileAt(x, y);
                        if (anyTile.getOwner() == this.player)
                            anyTile.setOwner(Game.getGame().getCurrentState().getPlayerFromNumber(this.bonusRequest));
                        else if (anyTile.getOwner() == Game.getGame().getCurrentState().getPlayerFromNumber(this.bonusRequest))
                            anyTile.setOwner(this.player);
                    }
                }
        }

        tile.setProperty(Tile.Property.DEFAULT); // After playing our move, the tile becomes default (no bonus anymore)
    }

}
