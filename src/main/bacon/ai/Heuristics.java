package bacon.ai;

import bacon.*;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Heuristics {
    private static Heuristics heuristic = new Heuristics();

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
    public static boolean uncertaintyPhase(GameState state){
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
    public static double mobility(GameState state, int playerNr){
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
     * @param playerNr number of the player in turn
     * @return a real number as stability heuristics
     */
    public static double stability(GameState state, int playerNr){
        Iterator<Tile> stoneIterator = state.getPlayerFromNumber(playerNr).getStonesIterator();
        Tile stone;

        while(stoneIterator.hasNext()){
            stone = stoneIterator.next();

            if(stone.getTransition(Direction.LEFT) == null||stone.getTransition(Direction.RIGHT)==null){
                horzStbl.add(stone);
            }

            if(stone.getTransition(Direction.UP) == null||stone.getTransition(Direction.DOWN) == null){
                vertStbl.add(stone);
            }

            if(stone.getTransition(Direction.UP_RIGHT) == null||stone.getTransition(Direction.DOWN_LEFT) == null){
                diagStbl.add(stone);
            }

            if(stone.getTransition(Direction.UP_LEFT) == null||stone.getTransition(Direction.DOWN_RIGHT) == null){
                indiagStbl.add(stone);
            }
        }

        while(!horzStbl.isEmpty() || !vertStbl.isEmpty() || !diagStbl.isEmpty() || !indiagStbl.isEmpty()){
            for(Tile tile: horzStbl) {
                if (tile.getTransition(Direction.LEFT) != null && tile.getTransition(Direction.LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.LEFT);
                }
                if (tile.getTransition(Direction.RIGHT) != null && tile.getTransition(Direction.RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.RIGHT);
                }
                horzFinal.add(tile);
            }
            horzStbl.clear();
            horzStbl.addAll(tmpHorz);
            tmpHorz.clear();

            for(Tile tile: vertStbl) {
                if (tile.getTransition(Direction.UP) != null && tile.getTransition(Direction.UP).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.UP);
                }
                if (tile.getTransition(Direction.DOWN) != null && tile.getTransition(Direction.DOWN).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.DOWN);
                }
                vertFinal.add(tile);
            }
            vertStbl.clear();
            vertStbl.addAll(tmpVert);
            tmpVert.clear();

            for(Tile tile: diagStbl) {
                if (tile.getTransition(Direction.UP_RIGHT) != null && tile.getTransition(Direction.UP_RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.UP_RIGHT);
                }
                if (tile.getTransition(Direction.DOWN_LEFT) != null && tile.getTransition(Direction.DOWN_LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.DOWN_LEFT);
                }
                diagFinal.add(tile);
            }
            diagStbl.clear();
            diagStbl.addAll(tmpDiag);
            tmpDiag.clear();

            for(Tile tile: indiagStbl) {
                if (tile.getTransition(Direction.UP_LEFT) != null && tile.getTransition(Direction.UP_LEFT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.UP_LEFT);
                }
                if (tile.getTransition(Direction.DOWN_RIGHT) != null && tile.getTransition(Direction.DOWN_RIGHT).getOwner() == state.getPlayerFromNumber(playerNr)) {
                    stabilityFinder(tile,Direction.DOWN_RIGHT);
                }
                indiagFinal.add(tile);
            }
            indiagStbl.clear();
            indiagStbl.addAll(tmpIndiag);
            tmpIndiag.clear();
        }
        return horzFinal.size() + vertFinal.size() + diagFinal.size() + indiagFinal.size();
    }

    private static void stabilityFinder(Tile tile, Direction direction){
        switch (tile.getArrivalDirection(direction)) {
            case DOWN:
            case UP:
                if(!vertFinal.contains(tile.getTransition(direction))) {
                    tmpVert.add(tile.getTransition(direction));
                }
                break;
            case LEFT:
            case RIGHT:
                if(!horzFinal.contains(tile.getTransition(direction))){
                    tmpHorz.add(tile.getTransition(direction));
                }
                break;
            case DOWN_LEFT:
            case UP_RIGHT:
                if(!diagFinal.contains(tile.getTransition(direction))){
                    tmpDiag.add(tile.getTransition(direction));
                }
                break;
            case UP_LEFT:
            case DOWN_RIGHT:
                if(!indiagFinal.contains(tile.getTransition(direction))) {
                    tmpIndiag.add(tile.getTransition(direction));
                }
                break;
        }
    }

    /**
     * Calculates the clustering heuristics of this certain given game state and player.
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as clustering heuristics
     */

    //TODO: Scale clustering heuristic depending on number of free tiles
    public static double clustering(GameState state, int playerNr){
        int playerStoneCount = state.getPlayerFromNumber(playerNr).getStoneCount();
        int bombRadius = state.getBombRadius();
        int totalPlayer = state.getTotalPlayerCount();

        double[] rivalry = new double[totalPlayer]; // rivalry factor between the player and each of his rivals (all other players).
                                                    // The closer in stone count the stronger the rivalry.
        int[] rivalBombCount = new int[totalPlayer]; // the numbers of bombs of each rival
        int[] rivalStoneCount = new int[totalPlayer]; // the total numbers of stones of each rival

        for(int i = 0; i < totalPlayer; i++){ // calculates global variables of the current state
            rivalBombCount[i] = state.getPlayerFromNumber(i+1).getBombCount();
            rivalStoneCount[i] = state.getPlayerFromNumber(i+1).getStoneCount();
        }

        for(int i = 0; i < totalPlayer; i++){ // calculates the rivalry factor between the player and each of his rivals
            if(i+1 == playerNr){
                rivalry[i] = -1; // rivalry factor with oneself is -1
            }
            else {
                rivalry[i] = (rivalBombCount[i]*(Math.pow(2*bombRadius+1,2)))/((totalPlayer-1)*(abs(rivalStoneCount[i]-playerStoneCount)+1));
            }
        }

        Iterator<Tile> stoneIterator = state.getPlayerFromNumber(playerNr).getStonesIterator();
        Tile stone;
        int[] bombedStoneCount = new int[totalPlayer]; // the numbers of stones of each rival within the bomb diameter of a player's stone
        int diameter = 2*bombRadius;
        double clusteringSum = 0; // heuristic to be returned

        while(stoneIterator.hasNext()){ // iterates over all player's stones; adding clustering factor of each stone to clusteringSum
            stone = stoneIterator.next();


            // in the follow part of the code we search for all tiles within one bomb diameter (not radius!) of a player's stone
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
            // end of recycled code


            for(Tile t: bombSet){   // we examine each tile within the bomb diameter for ownership
                                    // and assign collateral damage to each rival in case our stone is bombed
                if(t.getOwner() != null){
                    bombedStoneCount[t.getOwner().getPlayerNumber()-1]++;
                }
            }
            bombedStoneCount[playerNr-1]--;              //this line removes the target itself from collateral damage

            for(int i = 0; i < totalPlayer; i++){                   // This for-loop sums collateral damage over all rivals
                clusteringSum += bombedStoneCount[i]*rivalry[i];    // to get the clustering factor of a player's stone,
                bombedStoneCount[i]=0;
            }                                                       // the outer while-loop sums clustering factors over all
        }                                                           // of player's stones to get the total clustering factor of the game state

        double clusteringScaled = clusteringSum/(Math.pow(2*(2*bombRadius+1)-1,2)); // re-normalizes clustering factor such that
        return clusteringScaled;                                            // it is independent of bomb radius
    }

    /**
     * Calculates the bomb bonus heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public static double bonusBomb(GameState state, int playerNr){
        int bombCount = state.getPlayerFromNumber(playerNr).getBombCount();
        int bombRadius = state.getBombRadius();

        double bonusBomb = 2*(Math.pow(2*bombRadius+1,2))*bombCount;

        return bonusBomb;
    }

    /**
     * Calculates the override bonus heuristics of this certain given game state and player
     *
     * @param state GameState to be examined
     * @param playerNr number of player in turn
     * @return a real number as bonus heuristics
     */
    public static double bonusOverride(GameState state, int playerNr){
        int overrideStoneCount = state.getPlayerFromNumber(playerNr).getOverrideStoneCount();
        double mapHeight = state.getMap().height;
        double mapWidth = state.getMap().width;

        double bonusOverride = 2*sqrt(mapHeight*mapWidth)*overrideStoneCount;

        return bonusOverride;
    }

}
