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

public class BRSNode {

    private final double STABILITY_SCALAR = 1;
    private final double CLUSTERING_SCALAR = 1;
    private final double MOBILITY_SCALAR = 1;
    private final double BONUS_SCALAR = 100;

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

    private static boolean enablePruning;
    private double alpha;
    private double beta;

    /**
     * External call constructor for game tree root
     *
     * @param depth           maximum depth to be searched
     * @param branchingFactor maximum branching factor at each node
     * @param enablePruning   set to true if alpha-beta pruning should be applied
     * @param watchdog        Watchdog timer that triggers when time is running out
     */
    public BRSNode(int depth, int branchingFactor, boolean enablePruning, PancakeWatchdog watchdog) {
        BRSNode.searchDepth = depth;
        BRSNode.branchingFactor = branchingFactor;
        BRSNode.enablePruning = enablePruning;

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
     * @param watchdog        Watchdog timer that triggers when time is running out
     */
    private BRSNode(int layer, boolean isMaxNode, Move.Type type, double alpha, double beta, PancakeWatchdog watchdog) {
        this.layer = layer;
        this.isMaxNode = isMaxNode;
        this.type = type;
        this.alpha = alpha;
        this.beta = beta;

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
     * Evaluates this node using Best Reply Search. The results are saved in the fields of the instance.
     * Determines child nodes and recursively executes this function, if depth limit is not reached and further valid
     * moves can be done.
     */
    public void evaluateNode() {
        // computes the best moves and orders them in a list as the beam for the beam search
        List<BuildMove> beam = computeBeam();

        // initiates node value with -infinity for Max-Nodes and +infinity for Min-Nodes
        this.value = this.isMaxNode ? -Double.MAX_VALUE : Double.MAX_VALUE;

        // no move is available, return value of current game state directly
        if (beam == null) {
            Statistics.getStatistics().enterState(layer);
            this.value = evaluateCurrentState(this.type);
        }

        // do beam search: go through each move in beam, construct and evaluate child nodes (recursion)
        else if (this.layer < BRSNode.searchDepth - 1) {
            Statistics.getStatistics().enterState(this.layer);
            for (BuildMove move : beam) {
                BRSNode childNode = new BRSNode(this.layer + 1, !isMaxNode, move.getType(), this.alpha, this.beta, this.watchdog);
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (this.isMaxNode) {
                    if (childNode.value > this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;

                        this.alpha = this.value;
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                } else {
                    if (childNode.value < this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;

                        this.beta = this.value;
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                }

                if (this.watchdog.isPancake())
                    return;
            }

        }

        // in this case a node is one layer above leaf nodes, i.e. we only need to return the value of the first beam entry
        else {
            Statistics.getStatistics().enterMeasuredState(this.layer);
            BuildMove leafMove = beam.get(0);
            this.value = leafMove.getValue();
            this.bestMove = leafMove;

            // updates the value of alpha and beta
            if (this.isMaxNode) {
                this.alpha = Math.max(this.alpha, this.value);
            } else {
                this.beta = Math.min(this.beta, this.value);
            }
            Statistics.getStatistics().leaveMeasuredState();
        }

    }

    /**
     * Computes the n best moves, where n is the branching factor (beam width), and orders them in a list.
     * All other moves are discarded.
     *
     * @return the n best moves, ordered
     */
    private List<BuildMove> computeBeam() {

        // saves legal moves temporary storage
        Set<? extends BuildMove> legalMoves;
        if (isMaxNode) {
            legalMoves = getMaxMoves();
            if (legalMoves.isEmpty()) {
                this.isMaxNode = false;
                legalMoves = getMinMoves();
            }
        } else {
            legalMoves = getMinMoves();
            if (legalMoves.isEmpty()) {
                this.isMaxNode = true;
                legalMoves = getMaxMoves();
            }
        }

        // return null if no build moves are possible => first phase ends
        if (legalMoves.isEmpty())
            return null;

        // beamWidth is usually just the branching factor unless very few legal moves were found
        int beamWidth = Math.min(branchingFactor, legalMoves.size());

        // me
        if (isMaxNode) {    //  orders the best legal moves into a list (beam)
            PriorityQueue<BuildMove> minHeap = new PriorityQueue<>(beamWidth, Comparator.comparing(Move::getValue));

            // check if tile belongs to the n best moves (until now)
            for (BuildMove move : legalMoves) {
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                if (minHeap.size() < beamWidth)
                    // insert move if not reached beam width
                    minHeap.add(move);
                else if (minHeap.peek() != null && minHeap.peek().getValue() < move.getValue()) {
                    // otherwise, if new move ist better, replace first (worst) move with new move
                    minHeap.remove();
                    minHeap.add(move);
                }
            }

            return new ArrayList<>(minHeap);
        }
        else {
            // "all" other players (since we're doing BRS)
            PriorityQueue<BuildMove> maxHeap = new PriorityQueue<>(beamWidth,
                    Comparator.comparing(Move::getValue).reversed());

            // check if tile belongs to the n best moves (until now)
            for (BuildMove move : legalMoves) {
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                if (maxHeap.size() < beamWidth)
                    // insert move if not reached beam width
                    maxHeap.add(move);
                else if (maxHeap.peek() != null && maxHeap.peek().getValue() > move.getValue()) {
                    // otherwise, if new move ist worse, replace first (better) move with new move
                    maxHeap.remove();
                    maxHeap.add(move);
                }
            }

            return new ArrayList<>(maxHeap);
        }

    }

    /**
     * Returns all legal {@link BuildMove}s the max player can do.
     *
     * @return a <code>Set</code> of {@link BuildMove}s the max player can do
     */
    private Set<? extends BuildMove> getMaxMoves() {
        // Assign either regular moves or override moves to legalMoves since we are considering either one or the other
        Set<? extends BuildMove> legalMoves;
        legalMoves = LegalMoves.getLegalRegularMoves(state, state.getMe(), this.watchdog);
        if (this.watchdog.isPancake())
            return legalMoves;
        if (legalMoves.isEmpty()) // regular moves are preferred, only if the search turns up empty do we consider override moves
            legalMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe(), this.watchdog);

        return legalMoves;
    }

    /**
     * Returns all legal {@link BuildMove}s any min player can do.
     *
     * @return a <code>Set</code> of {@link BuildMove}s the min players can do
     */
    private Set<? extends BuildMove> getMinMoves() {
        Set<RegularMove> legalRegularMoves;
        Set<OverrideMove> legalOverrideMoves;

        legalRegularMoves = new HashSet<>();
        legalOverrideMoves = new HashSet<>();
        for (int i = 1; i <= state.getTotalPlayerCount(); i++) { // Add all regular moves of other players to storage (definition of BRS)
            if (i == state.getMe()) continue;
            legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i, this.watchdog));
            if (this.watchdog.isPancake())
                return legalRegularMoves;
        }
        if (legalRegularMoves.isEmpty()) { // If no regular moves exist, add all override moves of other players to storage instead
            for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                if (i == state.getMe()) continue;
                legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i, this.watchdog));
                if (this.watchdog.isPancake())
                    return legalOverrideMoves;
            }
        }

        // Return either regular moves or override moves since we are considering either one or the other
        if (!legalRegularMoves.isEmpty()) return legalRegularMoves;
        else if (!legalOverrideMoves.isEmpty()) return legalOverrideMoves;
        else return Collections.emptySet();
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
                    + MOBILITY_SCALAR * Heuristics.mobility(state, state.getMe(), this.watchdog)
                    + BONUS_SCALAR * Heuristics.bonusBomb(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusOverride(state, state.getMe());
        } else if (type == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe());
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
