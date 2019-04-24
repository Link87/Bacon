package bacon.ai;

import bacon.*;
import bacon.move.BuildMove;
import bacon.move.Move;
import bacon.move.RegularMove;

import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * A singleton that determines an ArrayList of all possible legal move from a certain game state
 * for a certain player in any game phase
 */
public class LegalMoves {
    private static LegalMoves legalMoves = new LegalMoves();
    private Set<Tile> regularMoves = new HashSet<>();
    private Set<Tile> overrideMoves = new HashSet<>();
    private Set<Tile> bombMoves = new HashSet<>();

    private LegalMoves() {
    }

    /**
     * all legal moves possible from a certain given board state and player
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @state Game State to be examined
     * @playerNr Number of player in turn
     * @param moveType type of moves we are searching for
     * @return legal moves (including RegularMoves, OverrideMoves, BombMoves)
     * @throws IllegalArgumentException if the game has ended
     */
    public Set<Tile> legalMoves(GameState state, int playerNr, MoveType moveType){
        switch(state.getGamePhase()){
            case PHASE_ONE:
                legalBuildMoves(state, playerNr);
                if(moveType == MoveType.REGULAR){
                    return regularMoves;
                }
                else if(moveType == MoveType.OVERRIDE){
                    return overrideMoves;
                }
                break;
            case PHASE_TWO:
                if(moveType == MoveType.BOMB){
                    return bombMoves;
                }
        }
        throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
    }

    /**
     * all legal moves possible from a certain given board state and player in the first phase
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * We go outward from each of the player's stones and find all possible moves on our straight path.
     *
     * @state Game State to be examined
     * @playerNr number of player in turn
     * @return legal moves (including RegularMoves, OverrideMoves)
     */
    public void legalBuildMoves(GameState state, int playerNr){
        regularMoves.clear();
        overrideMoves.clear();
        Player player = state.getPlayerFromNumber(playerNr);

        Iterator<Tile> stoneIterator = player.getStonesIterator();
        Tile tile;

        while(stoneIterator.hasNext()){ // iterates over all of the player's stones
            tile = stoneIterator.next();

            for(Direction direction: Direction.values()) {
                int steps = 0; //counts steps from our own stone currently under consideration
                var searchDirection = direction;
                Tile last = tile;

                while (true) {
                    if (last.getTransition(searchDirection) == null)
                        // If the next tile is a hole we can stop searching in this direction
                        break;
                    else {
                        // determine new search direction, is opposite to arrival direction
                        Direction helper = searchDirection;
                        searchDirection = last.getArrivalDirection(searchDirection).opposite();
                        last = last.getTransition(helper);

                        if (last.getOwner() == player) { // we can stop searching if we find a tile occupied by the same player
                            break;
                        } else if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION) {
                            if (steps > 0) {            // checks if the move actually captures any tile
                                regularMoves.add(last);
                            }
                            break;
                        } else {
                            if (player.getOverrideStoneCount() > 0 && steps > 0) { //checks if the move actually captures any tile
                                overrideMoves.add(last);                           // and if the player is allowed to override stones
                            }
                        }
                    }
                    steps++;
                }
            }
        }
    }

    /**
     * all legal moves possible from a certain given board state and player in the second phase
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @state Game State to be examined
     * @playerNr number of player in turn
     * @return legal BombMoves
     */
    public void legalBombMoves(GameState state){
        bombMoves.clear();
        for(int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) { // Going through the whole map
                Tile tile = state.getMap().getTileAt(x, y);
                if (tile.getProperty() != Tile.Property.HOLE) { // Only if tile is not a hole, it's legal to bomb it
                    bombMoves.add(tile);
                }
            }
        }
    }

    /**
     * Enum that indicates different types of moves
     */
    enum MoveType{
        REGULAR,
        OVERRIDE,
        BOMB
    }
}
