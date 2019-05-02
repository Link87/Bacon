package bacon.ai.heuristics;

import bacon.Direction;
import bacon.Tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Contains the stability heuristic.
 */
public class StabilityHeuristic {

    // Variables only needed for stability heuristic
    private static Set<Tile> horzStbl = new HashSet<>();
    private static Set<Tile> vertStbl = new HashSet<>();
    private static Set<Tile> diagStbl = new HashSet<>();
    private static Set<Tile> indiagStbl = new HashSet<>();

    private static Set<Tile> horzFinal = new HashSet<>();
    private static Set<Tile> vertFinal = new HashSet<>();
    private static Set<Tile> diagFinal = new HashSet<>();
    private static Set<Tile> indiagFinal = new HashSet<>();

    private static Set<Tile> tmpHorz = new HashSet<>();
    private static Set<Tile> tmpVert = new HashSet<>();
    private static Set<Tile> tmpDiag = new HashSet<>();
    private static Set<Tile> tmpIndiag = new HashSet<>();

    /**
     * Calculates the stability heuristics of this certain given game state and player.
     *
     * @param state    GameState to be examined
     * @param playerNr number of the player in turn
     * @return a real number as stability heuristics
     */
    public static double stability(bacon.GameState state, int playerNr) {
        Iterator<Tile> stoneIterator = state.getPlayerFromNumber(playerNr).getStonesIterator();
        Tile stone;

        while (stoneIterator.hasNext()) {     //Iterates over all player's stones and categorizes them according to stability directions
            stone = stoneIterator.next();

            if (stone.getTransition(Direction.LEFT) == null || stone.getTransition(Direction.RIGHT) == null) {
                horzStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP) == null || stone.getTransition(Direction.DOWN) == null) {
                vertStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP_RIGHT) == null || stone.getTransition(Direction.DOWN_LEFT) == null) {
                diagStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP_LEFT) == null || stone.getTransition(Direction.DOWN_RIGHT) == null) {
                indiagStbl.add(stone);
            }
        }

        // Gradually extends stability from stable stones to neighbouring stones
        while (!horzStbl.isEmpty() || !vertStbl.isEmpty() || !diagStbl.isEmpty() || !indiagStbl.isEmpty()) {
            for (Tile tile : horzStbl) { // Extending horizontally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.LEFT) != null && tile.getTransition(Direction.LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.LEFT);
                }
                if (tile.getTransition(Direction.RIGHT) != null && tile.getTransition(Direction.RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.RIGHT);
                }
                horzFinal.add(tile); // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : vertStbl) {  // Extending vertically stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP) != null && tile.getTransition(Direction.UP).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.UP);
                }
                if (tile.getTransition(Direction.DOWN) != null && tile.getTransition(Direction.DOWN).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.DOWN);
                }
                vertFinal.add(tile); // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : diagStbl) {  // Extending diagonally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP_RIGHT) != null && tile.getTransition(Direction.UP_RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.UP_RIGHT);
                }
                if (tile.getTransition(Direction.DOWN_LEFT) != null && tile.getTransition(Direction.DOWN_LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.DOWN_LEFT);
                }
                diagFinal.add(tile);    // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : indiagStbl) {    // Extending indiagonally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP_LEFT) != null && tile.getTransition(Direction.UP_LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.UP_LEFT);
                }
                if (tile.getTransition(Direction.DOWN_RIGHT) != null && tile.getTransition(Direction.DOWN_RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile, Direction.DOWN_RIGHT);
                }
                indiagFinal.add(tile);  // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            // Update stability set to new batch of newly found stable stones
            horzStbl.clear();
            horzStbl.addAll(tmpHorz);
            tmpHorz.clear();

            vertStbl.clear();
            vertStbl.addAll(tmpVert);
            tmpVert.clear();

            diagStbl.clear();
            diagStbl.addAll(tmpDiag);
            tmpDiag.clear();

            indiagStbl.clear();
            indiagStbl.addAll(tmpIndiag);
            tmpIndiag.clear();
        }

        // Returns the sum of all the stones' stability values
        double value = horzFinal.size() + vertFinal.size() + diagFinal.size() + indiagFinal.size();
        horzFinal.clear();
        vertFinal.clear();
        diagFinal.clear();
        indiagFinal.clear();

        tmpHorz.clear();
        tmpVert.clear();
        tmpDiag.clear();
        tmpIndiag.clear();
        return value;
    }

    /**
     * Part of the stability heuristic.
     * Finds arrival direction and adds the tile to the according stability direction.
     *
     * @param tile      to be examined
     * @param direction of the original stable stone
     */
    private static void stabilityFinder(Tile tile, Direction direction) {
        switch (tile.getArrivalDirection(direction)) {
            case DOWN:
            case UP:
                if (!vertFinal.contains(tile.getTransition(direction))) {    // If the neighbouring tile in this direction has not be examined yet
                    tmpVert.add(tile.getTransition(direction));             // Adds neighbouring tile to the according stability direction
                }
                break;
            case LEFT:
            case RIGHT:
                if (!horzFinal.contains(tile.getTransition(direction))) {     // If the neighbouring tile in this direction has not be examined yet
                    tmpHorz.add(tile.getTransition(direction));             // Adds neighbouring tile to the according stability direction
                }
                break;
            case DOWN_LEFT:
            case UP_RIGHT:
                if (!diagFinal.contains(tile.getTransition(direction))) {     // If the neighbouring tile in this direction has not be examined yet
                    tmpDiag.add(tile.getTransition(direction));             // Adds neighbouring tile to the according stability direction
                }
                break;
            case UP_LEFT:
            case DOWN_RIGHT:
                if (!indiagFinal.contains(tile.getTransition(direction))) {      // If the neighbouring tile in this direction has not be examined yet
                    tmpIndiag.add(tile.getTransition(direction));               // Adds neighbouring tile to the according stability direction
                }
                break;
        }
    }

    private StabilityHeuristic() {}
}
