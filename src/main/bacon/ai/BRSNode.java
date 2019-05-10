package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.StabilityHeuristic;
import bacon.move.*;

import java.util.*;

public class BRSNode {

    private final double STABILITY_SCALAR = 1;
    private final double CLUSTERING_SCALAR = 1;
    private final double MOBILITY_SCALAR = 1;
    private final double BONUS_SCALAR = 1;

    private final int layer;
    private final int searchDepth;
    private final int branchingFactor;
    private List<BuildMove> orderedMoves;
    private BuildMove bestMove;
    private boolean isMaxNode;
    private GameState state;
    /** Type of move that lead to this node */
    private final Move.Type type;
    private double value;

    BRSNode(int layer, int searchDepth, int branchingFactor, boolean isMaxNode, Move.Type type) {
        this.layer = layer;
        this.searchDepth = searchDepth;
        this.branchingFactor = branchingFactor;
        this.isMaxNode = isMaxNode;
        this.type = type;

        this.state = Game.getGame().getCurrentState();
    }

    BuildMove getBestMove() {
        return bestMove;
    }

    void doBRS() {
        double bestValue = -Double.MAX_VALUE;

        this.orderedMoves = computeBestMoves();

        if (orderedMoves == null) {
            this.value = computeEvaluationValue(this.type);
        } else if (this.layer < this.searchDepth - 1) {
            for (BuildMove move : orderedMoves) {
                BRSNode childNode = new BRSNode(this.layer + 1, this.searchDepth, this.branchingFactor, !isMaxNode, move.getType());
                move.doMove();
                childNode.doBRS();
                move.undoMove();

                if (childNode.value > bestValue) {
                    bestValue = childNode.value;
                    bestMove = move;
                }
            }

        } else {
            BuildMove leafMove = orderedMoves.get(0);
            leafMove.doMove();
            bestValue = computeEvaluationValue(leafMove.getType());
            leafMove.undoMove();
        }

        this.value = bestValue;
    }

    /**
     * Computes the n best moves, where n is the branching factor (beam width), and orders them in a list.
     * All other moves are discarded.
     *
     * @return the n best moves, ordered
     */
    private List<BuildMove> computeBestMoves() {

        Set<RegularMove> legalRegularMoves;
        Set<OverrideMove> legalOverrideMoves = null;
        if (isMaxNode) {
            legalRegularMoves = LegalMoves.getLegalRegularMoves(state, state.getMe().getPlayerNumber());
            if (legalRegularMoves.isEmpty())
                legalOverrideMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe().getPlayerNumber());
            if (legalRegularMoves.isEmpty() && legalOverrideMoves.isEmpty()) {
                this.isMaxNode = false;
                legalRegularMoves = new HashSet<>();
                legalOverrideMoves = new HashSet<>();
                for (int i = 1; i <= state.getTotalPlayerCount() ; i++) {
                    if (i == state.getMe().number) continue;
                    legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));
                }
                if (legalRegularMoves.isEmpty()) {
                    for (int i = 1; i <= state.getTotalPlayerCount() ; i++) {
                        if (i == state.getMe().number) continue;
                        legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i));
                    }
                }
            }
        } else {
            legalRegularMoves = new HashSet<>();
            legalOverrideMoves = new HashSet<>();
            for (int i = 1; i <= state.getTotalPlayerCount() ; i++) {
                if (i == state.getMe().number) continue;
                legalRegularMoves.addAll(LegalMoves.getLegalRegularMoves(state, i));
            }
            if (legalRegularMoves.isEmpty()) {
                for (int i = 1; i <= state.getTotalPlayerCount() ; i++) {
                    if (i == state.getMe().number) continue;
                    legalOverrideMoves.addAll(LegalMoves.getLegalOverrideMoves(state, i));
                }
            } if (legalRegularMoves.isEmpty() && legalOverrideMoves.isEmpty()) {
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

        // me
        if (isMaxNode) {
            BuildMove[] bestMoves = new BuildMove[this.branchingFactor];
            double[] values = new double[this.branchingFactor];
            Arrays.fill(values, -Double.MAX_VALUE);

            // check if tile belongs to the n best moves (until now)
            // doing some kind of insertion sort
            for (BuildMove move : legalMoves) {
                move.doMove();
                double eval = computeEvaluationValue(move.getType());
                move.undoMove();

                // find position to insert into
                int pos = this.branchingFactor - 1;
                while (pos >= 0 && eval > values[pos]) pos--;

                // move all other elements one down, discard the last
                for (int i = branchingFactor - 2; i > pos; i--) {
                    values[i + 1] = values[i];
                    bestMoves[i + 1] = bestMoves[i];
                }

                // insert the new move, if applicable
                if (pos + 1 < this.branchingFactor) {
                    values[pos + 1] = eval;
                    bestMoves[pos + 1] = move;
                }
            }

            return Arrays.asList(bestMoves);
        }
        // TODO "all" other players (since we're doing BRS)
        else {
            BuildMove[] worstMoves = new BuildMove[this.branchingFactor];
            double[] values = new double[this.branchingFactor];
            Arrays.fill(values, Double.MAX_VALUE);

            // check if tile belongs to the n best moves (until now)
            // doing some kind of insertion sort
            for (BuildMove move : legalMoves) {
                move.doMove();
                double eval = computeEvaluationValue(move.getType());
                move.undoMove();

                // find position to insert into
                int pos = this.branchingFactor - 1;
                while (pos >= 0 && eval < values[pos]) pos--;

                // move all other elements one down, discard the last
                for (int i = branchingFactor - 2; i > pos; i--) {
                    values[i + 1] = values[i];
                    worstMoves[i + 1] = worstMoves[i];
                }

                // insert the new move, if applicable
                if (pos + 1 < this.branchingFactor) {
                    values[pos + 1] = eval;
                    worstMoves[pos + 1] = move;
                }

            }

            return Arrays.asList(worstMoves);
        }

    }

    private double computeEvaluationValue(Move.Type type) {

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
