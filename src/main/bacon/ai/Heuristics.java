package bacon.ai;

import bacon.*;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

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
    //TODO Optimize this methods by including inversion/choice tile coordinates as stateless attribute of Game
    public boolean uncertaintyPhase(GameState state){
        for(int x = 0; x < state.getMap().width; x++){
            for(int y = 0; y < state.getMap().height; y++){
                if(state.getMap().getTileAt(x,y).getProperty() == Tile.Property.CHOICE ||
                   state.getMap().getTileAt(x,y).getProperty() == Tile.Property.INVERSION){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the mobility heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param playerNr Number of player in turn
     * @return a real number as mobility heuristics
     */
    public double mobility(GameState state, int playerNr){
        double mobility;

        if(state.getGamePhase() != GamePhase.PHASE_ONE){
            throw new IllegalArgumentException("Mobility heuristics should only be used in build phase");
        }

        mobility = LegalMoves.legalMoves(state, playerNr, MoveType.REGULAR).size();
        //TODO: Weight regular move mobility against override move mobility
        mobility += LegalMoves.legalMoves(state, playerNr, MoveType.OVERRIDE).size();

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
        Tile tile;

        while(stoneIterator.hasNext()){
            tile = stoneIterator.next();

        }
        return 0;
    }

    /**
     * Calculates the clustering heuristics of this certain given game state and player.
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as clustering heuristics
     */
    public double clustering(GameState state, int playerNr){
        int playerStoneCount = state.getPlayerFromNumber(playerNr).getStoneCount();
        int bombRadius = state.getBombRadius();
        int totalPlayer = state.getTotalPlayerCount();
        int diameter = 2*bombRadius;

        double clusteringSum = 0;

        double[] rivalry = new double[totalPlayer];
        int[] rivalBombCount = new int[totalPlayer];
        int[] rivalStoneCount = new int[totalPlayer];
        int[] bombedStoneCount = new int[totalPlayer];

        for(int i = 0; i < totalPlayer; i++){
            rivalBombCount[i] = state.getPlayerFromNumber(i).getBombCount();
            rivalStoneCount[i] = state.getPlayerFromNumber(i).getStoneCount();
        }

        for(int i = 0; i < totalPlayer; i++){
            if(i == playerNr){
                rivalry[i] = -1;
            }
            else {
                rivalry[i] = (rivalBombCount[i]*(2*bombRadius+1)^2)/((totalPlayer-1)*(abs(rivalStoneCount[i]-playerStoneCount)+1));
            }
        }

        Iterator<Tile> stoneIterator = state.getPlayerFromNumber(playerNr).getStonesIterator();
        Tile stone;


        while(stoneIterator.hasNext()){
            stone = stoneIterator.next();

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
                for (Tile t: currentTiles) {
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

            for(Tile t: bombSet){
                if(t.getOwner() != null){
                    bombedStoneCount[t.getOwner().getPlayerNumber()-1]++;
                }
            }

            for(int i = 0; i < totalPlayer; i++){
                clusteringSum += bombedStoneCount[i]*rivalry[i];
            }
        }

        double clusteringScaled = clusteringSum/((2*(2*bombRadius+1)-1)^2);
        return clusteringScaled;
    }

    /**
     * Calculates the bomb bonus heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public double bonusBomb(GameState state, int playerNr){
        int bombCount = state.getPlayerFromNumber(playerNr).getBombCount();
        int bombRadius = state.getBombRadius();

        double bonusBomb = 2*(2*bombRadius+1)^2*bombCount;

        return bonusBomb;
    }

    /**
     * Calculates the override bonus heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public double bonusOverride(GameState state, int playerNr){
        int overrideStoneCount = state.getPlayerFromNumber(playerNr).getOverrideStoneCount();
        double mapHeight = state.getMap().height;
        double mapWidth = state.getMap().width;

        double bonusOverride = 2*sqrt(mapHeight*mapWidth)*overrideStoneCount;

        return bonusOverride;
    }

}
