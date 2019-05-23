package bacon.ai.heuristics;

import bacon.*;
import bacon.move.*;

import java.util.Collections;
import java.util.HashSet;
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
     * @param playerId number of the current player in turn
     * @return legal regular moves in the given board state
     */
    public static Set<RegularMove> getLegalRegularMoves(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified())
            return Collections.emptySet();

        Set<RegularMove> legalMoves = new HashSet<>();

        for (Tile tile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones

            for (int direction = 0; direction < Direction.values().length; direction++) {
                int steps = 0; //counts steps from our own stone currently under consideration
                int searchDirection = direction;
                Tile last = tile;

                while (true) {
                    if (last.getTransition(searchDirection) == null || last.getTransition(searchDirection) == tile)
                        // If the next tile is a hole (or tile we came from) we can stop searching in this direction
                        break;
                    else {
                        // determine new search direction, is opposite to arrival direction
                        int helper = searchDirection;
                        searchDirection = Direction.oppositeOf(last.getArrivalDirection(searchDirection));
                        last = last.getTransition(helper);

                        if (last.getOwnerId() == playerId) { // we can stop searching if we find a tile occupied by the same player
                            break;
                        } else if (last.getOwnerId() == Player.NULL_PLAYER_ID && last.getProperty() != Tile.Property.EXPANSION) {
                            // checks if the move actually captures any tile
                            // also handle tile property
                            if (steps > 0 && last.getProperty() == Tile.Property.CHOICE) {
                                for (int i = 1; i <= Game.getGame().getTotalPlayerCount(); i++) {
                                    legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, BonusRequest.fromValue(i, state)));
                                }
                            } else if (steps > 0 && last.getProperty() == Tile.Property.BONUS) {
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS)));
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS)));
                            } else if (steps > 0) {
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y));
                            }
                            break;
                        }
                    }

                    if (last != last.getTransition(searchDirection))
                        steps++; // increment step counter only if last isn't self-neighboring
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
     * @param playerId number of the current player in turn
     * @return legal override moves in the given board state
     */
    public static Set<OverrideMove> getLegalOverrideMoves(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified())
            return Collections.emptySet();

        Set<OverrideMove> legalMoves = new HashSet<>();
        if (state.getPlayerFromId(playerId).getOverrideStoneCount() <= 0) return legalMoves;

        for (Tile ogTile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones

            for (int ogDirection = 0; ogDirection < Direction.values().length; ogDirection++) {
                int searchDirection = ogDirection;
                Tile last = ogTile;

                while (true) {
                    Tile next = last.getTransition(searchDirection);
                    if (next == null ||
                            (next.getOwnerId() == Player.NULL_PLAYER_ID && next.getProperty() != Tile.Property.EXPANSION)) {
                        //next is hole or unowned
                        break;
                    }
                    if (next != ogTile.getTransition(ogDirection) && next != ogTile) {
                        //next is not right next to og in search direction or og
                        legalMoves.add((OverrideMove) MoveFactory.createMove(state, playerId, next.x, next.y));
                    }
                    if (next.getOwnerId() == playerId) {
                        break;
                    }
                    int oldDirection = searchDirection;
                    searchDirection = Direction.oppositeOf(last.getArrivalDirection(searchDirection));
                    last = last.getTransition(oldDirection);
                }
            }
        }

        // adds independent expansion moves to possible override moves
        state.getMap().getExpansionTiles().forEach(expansion ->
                legalMoves.add((OverrideMove) MoveFactory.createMove(state, playerId, expansion.x, expansion.y)));

        return legalMoves;
    }

    /**
     * Returns all legal moves possible from a certain given board state and player in the second phase.
     *
     * @param state    Game State to be examined
     * @param playerId number of the current player in turn
     * @return legal bomb moves in the given board state
     */
    public static Set<BombMove> getLegalBombMoves(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_TWO) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified())
            return Collections.emptySet();

        Set<BombMove> legalMoves = new HashSet<>();
        for (int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) { // Going through the whole map
                Tile tile = state.getMap().getTileAt(x, y);
                if (tile.getProperty() != Tile.Property.HOLE) { // Only if tile is not a hole, it's legal to bomb it
                    legalMoves.add((BombMove) MoveFactory.createMove(state, playerId, tile.x, tile.y));
                }
            }
        }

        return legalMoves;
    }
}
