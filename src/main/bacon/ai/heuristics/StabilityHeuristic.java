package bacon.ai.heuristics;

import bacon.Direction;
import bacon.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains methods for the stability heuristic.
 */
public class StabilityHeuristic {

    private static final Set<Tile> horzStbl = new HashSet<>();

    /*
    Sets that solely exist, to avoid huge method signatures. These are reset after each usage.
     */
    private static final Set<Tile> vertStbl = new HashSet<>();
    private static final Set<Tile> diagStbl = new HashSet<>();
    private static final Set<Tile> indiagStbl = new HashSet<>();
    private static final Set<Tile> horzFinal = new HashSet<>();
    private static final Set<Tile> vertFinal = new HashSet<>();
    private static final Set<Tile> diagFinal = new HashSet<>();
    private static final Set<Tile> indiagFinal = new HashSet<>();
    private static final Set<Tile> tmpHorz = new HashSet<>();
    private static final Set<Tile> tmpVert = new HashSet<>();
    private static final Set<Tile> tmpDiag = new HashSet<>();
    private static final Set<Tile> tmpIndiag = new HashSet<>();

    private StabilityHeuristic() {}

    /**
     * Calculates the stability heuristics of the given {@link bacon.GameState} and {@link bacon.Player}.
     *
     * @param state    the {@link bacon.GameState} to be examined
     * @param playerId {@code id} of the {@link bacon.Player} in turn
     * @return a real number as mobility heuristics
     */
    public static double stability(bacon.GameState state, int playerId) {

        // Iterates over all player's stones and categorizes them according to stability directions
        for (Tile stone : state.getPlayerFromId(playerId).getStones()) {

            if (stone.getTransition(Direction.LEFT.id) == null || stone.getTransition(Direction.RIGHT.id) == null || stone.getRow().getFillLevel() == stone.getRow().getLineSize()) {
                horzStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP.id) == null || stone.getTransition(Direction.DOWN.id) == null || stone.getColumn().getFillLevel() == stone.getColumn().getLineSize()) {
                vertStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP_RIGHT.id) == null || stone.getTransition(Direction.DOWN_LEFT.id) == null || stone.getDiagonal().getFillLevel() == stone.getDiagonal().getLineSize()) {
                diagStbl.add(stone);
            }

            if (stone.getTransition(Direction.UP_LEFT.id) == null || stone.getTransition(Direction.DOWN_RIGHT.id) == null || stone.getIndiagonal().getFillLevel() == stone.getIndiagonal().getLineSize()) {
                indiagStbl.add(stone);
            }

        }

        // Gradually extends stability from stable stones to neighbouring stones
        while (!horzStbl.isEmpty() || !vertStbl.isEmpty() || !diagStbl.isEmpty() || !indiagStbl.isEmpty()) {
            for (Tile tile : horzStbl) { // Extending horizontally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.LEFT.id) != null && tile.getTransition(Direction.LEFT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.LEFT.id);
                }
                if (tile.getTransition(Direction.RIGHT.id) != null && tile.getTransition(Direction.RIGHT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.RIGHT.id);
                }
                horzFinal.add(tile); // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : vertStbl) {  // Extending vertically stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP.id) != null && tile.getTransition(Direction.UP.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.UP.id);
                }
                if (tile.getTransition(Direction.DOWN.id) != null && tile.getTransition(Direction.DOWN.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.DOWN.id);
                }
                vertFinal.add(tile); // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : diagStbl) {  // Extending diagonally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP_RIGHT.id) != null && tile.getTransition(Direction.UP_RIGHT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.UP_RIGHT.id);
                }
                if (tile.getTransition(Direction.DOWN_LEFT.id) != null && tile.getTransition(Direction.DOWN_LEFT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.DOWN_LEFT.id);
                }
                diagFinal.add(tile);    // Adds stone to final stability set and never consider this stability direction of this stone again
            }

            for (Tile tile : indiagStbl) {    // Extending indiagonally stable stones
                // In case there is a neighbour in this stability direction, find the arrival direction and add neighbour to temporary memory
                if (tile.getTransition(Direction.UP_LEFT.id) != null && tile.getTransition(Direction.UP_LEFT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.UP_LEFT.id);
                }
                if (tile.getTransition(Direction.DOWN_RIGHT.id) != null && tile.getTransition(Direction.DOWN_RIGHT.id).getOwnerId() == playerId) {
                    stabilityFinder(tile, Direction.DOWN_RIGHT.id);
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
     * Finds arrival direction and adds the {@link Tile} to the according stability direction.
     * <p>
     * Subroutine for the stability heuristic.
     *
     * @param tile      the {@code Tile} to be examined
     * @param direction the {@link Direction} in integer representation of the original stable stone
     */
    private static void stabilityFinder(Tile tile, int direction) {
        switch (Direction.fromId(tile.getArrivalDirection(direction))) {
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

}
