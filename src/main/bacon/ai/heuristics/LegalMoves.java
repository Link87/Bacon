package bacon.ai.heuristics;

import bacon.Direction;
import bacon.GameState;
import bacon.Player;
import bacon.Tile;
import bacon.move.Move;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Determines an ArrayList of all possible legal move from a certain game state
 * for a certain player in any game phase.
 */
public class LegalMoves {

    private LegalMoves() {
    }

    /**
     * Returns all legal moves possible from a certain given board state and player.
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @param state    Game State to be examined
     * @param playerNr Number of player in turn
     * @param moveType type of moves we are searching for
     * @return legal moves (including RegularMoves, OverrideMoves, BombMoves)
     * @throws IllegalArgumentException if the game has ended or an illegal move type is provided
     */
    public static Set<Tile> getLegalMoveTiles(GameState state, int playerNr, Move.Type moveType) {
        switch (state.getGamePhase()) {
            case PHASE_ONE:
                if (moveType == Move.Type.REGULAR || moveType == Move.Type.OVERRIDE) {
                    return getLegalBuildMoves(state, playerNr, moveType);
                }
                break;
            case PHASE_TWO:
                if (moveType == Move.Type.BOMB) {
                    return getLegalBombMoves(state);
                }
        }

        throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
    }

    /**
     * Returns all legal moves possible from a certain given board state and player in the first phase.
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     * <p>
     * We go outward from each of the player's stones and find all possible moves on our straight path.
     *
     * @param state    Game State to be examined
     * @param playerNr number of player in turn
     * @return legal regular or override stones
     */
    private static Set<Tile> getLegalBuildMoves(GameState state, int playerNr, Move.Type type) {
        Set<Tile> legalTiles = new HashSet<>();

        Player player = state.getPlayerFromNumber(playerNr);

        Iterator<Tile> stoneIterator = player.getStonesIterator();

        while (stoneIterator.hasNext()) { // iterates over all of the player's stones
            Tile tile = stoneIterator.next();

            for (Direction direction : Direction.values()) {
                int steps = 0; //counts steps from our own stone currently under consideration
                var searchDirection = direction;
                Tile last = tile;

                while (true) {
                    if (last.getTransition(searchDirection) == null || last.getTransition(searchDirection) == tile)
                        // If the next tile is a hole (or tile we came from) we can stop searching in this direction
                        break;
                    else {
                        // determine new search direction, is opposite to arrival direction
                        Direction helper = searchDirection;
                        searchDirection = last.getArrivalDirection(searchDirection).opposite();
                        last = last.getTransition(helper);

                        if (last.getOwner() == player) { // we can stop searching if we find a tile occupied by the same player
                            if (player.getOverrideStoneCount() > 0 && steps > 0 && type == Move.Type.OVERRIDE) { //checks if the move actually captures any tile
                                legalTiles.add(last);                           // and if the player is allowed to override stones
                            }
                            break;
                        } else if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION) {
                            if (steps > 0 && type == Move.Type.REGULAR) {            // checks if the move actually captures any tile
                                legalTiles.add(last);
                            }
                            break;
                        } else if (last.getProperty() == Tile.Property.EXPANSION) { // in case of expansion stone no tile need to be captured and the search continues afterwards
                            if (player.getOverrideStoneCount() > 0 && type == Move.Type.OVERRIDE) {
                                legalTiles.add(last);
                            }
                        } else {
                            if (player.getOverrideStoneCount() > 0 && steps > 0 && type == Move.Type.OVERRIDE) { //checks if the move actually captures any tile
                                legalTiles.add(last);                           // and if the player is allowed to override stones; the search continues afterwards
                            }
                        }
                    }
                    steps++;
                }
            }
        }

        return legalTiles;
    }

    /**
     * Returns all legal moves possible from a certain given board state and player in the second phase.
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @param state Game State to be examined
     * @return legal bomb moves in the given board state
     */
    private static Set<Tile> getLegalBombMoves(GameState state) {
        Set<Tile> legalTiles = new HashSet<>();
        for (int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) { // Going through the whole map
                Tile tile = state.getMap().getTileAt(x, y);
                if (tile.getProperty() != Tile.Property.HOLE) { // Only if tile is not a hole, it's legal to bomb it
                    legalTiles.add(tile);
                }
            }
        }

        return legalTiles;
    }
}
