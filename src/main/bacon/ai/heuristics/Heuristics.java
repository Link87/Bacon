package bacon.ai.heuristics;

import bacon.*;
import bacon.move.BombMove;

import java.util.Set;

import static java.lang.Math.*;

/**
 * A collection of heuristic methods.
 * <p>
 * All methods are static, stateless and stand-alone. Therefore no instances of {@code Heuristics} can be created.
 */
public class Heuristics {

    private Heuristics() {}

    /**
     * Returns whether the game is still in uncertainty phase.
     * <p>
     * This is the case, if there are still inversion or choice tiles on the map and hints
     * uncertainty about stone ownership.
     *
     * @param state the {@link GameState} to be examined
     * @return whether this game state is in the uncertainty phase
     */
    static boolean isUncertaintyPhase(GameState state) {
        // TODO Optimize this methods by including inversion/choice tile coordinates as stateful attribute of Game
        for (int x = 0; x < state.getMap().width; x++) {
            for (int y = 0; y < state.getMap().height; y++) {
                if (state.getMap().getTileAt(x, y).getProperty() == Tile.Property.CHOICE ||
                        state.getMap().getTileAt(x, y).getProperty() == Tile.Property.INVERSION) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the mobility heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a real number as mobility heuristics
     */
    public static int mobility(GameState state, int playerId) {
        int mobility;

        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Mobility heuristics should only be used in build phase");
        }

        mobility = LegalMoves.getLegalRegularMoves(state, playerId).size();
        //TODO: Weight regular move mobility against override move mobility
        mobility += LegalMoves.getLegalOverrideMoves(state, playerId).size();

        return mobility;
    }

    /**
     * Calculates the override stability heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a real number as override stability heuristics
     */
    public static int overrideStability(GameState state, int playerId) {
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
     * Calculates the bomb bonus heuristics of the given game state and player.
     *
     * @param state    the {@link GameState} to be examined
     * @param playerId {@code id} of the {@link Player} in turn
     * @return a real number as mobility heuristics
     */
    public static double bonusBomb(GameState state, int playerId) {
        int bombCount = state.getPlayerFromId(playerId).getBombCount();
        int bombRadius = state.getBombRadius();

        return 20 * (pow(2 * bombRadius + 1, 2)) * (pow(bombCount, 0.7));
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
        double mapHeight = state.getMap().height;
        double mapWidth = state.getMap().width;

        return 20 * sqrt(mapHeight * mapWidth) * (pow(overrideStoneCount, 0.7));
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
        int[] rankPoints = {-25,-11,-5,-2,-1,0,0,0};

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
            for (int j=0; j < totalPlayer; j++) {
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
        if (bombSet.isEmpty()) bombSet = BombMove.getAffectedTiles(state.getMap().getTileAt(move.getX(), move.getY()), bombRadius);

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
