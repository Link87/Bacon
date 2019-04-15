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
    public RegularMove(int moveID, Map map, Player player, int x, int y, BonusRequest bonusRequest) {
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

        switch(this.bonusRequest){
            case OVERRIDE:
            case BOMB:
                if(tile.getProperty() != Tile.Property.BONUS) return false;
                break;
            case NONE:
                break;
            default:
                if(tile.getProperty() != Tile.Property.CHOICE) return false;
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
                if (this.bonusRequest == BonusRequest.BOMB) this.player.receiveBomb(1);
                else this.player.receiveOverrideStone(1);
                break;

            // TODO: Current approach checks every tile on the map. Increase efficiency by using TileOwnerID swap between players instead
            case INVERSION:
                int playerCount = Game.getGame().getTotalPlayerCount();
                for (int x = 0; x < map.width; x++) {
                    for (int y = 0; y < map.height; y++) {
                        Tile anyTile = map.getTileAt(x, y);
                        if (anyTile.getOwner() != 0) {
                            int oldNumber = anyTile.getOwner();
                            int newNumber = (oldNumber + 1) % playerCount;
                            anyTile.setOwner(newNumber);
                        }
                    }
                }
                break;

            case CHOICE:
                for (int x = 0; x < map.width; x++) {
                    for (int y = 0; y < map.height; y++) {
                        Tile anyTile = map.getTileAt(x, y);
                        if (anyTile.getOwner() == this.player.number)
                            anyTile.setOwner(PlayerNrFromBonusRequest(this.bonusRequest));
                        else if (anyTile.getOwner() == PlayerNrFromBonusRequest(this.bonusRequest))
                            anyTile.setOwner(this.player.number);
                    }
                }
        }

        tile.setProperty(Tile.Property.DEFAULT); // After playing our move, the tile becomes default (no bonus anymore)
    }
    /**
     * Translates the enum BonusRequest in a number
     *
     * @param playerNr number of player in the enum
     * @return the number of the player in an int
     * @throws IllegalArgumentException if input is not a playersNr
     */
    private int PlayerNrFromBonusRequest(BonusRequest playerNr){
        switch (playerNr){
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            default:
                throw new IllegalArgumentException("BonusRequest is not a player's number");
        }
    }
}

