package bacon.ai.heuristics;

import bacon.Direction;
import bacon.GamePhase;
import bacon.GameState;
import bacon.Tile;
import bacon.ai.MoveType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Heuristics {

    private Heuristics() {}

    /**
     * Determines whether there are still inversion/choice tiles on the map and hints
     * uncertainty about stone ownership
     *
     * @param state GameState to be examined
     * @return whether this game state is in the uncertainty phase
     */
    public static boolean isUncertaintyPhase(GameState state) {
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
     * Calculates the mobility heuristics of this certain given game state and player
     *
     * @param state    GameState to be examined
     * @param playerNr Number of player in turn
     * @return a real number as mobility heuristics
     */
    public static int mobility(GameState state, int playerNr) {
        int mobility;

        if (state.getGamePhase() != GamePhase.PHASE_ONE) {
            throw new IllegalArgumentException("Mobility heuristics should only be used in build phase");
        }

        mobility = LegalMoves.getLegalMoveTiles(state, playerNr, MoveType.REGULAR).size();
        //TODO: Weight regular move mobility against override move mobility
        mobility += LegalMoves.getLegalMoveTiles(state, playerNr, MoveType.OVERRIDE).size();

        return mobility;
    }

    /**
     * Calculates the clustering heuristics of this certain given game state and player.
     *
     * @param state    GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as clustering heuristics
     */
    public static double clustering(GameState state, int playerNr) {
        // TODO: Scale clustering heuristic depending on number of free tiles
        int playerStoneCount = state.getPlayerFromNumber(playerNr).getStoneCount();
        int bombRadius = state.getBombRadius();
        int totalPlayer = state.getTotalPlayerCount();

        double[] rivalry = new double[totalPlayer]; // rivalry factor between the player and each of his rivals (all other players).
        // The closer in stone count the stronger the rivalry.
        int[] rivalBombCount = new int[totalPlayer]; // the number of bombs of each rival
        int[] rivalStoneCount = new int[totalPlayer]; // the total number of stones of each rival

        for (int i = 0; i < totalPlayer; i++) { // calculates global variables of the current state
            rivalBombCount[i] = state.getPlayerFromNumber(i + 1).getBombCount();
            rivalStoneCount[i] = state.getPlayerFromNumber(i + 1).getStoneCount();
        }

        for (int i = 0; i < totalPlayer; i++) { // calculates the rivalry factor between the player and each of his rivals
            if (i + 1 == playerNr) {
                rivalry[i] = -1; // rivalry factor with oneself is -1
            } else {
                rivalry[i] = (rivalBombCount[i] * (pow(2 * bombRadius + 1, 2))) / ((totalPlayer - 1) * (abs(rivalStoneCount[i] - playerStoneCount) + 1));
            }
        }

        Iterator<Tile> stoneIterator = state.getPlayerFromNumber(playerNr).getStonesIterator();
        int[] bombedStoneCount = new int[totalPlayer]; // the number of stones of each rival within the bomb diameter of a player's stone
        int diameter = 2 * bombRadius;
        double clusteringSum = 0; // heuristic to be returned

        while (stoneIterator.hasNext()) { // iterates over all player's stones; adding clustering factor of each stone to clusteringSum
            Tile stone = stoneIterator.next();

            // in the following part of the code we search for all tiles within one bomb diameter (not radius!) of a player's stone
            // we recycle code from BombMove for this purpose

            // set of already examined tiles
            Set<Tile> bombSet = new HashSet<>();
            // initializing ArrayList to examine the tiles which are i away from the tile which is bombed
            var currentTiles = new ArrayList<Tile>();
            // initializing ArrayList to save the tiles which are i+1 away from the tile which is bombed
            var nextTiles = new ArrayList<Tile>();

            bombSet.add(stone);
            currentTiles.add(stone);

            //searches for all neighbours that need to be bombed out
            for (int i = 0; i < diameter; i++) {
                for (Tile t : currentTiles) {
                    for (Direction direction : Direction.values()) {
                        if (t.getTransition(direction) != null) {
                            if (!bombSet.contains(t.getTransition(direction))) {
                                bombSet.add(t.getTransition(direction));
                                nextTiles.add(t.getTransition(direction));
                            }
                        }
                    }
                }
                currentTiles = nextTiles;
                nextTiles = new ArrayList<>((i + 1) * 8);
            }
            // end of recycled code


            for (Tile t : bombSet) {   // we examine each tile within the bomb diameter for ownership
                // and assign collateral damage to each rival in case our stone is bombed
                if (t.getOwner() != null) {
                    bombedStoneCount[t.getOwner().getPlayerNumber() - 1]++;
                }
            }
            bombedStoneCount[playerNr - 1]--;              //this line removes the target itself from collateral damage

            for (int i = 0; i < totalPlayer; i++) {                   // This for-loop sums collateral damage over all rivals
                clusteringSum += bombedStoneCount[i] * rivalry[i];    // to get the clustering factor of a player's stone,
                bombedStoneCount[i] = 0;
            }                                                       // the outer while-loop sums clustering factors over all
        }                                                           // of player's stones to get the total clustering factor of the game state

        double clusteringScaled = clusteringSum / (pow(2 * (2 * bombRadius + 1) - 1, 2)); // re-normalizes clustering factor such that it is independent of bomb radius

        int totalTileCount = state.getTotalTileCount();                 // weights clustering heuristic according to game stage:
        int occupiedTileCount = state.getOccupiedTileCount();           // the later in the game, the higher the occupation ratio,
        double occupationRatio = occupiedTileCount / totalTileCount;    // the more important clustering heuristics becomes
        double clusteringWeighted = clusteringScaled * occupationRatio;
        return clusteringWeighted;
    }

    /**
     * Calculates the bomb bonus heuristics of this certain given game state and player.
     *
     * @param state    GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public static double bonusBomb(GameState state, int playerNr) {
        int bombCount = state.getPlayerFromNumber(playerNr).getBombCount();
        int bombRadius = state.getBombRadius();

        return 2 * (pow(2 * bombRadius + 1, 2)) * (pow(bombCount, 0.7));
    }

    /**
     * Calculates the override bonus heuristics of this certain given game state and player.
     *
     * @param state    GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public static double bonusOverride(GameState state, int playerNr) {
        int overrideStoneCount = state.getPlayerFromNumber(playerNr).getOverrideStoneCount();
        double mapHeight = state.getMap().height;
        double mapWidth = state.getMap().width;

        return 2 * sqrt(mapHeight * mapWidth) * (pow(overrideStoneCount, 0.7));
    }

    /**
     * Calculates the clustering heuristics of this certain given game state, player and target tile. This heuristic is
     * basically a copy of clustering heuristic, but evaluates MOVES directly instead of GAME STATES due to high branching
     * factor in the Bombing Phase
     *
     * @param state         GameState to be examined
     * @param playerNr      number of player in turn
     * @param targetTile    the tile to be bombed in the next move
     * @return              a real number as clustering heuristics (the only heuristics that matters in Bombing Phase)
     */
    public static double bombingPhaseHeuristic (GameState state, int playerNr, Tile targetTile) {
        int playerStoneCount = state.getPlayerFromNumber(playerNr).getStoneCount();
        int bombRadius = state.getBombRadius();
        int totalPlayer = state.getTotalPlayerCount();

        double[] rivalry = new double[totalPlayer]; // rivalry factor between the player and each of his rivals (all other players).
        // The closer in stone count the stronger the rivalry.
        int[] rivalBombCount = new int[totalPlayer]; // the number of bombs of each rival
        int[] rivalStoneCount = new int[totalPlayer]; // the total number of stones of each rival

        for (int i = 0; i < totalPlayer; i++) { // calculates global variables of the current state
            rivalBombCount[i] = state.getPlayerFromNumber(i + 1).getBombCount();
            rivalStoneCount[i] = state.getPlayerFromNumber(i + 1).getStoneCount();
        }

        for (int i = 0; i < totalPlayer; i++) { // calculates the rivalry factor between the player and each of his rivals
            if (i + 1 == playerNr) {
                rivalry[i] = -1; // rivalry factor with oneself is -1
            } else {
                rivalry[i] = (rivalBombCount[i] * (pow(2 * bombRadius + 1, 2))) / ((totalPlayer - 1) * (abs(rivalStoneCount[i] - playerStoneCount) + 1));
            }
        }

        int[] bombedStoneCount = new int[totalPlayer]; // the number of stones of each rival within the bomb radius of the target tile
        double clusteringSum = 0; // heuristic to be returned


        // in the following part of the code we search for all tiles within one bomb radius of the bombing target
        // we recycle code from BombMove for this purpose

        // set of already examined tiles
        Set<Tile> bombSet = new HashSet<>();
        // initializing ArrayList to examine the tiles which are i away from the tile which is bombed
        var currentTiles = new ArrayList<Tile>();
        // initializing ArrayList to save the tiles which are i+1 away from the tile which is bombed
        var nextTiles = new ArrayList<Tile>();

        bombSet.add(targetTile);
        currentTiles.add(targetTile);

        //searches for all neighbours that need to be bombed out
        for (int i = 0; i < bombRadius; i++) {
            for (Tile t : currentTiles) {
                for (Direction direction : Direction.values()) {
                    if (t.getTransition(direction) != null) {
                        if (!bombSet.contains(t.getTransition(direction))) {
                            bombSet.add(t.getTransition(direction));
                            nextTiles.add(t.getTransition(direction));
                        }
                    }
                }
            }
            currentTiles = nextTiles;
            nextTiles = new ArrayList<>((i + 1) * 8);
        }
        // end of recycled code


        for (Tile t : bombSet) {   // we examine each tile within the bomb radius for ownership
            // and assign damage to each rival in case our stone is bombed
            if (t.getOwner() != null) {
                bombedStoneCount[t.getOwner().getPlayerNumber() - 1]++;
            }
        }

        for (int i = 0; i < totalPlayer; i++) {                   // This for-loop sums damage over all rivals
            clusteringSum += bombedStoneCount[i] * rivalry[i];    // to get the clustering factor of the target tile
            bombedStoneCount[i] = 0;
        }

        double clusteringScaled = clusteringSum / (pow(2 * (2 * bombRadius + 1) - 1, 2)); // re-normalizes clustering factor such that
        return clusteringScaled;                                            // it is independent of bomb radius
    }

}
