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
    private List<BuildMove> beam;
    private BuildMove bestMove;
    private boolean isMaxNode;
    private GameState state;
    /**
     * Type of move that lead to this node
     */
    private final Move.Type type;
    private double value;

    private static boolean pruning;
    private double alpha;
    private double beta;

    /**
     * External call constructor for game tree root
     *
     * @param depth     maximum depth to be searched
     * @param branching maximum branching factor at each node
     * @param prune     set to true if alpha-beta pruning should be applied
     */
    public BRSNode(int depth, int branching, boolean prune) {
        this.layer = 0;
        searchDepth = depth;
        branchingFactor = branching;
        this.isMaxNode = true;
        this.type = null;
        pruning = prune;
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

    public BuildMove getBestMove() {
        return bestMove;
    }

    public void evaluateNode() {
        this.value = -Double.MAX_VALUE;
        if (!this.isMaxNode) this.value = Double.MAX_VALUE;

        this.beam = computeBeam();

        if (beam == null) {
            this.value = evaluateCurrentState(this.type);

            if (this.isMaxNode) {
                this.alpha = Math.max(this.alpha, this.value);
            } else {
                this.beta = Math.min(this.beta, this.value);
            }

        } else if (this.layer < searchDepth - 1) {
            for (BuildMove move : beam) {
                BRSNode childNode = new BRSNode(this.layer + 1, !isMaxNode, move.getType(), this.alpha, this.beta);
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();

                if (this.isMaxNode) {
                    if (childNode.value > this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;

                        this.alpha = this.value;
                        if (pruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                } else {
                    if (childNode.value < this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;

                        this.beta = this.value;
                        if (pruning && this.beta <= this.alpha) {
                            break;
                        }
                    }
                }
            }

        } else {
            BuildMove leafMove = beam.get(0);
            leafMove.doMove();
            this.value = evaluateCurrentState(leafMove.getType());
            leafMove.undoMove();
            this.bestMove = leafMove;

            if (this.isMaxNode) {
                this.alpha = Math.max(this.alpha, this.value);
            } else {
                this.beta = Math.min(this.beta, this.value);
            }
        }

    }

    /**
     * Computes the n best moves, where n is the branching factor (beam width), and orders them in a list.
     * All other moves are discarded.
     *
     * @return the n best moves, ordered
     */
    private List<BuildMove> computeBeam() {

        Set<RegularMove> legalRegularMoves = null;
        Set<OverrideMove> legalOverrideMoves = null;
        if (isMaxNode) {
            legalRegularMoves = LegalMoves.getLegalRegularMoves(state, state.getMe().getPlayerNumber());
            if (legalRegularMoves.isEmpty())
                legalOverrideMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe().getPlayerNumber());
            if (legalRegularMoves.isEmpty() && legalOverrideMoves.isEmpty()) {
                this.isMaxNode = false;
                legalRegularMoves = new HashSet<>();
                legalOverrideMoves = new HashSet<>();
                for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                    if (i == state.getMe().number) continue;
                    legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));
                }
                if (legalRegularMoves.isEmpty()) {
                    for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                        if (i == state.getMe().number) continue;
                        legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i));
                    }
                }
            }
        } else {
            legalRegularMoves = new HashSet<>();
            legalOverrideMoves = new HashSet<>();
            for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                if (i == state.getMe().number) continue;
                legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));
            }
            if (legalRegularMoves.isEmpty()) {
                for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
                    if (i == state.getMe().number) continue;
                    legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i));
                }
            }
            if (legalRegularMoves.isEmpty() && legalOverrideMoves.isEmpty()) {
                this.isMaxNode = true;
                legalRegularMoves = LegalMoves.getLegalRegularMoves(state, state.getMe().getPlayerNumber());
                if (legalRegularMoves.isEmpty())
                    legalOverrideMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe().getPlayerNumber());
            }
        }

        Set<? extends BuildMove> legalMoves = null;
        if (!legalRegularMoves.isEmpty()) legalMoves = legalRegularMoves;
        else if (!legalOverrideMoves.isEmpty()) legalMoves = legalOverrideMoves;
            // return null if no build moves are possible => first phase ends
        else return null;

        int beamWidth = Math.min(branchingFactor, legalMoves.size());

        // me
        if (isMaxNode) {
            BuildMove[] beam = new BuildMove[beamWidth];
            double[] values = new double[beamWidth];
            Arrays.fill(values, -Double.MAX_VALUE);

            // TODO: Use PriorityQueue here instead of custom made insertion sort algorithm
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
            }

            return Arrays.asList(beam);
        } else {
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

            }

            return Arrays.asList(worstMoves);
        }

    }

    private double evaluateCurrentState(Move.Type type) {

        if (type == Move.Type.REGULAR) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe().number)
                    + MOBILITY_SCALAR * Heuristics.mobility(state, state.getMe().number)
                    + BONUS_SCALAR * Heuristics.bonusBomb(state, state.getMe().number)
                    + BONUS_SCALAR * Heuristics.bonusOverride(state, state.getMe().number);
        } else if (type == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, state.getMe().number);
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
