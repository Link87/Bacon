package bacon.ai.heuristics;

import bacon.*;
import bacon.move.BombMove;

import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

/**
 * A collection of heuristic methods.
 * <p>
 * All methods are static, stateless and stand-alone. Therefore no instances of {@code Heuristics} can be created.
 */
public class Heuristics {

    private Heuristics() {}

    /**
     * Calculates the mobility heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a integer number as mobility heuristics
     */
    public static int mobility(GameState state, int playerId) {

        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Mobility heuristics should only be used in build phase");
        }

        return LegalMoves.getLegalRegularMoves(state, playerId).size();
    }

    /**
     * Calculates the override stability heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @return a real number as override stability heuristics
     */
    static int overrideStability(GameState state) {
        int overrideStability = 0;

        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Mobility heuristics should only be used in build phase");
        }

        for (TileLine t : state.getMap().getTileLines()) {
            if (t.getPlayerShare() == t.getLineSize()) overrideStability += t.getPlayerShare();
        }

        return overrideStability;
    }

    /**
     * Calculates a heuristic value from the stone count of other players that own more stones than we do.
     *
     * @param state the {@link GameState} to be examined
     * @return a value indicating the Ais rank, where higher is better
     */
    public static double relativeStoneCount(GameState state) {
        double value = state.getPlayerFromId(state.getMe()).getStoneCount() * state.getTotalPlayerCount();
        for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
            if (i == state.getMe()) continue;
            if (state.getPlayerFromId(state.getMe()).getStoneCount() <= state.getPlayerFromId(i).getStoneCount()) {
                value = value - state.getPlayerFromId(i).getStoneCount();
            }
        }
        return value / state.getTotalPlayerCount();
    }

    /**
     * Calculates the bomb bonus heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a real number as mobility heuristics
     */
    public static double bonusBomb(GameState state, int playerId) {
        int bombCount = state.getPlayerFromId(playerId).getBombCount();
        return state.getMap().getAvgBombArea() * bombCount;
    }

    /**
     * Calculates the override bonus heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a real number as mobility heuristics
     */
    public static double bonusOverride(GameState state, int playerId) {
        int overrideStoneCount = state.getPlayerFromId(playerId).getOverrideStoneCount();
        return state.getMap().getAvgTileLineLength() * overrideStoneCount;
    }

    /**
     * Calculates the clustering heuristics of the given game state.
     * <p>
     * The {@code GameState} is required to be in the second game phase.
     *
     * @param state the {@link GameState} to be examined
     * @param move  the {@link BombMove} to rate
     * @return a real number as clustering heuristics
     */
    public static double bombingPhaseHeuristic(GameState state, BombMove move) {
        int playerStoneCount = state.getPlayerFromId(move.getPlayerId()).getStoneCount();
        int playerBombCount = state.getPlayerFromId(move.getPlayerId()).getBombCount();
        int bombRadius = state.getBombRadius();
        int totalPlayer = state.getTotalPlayerCount();
        int[] rankPoints = {-25, -11, -5, -2, -1, 0, 0, 0};

        double[] rivalry = new double[totalPlayer]; // rivalry factor between the player and each of his rivals (all other players).
        // The closer in stone count the stronger the rivalry.
        int[] rivalStoneCount = new int[totalPlayer]; // the total number of stones of each rival
        double[] rivalRankPoints = new double[totalPlayer]; // the rank-points (25p, 11p, ...) of each rival if the game ended now

        for (int i = 0; i < totalPlayer; i++) { // calculates global variables of the current state
            rivalStoneCount[i] = state.getPlayerFromId(i + 1).getStoneCount();
            rivalRankPoints[i] = state.getPlayerFromId(i + 1).getStoneCount();
        }

        // rivalRankPoints is initialized as stone count and gradually gets replaced by rank-points*(-1) in this loop
        for (int i = 0; i < totalPlayer; i++) {
            double maxStoneCount = 0;
            int maxRankPlayerId = 0;
            for (int j = 0; j < totalPlayer; j++) {
                if (rivalRankPoints[j] > maxStoneCount) {
                    maxStoneCount = rivalRankPoints[j];
                    maxRankPlayerId = j;
                }
            }
            rivalRankPoints[maxRankPlayerId] = rankPoints[i];
        }

        assert playerBombCount > 0 :
                "bombingPhaseHeuristic is a move heuristic: cannot make a move without bombs";

        for (int i = 0; i < totalPlayer; i++) { // calculates the rivalry factor between the player and each of his rivals
            if (i == move.getPlayerId() - 1) {
                rivalry[i] = -1; // rivalry factor with oneself is -1
            } else {
                rivalry[i] = ((-1) * rivalRankPoints[i] / 25) * (playerBombCount * (pow(2 * bombRadius + 1, 2))) /
                        (abs(rivalStoneCount[i] - playerStoneCount) + playerBombCount * pow(2 * bombRadius + 1, 2));
            }
        }

        int[] bombedStoneCount = new int[totalPlayer]; // the number of stones of each rival within the bomb radius of the target tile
        double clusteringSum = 0; // heuristic to be returned


        // all tiles within one bomb radius of the bombing target
        Set<Tile> bombSet = state.getMap().getTileAt(move.getX(), move.getY()).getBombEffect();
        // in case precomputation of bombEffect failed (e.g. bomb radius too big), bombEffect is computed again
        if (bombSet.isEmpty())
            bombSet = BombMove.getAffectedTiles(state.getMap().getTileAt(move.getX(), move.getY()), bombRadius);

        for (Tile t : bombSet) {   // we examine each tile within the bomb radius for ownership
            // and assign damage to each rival in case our stone is bombed
            if (t.getOwnerId() != Player.NULL_PLAYER_ID) {
                bombedStoneCount[t.getOwnerId() - 1]++;
            }
        }

        for (int i = 0; i < totalPlayer; i++) {                   // This for-loop sums damage over all rivals
            clusteringSum += bombedStoneCount[i] * rivalry[i];    // to get the clustering factor of the target tile
            bombedStoneCount[i] = 0;
        }

        return clusteringSum;
    }

}
