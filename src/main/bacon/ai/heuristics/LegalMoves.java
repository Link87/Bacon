package bacon.ai.heuristics;

import bacon.*;
import bacon.move.*;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * A collection of methods, that return legal moves in a given game state.
 * <p>
 * All methods are static, stateless and stand-alone. Therefore no instances of {@code LegalMoves} can be created.
 */
public class LegalMoves {

    private LegalMoves() {}

    /**
     * Returns all legal {@link RegularMove}s possible from a certain given board state and player in the first phase.
     * Bonus {@link Move}s requesting Bombs are omitted!
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

        // code path that is faster when only few free tiles are left
        // start search from free tiles instead of player tiles
        if (freeTiles < playerStoneCount) {
            for (Tile tile : state.getMap().getFreeTiles()) {
                RegularMove move = new RegularMove(state, playerId, tile.x, tile.y);
                if (move.isLegal()) {
                    legalMoves.add(move);
                }
                move = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                if (move.isLegal()) {
                    legalMoves.add(move);
                    move = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                    legalMoves.add(move);
                }
                for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                    move = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(i));
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
                                    legalMoves.add(new RegularMove(state, playerId, last.x, last.y, new BonusRequest(i)));
                                }
                            } else if (steps > 0 && last.getProperty() == Tile.Property.BONUS) {
                                legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS)));
                                //legalMoves.add((RegularMove) MoveFactory.createMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS)));
                            } else if (steps > 0) {
                                legalMoves.add(new RegularMove(state, playerId, last.x, last.y));
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
     * Returns all legal {@link OverrideMove}s possible from a certain given board state and player in the first phase.
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
                        legalMoves.add(new OverrideMove(state, playerId, next.x, next.y));
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
                legalMoves.add(new OverrideMove(state, playerId, expansion.x, expansion.y)));

        return legalMoves;
    }

    /**
     * Returns all legal {@link BombMove}s possible from a certain given board state and player in the second phase.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId the {@code id} of the current {@link Player} in turn
     * @return a set of all {@link BombMove}s being legal in the given board state
     */
    public static List<BombMove> getLegalBombMoves(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_TWO) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified() || state.getPlayerFromId(playerId).getBombCount() < 1)
            return Collections.emptyList();

        List<BombMove> legalMoves = new LinkedList<>();
        for (int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) { // Going through the whole map
                Tile tile = state.getMap().getTileAt(x, y);
                if (tile.getProperty() != Tile.Property.HOLE) { // Only if tile is not a hole, it's legal to bomb it
                    legalMoves.add(new BombMove(state, playerId, tile.x, tile.y));
                }
            }
        }

        return legalMoves;
    }


    public static RegularMove quickRegularMove(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        int freeTiles = state.getMap().getFreeTiles().size();
        int playerStoneCount = state.getPlayerFromId(playerId).getStoneCount();
        boolean bombBonus = (pow(2 * state.getBombRadius() + 1, 2) > 10 * sqrt(state.getMap().height * state.getMap().width));

        if (state.getPlayerFromId(playerId).isDisqualified() || freeTiles == 0)
            return null;

        RegularMove legalMove = null;
        RegularMove backUpMove = null;

        if (freeTiles < playerStoneCount) {
            for (Tile tile : state.getMap().getFreeTiles()) {
                boolean repeat = (Math.random() > 0.5);
                if (tile.getProperty() == Tile.Property.DEFAULT || tile.getProperty() == Tile.Property.INVERSION) {
                    legalMove = new RegularMove(state, playerId, tile.x, tile.y);
                    if (legalMove.isLegal()) {
                        if (!repeat) break;
                        else backUpMove = legalMove;
                    }
                } else if (tile.getProperty() == Tile.Property.BONUS) {
                    if (bombBonus)
                        legalMove = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                    else
                        legalMove = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                    if (legalMove.isLegal()) {
                        if (!repeat) break;
                        else backUpMove = legalMove;
                    }
                } else if (tile.getProperty() == Tile.Property.CHOICE) {
                    int i = (int) (Math.random() * state.getTotalPlayerCount() + 1);
                    legalMove = new RegularMove(state, playerId, tile.x, tile.y, new BonusRequest(i));
                    if (legalMove.isLegal()) {
                        if (!repeat) break;
                        else backUpMove = legalMove;
                    }
                }
                legalMove = null;
            }

            if (legalMove != null) return legalMove;
            else return backUpMove;
        }


        for (Tile tile : state.getPlayerFromId(playerId).getStones()) { // iterates over all of the player's stones
            boolean repeat = (Math.random() > 0.5);
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
                                int i = (int) (Math.random() * state.getTotalPlayerCount() + 1);
                                legalMove = new RegularMove(state, playerId, last.x, last.y, new BonusRequest(i));
                                if (!repeat) return legalMove;
                                else break;
                            } else if (steps > 0 && last.getProperty() == Tile.Property.BONUS) {
                                if (bombBonus)
                                    legalMove = new RegularMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                                else
                                    legalMove = new RegularMove(state, playerId, last.x, last.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                                if (!repeat) return legalMove;
                                else break;
                            } else if (steps > 0) {
                                legalMove = new RegularMove(state, playerId, last.x, last.y);
                                if (!repeat) return legalMove;
                                else break;
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

    public static OverrideMove quickOverrideMove(GameState state, int playerId) {
        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Cannot evaluate GameState: GamePhase invalid");
        }

        if (state.getPlayerFromId(playerId).isDisqualified() || state.getPlayerFromId(playerId).getOverrideStoneCount() <= 0)
            return null;

        OverrideMove legalMove = null;

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
                        legalMove = new OverrideMove(state, playerId, next.x, next.y);
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
            legalMove = new OverrideMove(state, playerId, expansion.x, expansion.y);
        }

        return legalMove;
    }

}
