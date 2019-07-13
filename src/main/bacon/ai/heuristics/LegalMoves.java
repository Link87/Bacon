package bacon.ai.heuristics;

import bacon.*;
import bacon.move.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * A collection of methods, that return legal moves in a given game state.
 * <p>
 * All methods are static, stateless and stand-alone. Therefore no instances of {@code LegalMoves} can be created.
 */
public class LegalMoves {

    private LegalMoves() {
    }

    /**
     * Returns all legal regular {@link Move}s possible from a certain given board state and player in the first phase.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId the {@code id} of the current {@link Player} in turn
     * @return a set of all {@link RegularMove}s being legal in the given board state
     */
    public static Set<RegularMove> getLegalRegularMoves(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        int freeTiles = state.getMap().getFreeTiles().size();
        int playerStoneCount = state.getPlayerFromId(playerId).getStoneCount();

        if (state.getPlayerFromId(playerId).isDisqualified() || freeTiles == 0)
            return Collections.emptySet();

        Set<RegularMove> legalMoves = new HashSet<>();

        if (freeTiles < playerStoneCount) {
            for (Tile tile : state.getMap().getFreeTiles()) {
                RegularMove move = (RegularMove) MoveFactory.createMove(state, playerId, tile.x, tile.y);
                if (move.isLegal()) {
                    legalMoves.add(move);
                }
                move = (RegularMove) MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                if (move.isLegal()) {
                    legalMoves.add(move);
                    move = (RegularMove) MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                    legalMoves.add(move);
                }
                for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                    move = (RegularMove) MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(i));
                    if (move.isLegal()) {
                        legalMoves.add(move);
                    }
                }
            }
            return legalMoves;
        }


        for (Tile tile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones

            for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
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
                                    legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(i)));
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
     * Returns all legal override {@link Move}s possible from a certain given board state and player in the first phase.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId the {@code id} of the current {@link Player} in turn
     * @return a set of all {@link OverrideMove}s being legal in the given board state
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

            for (int ogDirection = 0; ogDirection < Direction.DIRECTION_COUNT; ogDirection++) {
                int searchDirection = ogDirection;
                Tile last = ogTile;

                while (true) {
                    Tile next = last.getTransition(searchDirection);
                    if (next == null ||
                            (next.getOwnerId() == Player.NULL_PLAYER_ID && next.getProperty() != Tile.Property.EXPANSION)) {
                        // next is hole or unowned
                        break;
                    }
                    if (next != ogTile.getTransition(ogDirection) && next != ogTile) {
                        // next is not right next to og in search direction or og
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
     * @param state    the {@link GameState} to be examined
     * @param playerId the {@code id} of the current {@link Player} in turn
     * @return a set of all {@link BombMove}s being legal in the given board state
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


    public static Move quickLegalRegularMove(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        int freeTiles = state.getMap().getFreeTiles().size();
        int playerStoneCount = state.getPlayerFromId(playerId).getStoneCount();
        boolean bombBonus = (pow(2 * state.getBombRadius() + 1, 2) > 10 * sqrt(state.getMap().height * state.getMap().width));

        if (state.getPlayerFromId(playerId).isDisqualified() || freeTiles == 0)
            return null;

        Move legalMove = null;

        if (freeTiles < playerStoneCount) {
            for (Tile tile : state.getMap().getFreeTiles()) {
                if (tile.getProperty() == Tile.Property.DEFAULT || tile.getProperty() == Tile.Property.INVERSION) {
                    legalMove = MoveFactory.createMove(state, playerId, tile.x, tile.y);
                    break;
                } else if (tile.getProperty() == Tile.Property.BONUS) {
                    if (bombBonus) legalMove = MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                    else legalMove = MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                    break;
                } else if (tile.getProperty() == Tile.Property.CHOICE) {
                    int i = (int)(Math.random() * 8);
                    legalMove = MoveFactory.createMove(state, playerId, tile.x, tile.y, new BonusRequest(i));
                    break;
                }
            }

            return legalMove;
        }


        for (Tile tile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones

            for (int direction = 0; direction < Direction.DIRECTION_COUNT; direction++) {
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
                                int i = (int)(Math.random() * state.getTotalPlayerCount());
                                legalMove = MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(i));
                                return legalMove;
                            } else if (steps > 0 && last.getProperty() == Tile.Property.BONUS) {
                                if (bombBonus) legalMove = MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                                else legalMove = MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                                return legalMove;
                            } else if (steps > 0) {
                                legalMove = MoveFactory.createMove(state, playerId, last.x, last.y);
                                return legalMove;
                            }
                            break;
                        }
                    }

                    if (last != last.getTransition(searchDirection))
                        steps++; // increment step counter only if last isn't self-neighboring
                }
            }
        }
        return legalMove;

    }


    public static Move quickLegalOverrideMove(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified() || state.getPlayerFromId(playerId).getOverrideStoneCount() <= 0)
            return null;

        Move legalMove = null;

        for (Tile ogTile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones

            for (int ogDirection = 0; ogDirection < Direction.DIRECTION_COUNT; ogDirection++) {
                int searchDirection = ogDirection;
                Tile last = ogTile;

                while (true) {
                    Tile next = last.getTransition(searchDirection);
                    if (next == null ||
                            (next.getOwnerId() == Player.NULL_PLAYER_ID && next.getProperty() != Tile.Property.EXPANSION)) {
                        // next is hole or unowned
                        break;
                    }
                    if (next != ogTile.getTransition(ogDirection) && next != ogTile) {
                        // next is not right next to og in search direction or og
                        legalMove = MoveFactory.createMove(state, playerId, next.x, next.y);
                        return legalMove;
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

        // independent expansion moves are possible override moves
        if (!state.getMap().getExpansionTiles().isEmpty()) {
            Tile expansion = state.getMap().getExpansionTiles().iterator().next();
            legalMove = MoveFactory.createMove(state, playerId, expansion.x, expansion.y);
        }

        return legalMove;
    }





}
