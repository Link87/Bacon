package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
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
    /**
     * Type of move that lead to this node
     */
    private final Move.Type type;
    private double value;

    private static boolean enablePruning;
    private static boolean enableSorting;
    private double alpha;
    private double beta;

    /**
     * External call constructor for game tree root
     *
     * @param depth           maximum depth to be searched
     * @param branchingFactor maximum branching factor at each node
     * @param enablePruning   set to true if alpha-beta pruning should be applied
     */
    public BRSNode(int depth, int branchingFactor, boolean enablePruning, boolean enableSorting) {
        BRSNode.searchDepth = depth;
        BRSNode.branchingFactor = branchingFactor;
        BRSNode.enablePruning = enablePruning;
        BRSNode.enableSorting = enableSorting;

        this.layer = 0;
        this.isMaxNode = true;
        this.type = null;
        this.alpha = -Double.MAX_VALUE;
        this.beta = Double.MAX_VALUE;

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
     */
    private BRSNode(int layer, boolean isMaxNode, Move.Type type, double alpha, double beta) {
        this.layer = layer;
        this.isMaxNode = isMaxNode;
        this.type = type;
        this.alpha = alpha;
        this.beta = beta;

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

        Set<? extends BuildMove> legalMoves = getLegalMoves();

        // initiates node value with -infinity for Max-Nodes and +infinity for Min-Nodes
        this.value = this.isMaxNode ? -Double.MAX_VALUE : Double.MAX_VALUE;

        // no move is available, return value of current game state directly
        if (legalMoves.isEmpty()) {
            Statistics.getStatistics().enterState(layer);
            this.value = evaluateCurrentState(this.type);
        } else if (this.layer < BRSNode.searchDepth - 1) {
            // do beam search: go through each move in beam, construct and evaluate child nodes (recursion)

            List<? extends BuildMove> moves = null;
            if (BRSNode.enableSorting && BRSNode.branchingFactor > 0)
                moves = getBeamMoves(legalMoves);
            else if (BRSNode.enableSorting)
                moves = getOrderedMoves(legalMoves);

            Statistics.getStatistics().enterState(this.layer);
            for (BuildMove move : moves) {
                BRSNode childNode = new BRSNode(this.layer + 1, !isMaxNode, move.getType(), this.alpha, this.beta);
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
            }

        } else {
            Statistics.getStatistics().enterMeasuredState(this.layer);
            for (BuildMove move : legalMoves) {
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (this.isMaxNode) {
                    if (move.getValue() > this.value) {
                        this.value = move.getValue();
                        this.bestMove = move;
                        this.alpha = Math.max(this.alpha, this.value);
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                } else {
                    if (move.getValue() < this.value) {
                        this.value = move.getValue();
                        this.bestMove = move;
                        this.beta = Math.min(this.beta, this.value);
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                }
            }
            Statistics.getStatistics().leaveMeasuredState();
        }
    }

    /**
     * Gets the legal Moves that are possible for the max or min player, depending on the <code>isMaxNode</code> flag.
     * If no move is possible for one of them, the flag is switched and moves are returned from the other player.
     *
     * @return a set containing valid moves, empty when no valid moves are possible for neither of the players
     */
    private Set<? extends BuildMove> getLegalMoves() {
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

        // return empty immutable set if no build moves are possible => first phase ends
        if (legalMoves.isEmpty())
            return Collections.emptySet();
        return legalMoves;
    }

    private List<BuildMove> getOrderedMoves(Set<? extends BuildMove> legalMoves) {
        List<BuildMove> orderedMoves = new ArrayList<>(legalMoves);
        // rate every move
        for (BuildMove move : orderedMoves) {
            move.doMove();
            move.setValue(evaluateCurrentState(move.getType()));
            move.undoMove();
        }

        // order moves by value
        if (isMaxNode)
            orderedMoves.sort(Comparator.comparing(Move::getValue).reversed());
        else orderedMoves.sort(Comparator.comparing(Move::getValue));

        return orderedMoves;
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

            // check if tile belongs to the n best moves (until now)
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
            }

            return Arrays.asList(beam);

        }
        else {
            // "all" other players (since we're doing BRS)
            BuildMove[] worstMoves = new BuildMove[beamWidth];
            double[] values = new double[beamWidth];
            Arrays.fill(values, Double.MAX_VALUE);

            // check if tile belongs to the n best moves (until now)
            // doing some kind of insertion sort
            for (BuildMove move : legalMoves) {
                move.doMove();
                double eval = evaluateCurrentState(move.getType());
                move.undoMove();

                // find position to insert into
                int pos = beamWidth - 1;
                while (pos >= 0 && eval < values[pos]) pos--;

                // move all other elements one down, discard the last
                for (int i = beamWidth - 2; i > pos; i--) {
                    values[i + 1] = values[i];
                    worstMoves[i + 1] = worstMoves[i];
                }

                // insert the new move, if applicable
                if (pos + 1 < beamWidth) {
                    values[pos + 1] = eval;
                    worstMoves[pos + 1] = move;
                }

                move.setValue(eval);
            }

            return Arrays.asList(worstMoves);

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
        legalMoves = LegalMoves.getLegalRegularMoves(state, state.getMe());
        if (legalMoves.isEmpty()) // regular moves are preferred, only if the search turns up empty do we consider override moves
            legalMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe());

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
            legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));
        }
        if (legalRegularMoves.isEmpty()) { // If no regular moves exist, add all override moves of other players to storage instead
            for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                if (i == state.getMe()) continue;
                legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i));
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
                    + MOBILITY_SCALAR * Heuristics.mobility(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusBomb(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusOverride(state, state.getMe());
        } else if (type == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe());
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
