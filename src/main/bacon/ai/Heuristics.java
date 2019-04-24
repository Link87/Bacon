package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.Player;
import bacon.Tile;

import java.util.Iterator;

public class Heuristics {
    private static Heuristics heuristic = new Heuristics();

    public static Heuristics getHeuristic() {
        return heuristic;
    }

    private Heuristics() {
    }

    /**
     * Determines whether there are still inversion/choice tiles on the map and hints
     * uncertainty about stone ownership
     *
     * @param state GameState to be examined
     * @return whether this game state is in the uncertainty phase
     */
    public boolean uncertaintyPhase(GameState state){
        return true;
    }

    /**
     * Calculates the mobility heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param player in turn
     * @return a real number as mobility heuristics
     */
    public double mobility(GameState state, Player player){
        double mobility = 0;

        return mobility;
    }

    /**
     * Calculates the stability heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param player in turn
     * @return a real number as stability heuristics
     */
    public double stability(GameState state, Player player){
        Iterator<Tile> stoneIterator = player.getStonesIterator();
        Tile stone;

        while(stoneIterator.hasNext()){
            stone = stoneIterator.next();

        }
        return 0;
    }

    /**
     * Calculates the clustering heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param player in turn
     * @return a real number as clustering heuristics
     */
    public double clustering(GameState state, Player player){
        Iterator<Tile> stoneIterator = player.getStonesIterator();
        Tile stone;

        while(stoneIterator.hasNext()){
            stone = stoneIterator.next();

        }
        return 0;
    }

    /**
     * Calculates the bonus heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param player in turn
     * @return a real number as bonus heuristics
     */
    public double bonus(GameState state, Player player){
        return 0;
    }

}
