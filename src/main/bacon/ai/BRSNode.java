package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.PancakeWatchdog;
import bacon.ai.heuristics.StabilityHeuristic;
import bacon.move.BuildMove;
import bacon.move.Move;
import bacon.move.OverrideMove;
import bacon.move.RegularMove;

import java.util.*;

/**
 * A node in the search tree.
 * <p>
 * This is an implementation of the <i>Best Reply Search</i>, where min and max layers alternate regardless of the
 * player count. In the min layer only one move, the best that any enemy player can do, is evaluated.
 */
class BRSNode {

    /*
    Constant scalar values for the computation of evaluation values.
     */
    private static final double STABILITY_SCALAR = 1;
    private static final double MOBILITY_SCALAR = 10;
    private static final double BONUS_SCALAR = 100;

    /**
     * The maximum search depth.
     */
    private static int searchDepth;
    /**
     * The maximum branching factor. This is the <i>beam width</i> for beam search. {@code 0} represents infinity.
     */
    private static int branchingFactor;
    private static boolean enablePruning;
    private static boolean enableSorting;
    private static boolean aspWindowEnabled;
    private static double aspWindowAlpha;
    private static double aspWindowBeta;

    /**
     * The maximum depth that was reached in the search.
     */
    private static int reachedDepth;

    // Statistics needed for determining aspiration window size for the next BRS-iteration
    private static List<Double> stateValues;
    private static double stateAvg;
    private static double stateStdv;

    /**
     * The layer in the search tree this node is in.
     */
    private final int layer;
    /**
     * The {@link Move.Type} of move that lead to this node.
     */
    private final Move.Type type;
    /**
     * The {@link GameState} the node is in.
     */
    private final GameState state;
    /**
     * The watchdog timer that triggers, when time is about to run out.
     */
    private final PancakeWatchdog watchdog;
    /**
     * The best move that was found so far in all child nodes.
     */
    private BuildMove bestMove;
    /**
     * {@code true} if this node is a max node, {@code false} if this node is a min node.
     */
    private boolean isMaxNode;
    /**
     * The evaluation value of this node.
     */
    private double value;
    /**
     * The alpha value used by alpha-beta-pruning.
     */
    private double alpha;
    /**
     * The beta value used by alpha-beta-pruning.
     */
    private double beta;
    /**
     * {@code true} if search within aspiration window was successful, {@code false} otherwise.
     */
    private boolean windowSuccess;

    /**
     * Creates a new {@code BRSNode} that serves as the search tree root.
     *
     * @param depth            maximum depth to be searched
     * @param branchingFactor  maximum branching factor at each node
     * @param enablePruning    set to {@code true} if alpha-beta pruning should be applied
     * @param enableSorting    set to {@code true} when move sorting should be used
     * @param aspWindowEnabled set to {@code true} when aspiration window is on
     * @param alpha            alpha value passed down from ai
     * @param beta             beta value passed down from ai
     * @param watchdog         a watchdog timer that triggers when time is running out
     */
    BRSNode(int depth, int branchingFactor, boolean enablePruning, boolean enableSorting, boolean aspWindowEnabled, double alpha, double beta, PancakeWatchdog watchdog) {
        BRSNode.searchDepth = depth;
        BRSNode.branchingFactor = branchingFactor;
        BRSNode.enablePruning = enablePruning;
        BRSNode.enableSorting = enableSorting;
        BRSNode.aspWindowEnabled = aspWindowEnabled;
        BRSNode.aspWindowAlpha = alpha;
        BRSNode.aspWindowBeta = beta;
        BRSNode.stateAvg = 0;
        BRSNode.stateStdv = 0;
        BRSNode.stateValues = new ArrayList<>();
        BRSNode.reachedDepth = 0;

        this.layer = 0;
        this.isMaxNode = true;
        this.type = null;
        this.alpha = -Double.MAX_VALUE;
        this.beta = Double.MAX_VALUE;
        this.windowSuccess = false;

        this.watchdog = watchdog;
        this.state = Game.getGame().getCurrentState();
    }

    /**
     * Creates a new {@code BRSNode} that serves as a child node.
     *
     * @param layer     layer this node is part of. The root is on layer zero.
     * @param isMaxNode set to {@code true} if max player is in turn at this node
     * @param type      the {@link Move.Type} of the move that led to this node
     * @param alpha     the current alpha value
     * @param beta      the current beta value
     * @param watchdog  a watchdog timer that triggers when time is running out
     */
    private BRSNode(int layer, boolean isMaxNode, Move.Type type, double alpha, double beta, PancakeWatchdog watchdog) {
        this.layer = layer;
        this.isMaxNode = isMaxNode;
        this.type = type;
        this.alpha = alpha;
        this.beta = beta;
        this.windowSuccess = false;

        this.watchdog = watchdog;
        this.state = Game.getGame().getCurrentState();
    }

    /**
     * Returns the maximum depth that was reached in the search.
     *
     * @return the maximum reached search depth
     */
    static int getMaximumReachedDepth() {
        return BRSNode.reachedDepth;
    }

    /**
     * Returns the best move found in all child nodes.
     *
     * @return best move that was found
     */
    BuildMove getBestMove() {
        return bestMove;
    }

    /**
     * Analysis of node values of layer 1 nodes (average and standard deviation)
     */
    void aspWindow() {
        if (stateValues.size() != 0) {
            double sum = 0;
            for (Double value : stateValues) {
                sum += value;
            }

            stateAvg = sum / stateValues.size();

            double stdvSum = 0;
            for (Double value : stateValues) {
                stdvSum += (value - stateAvg) * (value - stateAvg);
            }
            stateStdv = Math.pow((stdvSum / stateValues.size()), 0.5);
        }
    }

    /**
     * Aspiration Window value for alpha for the next BRS-iteration; if no nodes were found in layer 1, use default value
     *
     * @return alpha value
     */
    double getAspWindowAlpha() {
        if (stateStdv == 0) {
            return -Double.MAX_VALUE;
        }
        return this.value - 5 * stateStdv;
    }

    /**
     * Aspiration Window value for beta for the next BRS-iteration; if no nodes were found in layer 1, use default value
     *
     * @return beta value
     */
    double getAspWindowBeta() {
        if (stateStdv == 0) {
            return Double.MAX_VALUE;
        }
        return this.value + 5 * stateStdv;
    }

    /**
     * Evaluates this node using <i>Best Reply Search</i>.
     * <p>
     * The results are saved in the fields of the instance.
     * Determines child nodes and recursively executes this function, if depth limit is not reached and further valid
     * moves can be done.
     * <p>
     * This method does <i>time panics</i> if time is running out.
     */
    void evaluateNode() {

        Set<? extends BuildMove> legalMoves = getLegalMoves();

        // initiates node value as aspiration window boundaries or +/-infinity if aspiration window is OFF
        if (aspWindowEnabled) this.value = this.isMaxNode ? aspWindowAlpha : aspWindowBeta;
        else this.value = this.isMaxNode ? -Double.MAX_VALUE : Double.MAX_VALUE;

        // no move is available, return value of current game state directly; counts as aspiration window success
        if (legalMoves.isEmpty()) {
            Statistics.getStatistics().enterState(layer);
            this.value = evaluateCurrentState(this.type);
            this.windowSuccess = true;
        } else if (this.layer < BRSNode.searchDepth - 1) {
            BRSNode.reachedDepth = Integer.max(BRSNode.reachedDepth, this.layer + 1);
            // do beam search: go through each move in beam, construct and evaluate child nodes (recursion)

            List<? extends BuildMove> moves;
            if (BRSNode.enableSorting && BRSNode.branchingFactor > 0)
                moves = getBeamMoves(legalMoves);
            else if (BRSNode.enableSorting)
                moves = getOrderedMoves(legalMoves);
            else
                moves = new ArrayList<>(legalMoves);

            Statistics.getStatistics().enterState(this.layer);

            for (BuildMove move : moves) {

                if (this.watchdog.isPancake()) {
                    this.bestMove = null;
                    break;
                }

                BRSNode childNode = new BRSNode(this.layer + 1, !isMaxNode, move.getType(), this.alpha, this.beta, this.watchdog);
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (this.isMaxNode) {
                    if (childNode.value > this.value && childNode.windowSuccess) {
                        this.value = childNode.value;
                        this.bestMove = move;
                        this.windowSuccess = true;

                        if (this.value > this.alpha) {
                            this.alpha = this.value;
                        }

                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                } else {
                    if (childNode.value < this.value && childNode.windowSuccess) {
                        this.value = childNode.value;
                        this.bestMove = move;
                        this.windowSuccess = true;

                        if (this.value < this.beta) {
                            this.beta = this.value;
                        }

                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                }
            }

        } else {
            BRSNode.reachedDepth = Integer.max(BRSNode.reachedDepth, this.layer + 1);

            for (BuildMove move : legalMoves) {

                Statistics.getStatistics().enterMeasuredState(this.layer);
                move.doMove();
                move.setValue(evaluateCurrentState(move.getType()));
                move.undoMove();

                if (this.layer == 0) stateValues.add(move.getValue());

                // update node value, bestMove, alpha and beta; break (prune) in case beta <= alpha
                if (this.isMaxNode) {
                    if (move.getValue() > this.value) {
                        this.value = move.getValue();
                        this.bestMove = move;
                        this.windowSuccess = true;

                        if (this.value > alpha) {
                            this.alpha = this.value;
                        }
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            Statistics.getStatistics().leaveMeasuredState();
                            break;
                        }
                    }
                } else {
                    if (move.getValue() < this.value) {
                        this.value = move.getValue();
                        this.bestMove = move;
                        this.windowSuccess = true;

                        if (this.value < beta) {
                            this.beta = this.value;
                        }
                        if (BRSNode.enablePruning && this.beta <= this.alpha) {
                            Statistics.getStatistics().leaveMeasuredState();
                            break;
                        }
                    }
                }
                Statistics.getStatistics().leaveMeasuredState();

                if (this.watchdog.isPancake()) {
                    break;
                }

            }
        }

        // Store the layer 1 node values for aspiration window size
        if (this.layer == 1 && this.windowSuccess) {
            stateValues.add(this.value);
        }
    }

    /**
     * Gets the legal moves that are possible from this node.
     * <p>
     * This method returns a move for the max or min player, depending on the {@code isMaxNode} flag.
     * If no move is possible for one of them, the flag is switched and moves are returned from the other player.
     * <p>
     * Returns an empty set if no moves can be done by any player.
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

    /**
     * Evaluates and orders the given set of legal moves.
     * <p>
     * Moves are ordered in descending order for max nodes and in ascending order for min nodes.
     * <p>
     * This method does <i>time panics</i> if time is running out.
     *
     * @param legalMoves a set of legal moves to evaluate and order
     * @return an ordered {@link List} of legal moves
     */
    private List<BuildMove> getOrderedMoves(Set<? extends BuildMove> legalMoves) {
        List<BuildMove> orderedMoves = new ArrayList<>(legalMoves);
        // rate every move
        for (BuildMove move : orderedMoves) {
            move.doMove();
            move.setValue(evaluateCurrentState(move.getType()));
            move.undoMove();
            if (watchdog.isPancake()) break;
        }

        // order moves by value
        if (isMaxNode)
            orderedMoves.sort(Comparator.comparing(Move::getValue).reversed());
        else orderedMoves.sort(Comparator.comparing(Move::getValue));

        return orderedMoves;
    }

    /**
     * Evaluates the given moves and executes a beam search on them.
     * <p>
     * This method only returns the {@code k} best moves and discards all other, where {@code k} is the branching factor.
     * <p>
     * This method does <i>time panics</i> if time is running out.
     *
     * @param legalMoves a set of legal moves to evaluate and order
     * @return an ordered {@link List} of legal moves
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

                if (this.watchdog.isPancake()) break;
            }

            return Arrays.asList(beam);

        } else {
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

                if (this.watchdog.isPancake()) break;
            }

            return Arrays.asList(worstMoves);

        }

    }

    /**
     * Returns all legal {@link BuildMove}s the max player can do.
     *
     * @return a {@link Set} of {@link BuildMove}s the max player can do
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
     * @return a {@link Set} of {@link BuildMove}s the min players can do
     */
    private Set<? extends BuildMove> getMinMoves() {
        Set<RegularMove> legalRegularMoves;
        Set<OverrideMove> legalOverrideMoves;

        legalRegularMoves = new HashSet<>();
        legalOverrideMoves = new HashSet<>();
        for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
            // Add all regular moves of other players to storage (definition of BRS)
            if (i == state.getMe()) continue;
            legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));

        }
        if (legalRegularMoves.isEmpty()) {
            // If no regular moves exist, add all override moves of other players to storage instead
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
     * Evaluates the current {@link GameState}.
     * <p>
     * The heuristic to use is determined by the {@link Move.Type} of the move that lead to this state.
     *
     * @param type the {@code Type} of the last executed move
     * @return the evaluation value of the current {@code GameState}
     * @throws IllegalStateException when called with a move type, that is not {@link Move.Type#REGULAR} or {@link Move.Type#OVERRIDE}
     */
    private double evaluateCurrentState(Move.Type type) {
        if (type == Move.Type.REGULAR) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe())
                    + MOBILITY_SCALAR * Heuristics.mobility(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusBomb(state, state.getMe())
                    + BONUS_SCALAR * Heuristics.bonusOverride(state, state.getMe())
                    + Heuristics.relativeStoneCount(state);
        } else if (type == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe())
                    + Heuristics.relativeStoneCount(state);
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
