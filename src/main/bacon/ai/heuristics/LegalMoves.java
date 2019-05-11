package bacon.ai.heuristics;

import bacon.*;
import bacon.move.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Determines an ArrayList of all possible legal move from a certain game state
 * for a certain player in any game phase.
 */
public class LegalMoves {

    private LegalMoves() {}

    /**
     * Returns all legal REGULAR moves possible from a certain given board state and player in the first phase.
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @param state    Game State to be examined
     * @param playerNr number of the current player in turn
     * @return legal regular moves in the given board state
     */
    public static Set<RegularMove> getLegalRegularMoves(GameState state, int playerNr) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        Set<RegularMove> legalMoves = new HashSet<>();
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
                            break;
                        } else if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION) {
                            // checks if the move actually captures any tile
                            // also handle tile property
                            if (steps > 0 && tile.getProperty() == Tile.Property.CHOICE) {
                                for (int i = 1; i <= Game.getGame().getTotalPlayerCount(); i++) {
                                    legalMoves.add((RegularMove) MoveFactory.createMove(state, player, last.x, last.y, BonusRequest.fromValue(i, state)));
                                }
                            } else if (steps > 0 && tile.getProperty() == Tile.Property.BONUS) {
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, player, last.x, last.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS)));
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, player, last.x, last.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS)));
                            } else if (steps > 0) {
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, player, last.x, last.y));
                            }
                            break;
                        }
                    }

                    if (last != last.getTransition(searchDirection)) steps++; // increment step counter only if last isn't self-neighboring
                }
            }
        }

        return legalMoves;
    }

    /**
     * Returns all legal OVERRIDE moves possible from a certain given board state and player in the first phase.
     * CAUTION: PLAYER ARGUMENT MUST REFER TO A PLAYER OF THE GIVEN STATE
     *
     * @param state    Game State to be examined
     * @param playerNr number of the current player in turn
     * @return legal override moves in the given board state
     */
    public static Set<OverrideMove> getLegalOverrideMoves(GameState state, int playerNr) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        Set<OverrideMove> legalMoves = new HashSet<>();
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
                            if (player.getOverrideStoneCount() > 0 && steps > 0) { // checks if the move actually captures any tile
                                // and if the player is allowed to override stones
                                legalMoves.add((OverrideMove) MoveFactory.createMove(state, player, last.x, last.y));
                            }
                            break;
                        } else if (last.getOwner() == null && last.getProperty() != Tile.Property.EXPANSION) {
                            break;
                        } else {
                            if (player.getOverrideStoneCount() > 0 && steps > 0) { // checks if the move actually captures any tile
                                // and if the player is allowed to override stones; the search continues afterwards
                                legalMoves.add((OverrideMove) MoveFactory.createMove(state, player, last.x, last.y));
                            }
                        }
                    }

                    if (last != last.getTransition(searchDirection)) steps++; // increment step counter only if last isn't self-neighboring
                }
            }
        }

        // adds independent expansion moves to possible override moves
        state.getMap().getExpansionTiles().forEach(expansion ->
                legalMoves.add((OverrideMove) MoveFactory.createMove(state, player, expansion.x, expansion.y)));

        return legalMoves;
    }

    /**
     * Returns all legal moves possible from a certain given board state and player in the second phase.
     *
     * @param state Game State to be examined
     * @param playerNo number of the current player in turn
     * @return legal bomb moves in the given board state
     */
    public static Set<BombMove> getLegalBombMoves(GameState state, int playerNo) {
        if (state.getGamePhase() != GamePhase.PHASE_TWO) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        Set<BombMove> legalMoves= new HashSet<>();
        for (int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) { // Going through the whole map
                Tile tile = state.getMap().getTileAt(x, y);
                if (tile.getProperty() != Tile.Property.HOLE) { // Only if tile is not a hole, it's legal to bomb it
                     legalMoves.add((BombMove) MoveFactory.createMove(state, state.getPlayerFromNumber(playerNo), tile.x, tile.y));
                }
            }
        }

        return legalMoves;
    }
}
