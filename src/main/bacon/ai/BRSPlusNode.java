package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.PancakeWatchdog;
import bacon.ai.heuristics.StabilityHeuristic;
import bacon.move.*;

import java.util.*;
import java.lang.Math;

public class BRSPlusNode {

    private static final double STABILITY_SCALAR = 1;
    private static final double MOBILITY_SCALAR = 1;
    private static final double BONUS_SCALAR = 100;

    private final int layer;
    private static int searchDepth;
    private static int branchingFactor;
    private BuildMove bestMove;
    private boolean isMaxNode;
    private GameState state;
    private PancakeWatchdog watchdog;
    /**
     * Type of move that lead to this node
     */
    private final Move.Type type;
    private double value;
    private double privValue;

    private static boolean enablePruning;
    private static boolean enableSorting;
    private double alpha;
    private double beta;

    /**
     * External call constructor for game tree root
     *
     * @param depth           maximum depth to be searched
     * @param branchingFactor maximum branching factor at each node
     * @param enablePruning   set to <code>true</code> if alpha-beta pruning should be applied
     * @param enableSorting   set to <code>true</code> when move sorting should be used
     * @param watchdog        Watchdog timer that triggers when time is running out
     */
    public BRSPlusNode(int depth, int branchingFactor, boolean enablePruning, boolean enableSorting, PancakeWatchdog watchdog) {
        BRSPlusNode.searchDepth = depth;
        BRSPlusNode.branchingFactor = branchingFactor;
        BRSPlusNode.enablePruning = enablePruning;
        BRSPlusNode.enableSorting = enableSorting;

        this.layer = 0;
        this.isMaxNode = true;
        this.type = null;
        this.alpha = -Double.MAX_VALUE;
        this.beta = Double.MAX_VALUE;

        this.watchdog = watchdog;
        this.state = Game.getGame().getCurrentState();
    }

    /**
     * Internal constructor for game tree child nodes
     *
     * @param layer     layer this node is part of (e.g. root is layer 0)
     * @param isMaxNode set to true if max player is in turn at this node
     * @param type      type of move (regular or override) that led to this node
     * @param alpha     alpha value
     * @param beta      beta value
     * @param watchdog  Watchdog timer that triggers when time is running out
     */
    private BRSPlusNode(int layer, boolean isMaxNode, Move.Type type, double alpha, double beta, double privValue, PancakeWatchdog watchdog) {
        this.layer = layer;
        this.isMaxNode = isMaxNode;
        this.type = type;
        this.alpha = alpha;
        this.beta = beta;
        this.privValue = privValue;

        this.watchdog = watchdog;
        this.state = Game.getGame().getCurrentState();
    }

    /**
     * Only used for the root node to return best move to the AI
     *
     * @return best move from node
     */
    public BuildMove getBestMove() {
        return bestMove;
    }

    /**
     * Evaluates a node. Handles cases of "this should be a max/min Node but is actually a min/max node"
     */
    public void evaluateNode() {
        if (this.isMaxNode) {
            if (!evalMaxNode()) {
                this.isMaxNode = false;
                if (!evalMinNode()) {
                    //nobody can make a move
                    this.value = evaluateCurrentState(this.type);
                }
            }
        } else {
            if (!evalMinNode()) {
                this.isMaxNode = true;
                if (!evalMaxNode()) {
                    //nobody can make a move
                    this.value = evaluateCurrentState(this.type);
                }
            }
        }
    }

    /**
     * Evaluates this node as a max node if possible
     *
     * @return false if it turns out not to be a max node, true otherwise
     */
    private boolean evalMaxNode() {
        Set<? extends BuildMove> legalMoves = getMovesOfPlayer(state.getMe());
        if (legalMoves.isEmpty()) return false;

        //at this point this is definitely a max node
        this.value = -Double.MAX_VALUE;
        if (this.layer < BRSPlusNode.searchDepth - 1) {

            Statistics.getStatistics().enterState(this.layer);
            List<? extends BuildMove> moves = getBeamMoves(legalMoves);

            for (BuildMove move : moves) {

                if (this.watchdog.isPancake()) {
                    this.bestMove = null;
                    break;
                }

                BRSPlusNode childNode = new BRSPlusNode(this.layer + 1, !isMaxNode, move.getType(), move.getValue(), this.alpha, this.beta, this.watchdog);
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (childNode.value > this.value) {
                    this.value = childNode.value;
                    this.bestMove = move;

                    this.alpha = this.value;
                    if (BRSPlusNode.enablePruning && this.beta <= this.alpha) {
                        break;
                    }
                }
            }

        } else {
            //lowest layer we want to go
            for (BuildMove move : legalMoves) {

                Statistics.getStatistics().enterMeasuredState(this.layer);
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (move.getValue() > this.value) {
                    this.value = move.getValue();
                    this.bestMove = move;
                    this.alpha = Math.max(this.alpha, this.value);
                    if (BRSPlusNode.enablePruning && this.beta <= this.alpha) {
                        Statistics.getStatistics().leaveMeasuredState();
                        break;
                    }
                }
                Statistics.getStatistics().leaveMeasuredState();

                if (this.watchdog.isPancake()) {
                    break;
                }
            }
        }

        return true;
    }

    /**
     * Evaluates this node as a min node if possible
     *
     * @return false if it turns out not to be a min node, true otherwise
     */
    private boolean evalMinNode() {
        Set<? extends BuildMove> legalMoves = Collections.emptySet();

        //find first min player who can make a move (and his moves)
        int startPlayer = (state.getMe() % state.getTotalPlayerCount()) + 1;
        while (startPlayer != state.getMe()) {
            legalMoves = getMovesOfPlayer(startPlayer);
            if (!legalMoves.isEmpty()) break;
            startPlayer = (startPlayer % state.getTotalPlayerCount()) + 1;
        }
        if (legalMoves.isEmpty()) return false;

        //at this point this is definitely a min node
        this.value = Double.MAX_VALUE;

        //decide which player gets to expand his moves

        List<List<? extends BuildMove>> listOfMoveLists = new ArrayList<>();

        //for each player (which can make a move on his turn)
        //find his moves (add to listOfMoveLists) and execute the best (worst from our perspective)
        //repeat with next player until next player would be us

        //we already know startPlayers legalMoves - so we do him fist
        listOfMoveLists.add(getBeamMoves(legalMoves));
        listOfMoveLists.get(0).get(0).doMove();
        int currentPlayer = (startPlayer % state.getTotalPlayerCount()) + 1;

        while (currentPlayer != state.getMe()) {

            if (this.watchdog.isPancake()) {
                for (int i = listOfMoveLists.size() - 1; i >= 0; i--) {
                    listOfMoveLists.get(i).get(0).undoMove();
                }
                return true;
            }

            Set<? extends BuildMove> legalMovesCurrentPlayer;
            legalMovesCurrentPlayer = getMovesOfPlayer(currentPlayer);
            currentPlayer = (currentPlayer % state.getTotalPlayerCount()) + 1;
            if (legalMovesCurrentPlayer.isEmpty()) continue;
            listOfMoveLists.add(getBeamMoves(legalMovesCurrentPlayer));
            listOfMoveLists.get(listOfMoveLists.size() - 1).get(0).doMove();
        }

        //determine the worst value (for us) by averaging the beam values of every opponent

        //init with startPlayer
        double biggestLoss = privValue - averageOfBeamValues(listOfMoveLists.get(0));
        int expandPlayer = startPlayer;
        int expandIndex = 0;

        for (int i = 1; i < listOfMoveLists.size(); i++) {
            double curLoss = averageOfBeamValues(listOfMoveLists.get(i - 1)) - averageOfBeamValues(listOfMoveLists.get(i));
            if (curLoss > biggestLoss) {
                biggestLoss = curLoss;
                expandIndex = i;
                expandPlayer = listOfMoveLists.get(expandIndex).get(0).getPlayerId();
            }
        }
        //expandPlayer gets to expand his moves and we are now at the most promising inner branch
        //meaning: after start of this min node, every opponent has made his most damaging move to our position
        //this state we are now in could really exist and this is now our turn

        if (this.layer < BRSPlusNode.searchDepth - 1) {

            Statistics.getStatistics().enterState(this.layer);

            //first eval this state (just to use what we have already computed) than move back to expand player to let him expand
            BRSPlusNode childNode = new BRSPlusNode(this.layer + 1, !isMaxNode, listOfMoveLists.get(expandIndex).get(0).getType(), listOfMoveLists.get(expandIndex).get(0).getValue(), this.alpha, this.beta, this.watchdog);
            childNode.evaluateNode();

            if (childNode.value < this.value) {
                this.value = childNode.value;

                this.beta = this.value;
                if (BRSPlusNode.enablePruning && this.beta <= this.alpha) {
                    for (int i = listOfMoveLists.size() - 1; i >= 0; i--) {
                        listOfMoveLists.get(i).get(0).undoMove();
                    }
                    return true;
                }
            }

            //go back to expand Player
            for (int i = listOfMoveLists.size() - 1; i >= expandIndex; i--) {
                listOfMoveLists.get(i).get(0).undoMove();
            }

            //now expand moves of expandPlayer - skip first one because tat was just done

            List<? extends BuildMove> expandMoves = listOfMoveLists.get(expandIndex);
            for (int mIndex = 1; mIndex < expandMoves.size(); mIndex++) {
                BuildMove move = expandMoves.get(mIndex);

                if (this.watchdog.isPancake()) {
                    for (int i = expandIndex - 1; i >= 0; i--) {
                        listOfMoveLists.get(i).get(0).undoMove();
                    }
                    return true;
                }

                move.doMove();

                //do moves that are after Expand player but before the next max node
                int afterExpandPlayer = (expandPlayer % state.getTotalPlayerCount()) + 1;
                List<BuildMove> moveStack = new ArrayList<>();
                while (afterExpandPlayer != state.getMe()) {
                    Set<? extends BuildMove> afterExpandMoves = getMovesOfPlayer(afterExpandPlayer);
                    afterExpandPlayer = (afterExpandPlayer % state.getTotalPlayerCount()) + 1;
                    if (afterExpandMoves.isEmpty()) continue;
                    BuildMove temp = worstMoveFromSet(afterExpandMoves);
                    moveStack.add(temp);
                    temp.doMove();
                }

                childNode = new BRSPlusNode(this.layer + 1, !isMaxNode, move.getType(), move.getValue(), this.alpha, this.beta, this.watchdog);
                childNode.evaluateNode();

                //undo moves that are after expandPlayer but before the next max node
                for (int i = moveStack.size() - 1; i >= 0; i--) {
                    moveStack.get(i).undoMove();
                }

                move.undoMove();

                if (childNode.value < this.value) {
                    this.value = childNode.value;
                    this.beta = Math.min(this.beta, this.value);
                    if (BRSPlusNode.enablePruning && this.beta <= this.alpha) {
                        break;
                    }
                }
            }


        } else {
            //lowest layer we want to go

            //first eval this state (just to use what we have already computed) than move to expand player to let him expand
            Statistics.getStatistics().enterMeasuredState(this.layer);
            this.value = evaluateCurrentState(listOfMoveLists.get(expandIndex).get(0).getType());
            this.beta = this.value;

            //go back to expand Player
            for (int i = listOfMoveLists.size() - 1; i >= expandIndex; i--) {
                listOfMoveLists.get(i).get(0).undoMove();
            }
            Statistics.getStatistics().leaveMeasuredState();

            //skip first move because that was already done
            List<? extends BuildMove> expandMoves = listOfMoveLists.get(expandIndex);
            for (int mIndex = 1; mIndex < expandMoves.size(); mIndex++) {
                BuildMove move = expandMoves.get(mIndex);

                if (this.watchdog.isPancake()) {
                    for (int i = expandIndex - 1; i >= 0; i--) {
                        listOfMoveLists.get(i).get(0).undoMove();
                    }
                    return true;
                }


                Statistics.getStatistics().enterMeasuredState(this.layer);

                move.doMove();

                //do moves that are after Expand player but before "the next max node"
                int afterExpandPlayer = (expandPlayer % state.getTotalPlayerCount()) + 1;
                List<BuildMove> moveStack = new ArrayList<>();
                while (afterExpandPlayer != state.getMe()) {
                    Set<? extends BuildMove> afterExpandMoves = getMovesOfPlayer(afterExpandPlayer);
                    afterExpandPlayer = (afterExpandPlayer % state.getTotalPlayerCount()) + 1;
                    if (afterExpandMoves.isEmpty()) continue;
                    BuildMove temp = worstMoveFromSet(afterExpandMoves);
                    moveStack.add(temp);
                    temp.doMove();
                }

                double evalValue = evaluateCurrentState(move.getType());

                //undo moves that are after expandPlayer but before "the next max node"
                for (int i = moveStack.size() - 1; i >= 0; i--) {
                    moveStack.get(i).undoMove();
                }

                move.undoMove();

                if (evalValue < this.value) {
                    this.value = evalValue;
                    this.beta = Math.min(this.beta, this.value);
                    if (BRSPlusNode.enablePruning && this.beta <= this.alpha) {
                        Statistics.getStatistics().leaveMeasuredState();
                        break;
                    }
                }
                Statistics.getStatistics().leaveMeasuredState();
            }

        }

        for (int i = expandIndex - 1; i >= 0; i--) {
            listOfMoveLists.get(i).get(0).undoMove();
        }

        return true;
    }

    private double averageOfBeamValues(List<? extends BuildMove> buildMoves) {
        double value = 0;
        for (BuildMove move : buildMoves) {
            value += move.getValue();
        }
        return value / buildMoves.size();
    }

    private BuildMove worstMoveFromSet(Set<? extends BuildMove> legalMoves) {
        Iterator<? extends BuildMove> legalIterator = legalMoves.iterator();
        BuildMove curWorst = legalIterator.next();
        curWorst.doMove();
        double curWorstVal = evaluateCurrentState(curWorst.getType());
        curWorst.setValue(curWorstVal);
        curWorst.undoMove();

        while (legalIterator.hasNext()) {

            if (this.watchdog.isPancake()) break;
            BuildMove curMove = legalIterator.next();
            curMove.doMove();
            curMove.setValue(evaluateCurrentState(curMove.getType()));
            curMove.undoMove();

            if (curMove.getValue() < curWorstVal) {
                curWorstVal = curMove.getValue();
                curWorst = curMove;
            }
        }

        return curWorst;
    }

    private Set<? extends BuildMove> getMovesOfPlayer(int player) {
        Set<? extends BuildMove> legalMoves;
        legalMoves = LegalMoves.getLegalRegularMoves(state, player);

        if (legalMoves.isEmpty())
            legalMoves = LegalMoves.getLegalOverrideMoves(state, player);

        if (legalMoves.isEmpty()) return Collections.emptySet();

        return legalMoves;
    }


    /**
     * Computes the n best moves, where n is the branching factor (beam width), and orders them in a list.
     * All other moves are discarded.
     *
     * @return the n best moves, ordered
     */
    private List<BuildMove> getBeamMoves(Set<? extends BuildMove> legalMoves) {

        // beamWidth is usually just the branching factor unless very few legal moves were found
        int beamWidth = Math.min(branchingFactor, legalMoves.size());

        //  orders the best legal moves into a list (beam)
        if (isMaxNode) {
            // me
            BuildMove[] beam = new BuildMove[beamWidth];
            double[] values = new double[beamWidth];
            Arrays.fill(values, -Double.MAX_VALUE);

            // check if move belongs to the n best moves (until now)
            // doing some kind of insertion sort
            for (BuildMove move : legalMoves) {
                move.doMove();
                double eval = evaluateCurrentState(move.getType());
                move.undoMove();

                // find position to insert into
                int pos = beamWidth - 1;
                while (pos >= 0 && eval > values[pos]) pos--;

                // move all other elements one down, discard the last
                for (int i = beamWidth - 2; i > pos; i--) {
                    values[i + 1] = values[i];
                    beam[i + 1] = beam[i];
                }

                // insert the new move, if applicable
                if (pos + 1 < beamWidth) {
                    values[pos + 1] = eval;
                    beam[pos + 1] = move;
                }

                move.setValue(eval);

                if (this.watchdog.isPancake()) break;
            }

            return Arrays.asList(beam);

        } else {
            // min Node
            List<BuildMove> worstMoves = new ArrayList<>(beamWidth);

            Iterator<? extends BuildMove> moveIterator = legalMoves.iterator();

            for (int i = 0; i < beamWidth; i++) {
                BuildMove move = moveIterator.next();
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();
                worstMoves.add(move);
                if (this.watchdog.isPancake()) {
                    return worstMoves.subList(0, i + 1);
                }
            }

            worstMoves.sort(new Comparator<BuildMove>() {
                @Override
                public int compare(BuildMove buildMove, BuildMove t1) {
                    return (int) (buildMove.getValue() - t1.getValue());
                }
            });

            while (moveIterator.hasNext()) {
                if (this.watchdog.isPancake()) break;

                BuildMove move = moveIterator.next();
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                if (move.getValue() < worstMoves.get(beamWidth - 1).getValue()) {
                    int index = beamWidth - 1;
                    while (index > 0 && move.getValue() < worstMoves.get(index - 1).getValue()) {
                        index--;
                    }
                    worstMoves.add(0, move);
                }

            }

            return worstMoves.subList(0, beamWidth);

        }

    }

    /**
     * Evaluates the current game state. The heuristic to use is determined by the type of the move that lead
     * to this state.
     *
     * @param type the type of the last executed move
     * @return the evaluation value of the current game state
     * @throws IllegalStateException when called with a move type, that is not <code>REGULAR</code> or <code>OVERRIDE</code>
     */
    private double evaluateCurrentState(Move.Type type) {
        if (type == Move.Type.REGULAR) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe())
                    + MOBILITY_SCALAR * Heuristics.mobility(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusBomb(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusOverride(state, state.getMe())
                    + state.getPlayerFromId(state.getMe()).getStoneCount();
        } else if (type == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe())
                    + state.getPlayerFromId(state.getMe()).getStoneCount();
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
