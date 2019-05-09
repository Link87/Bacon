package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.StabilityHeuristic;
import bacon.move.BonusRequest;
import bacon.move.BuildMove;
import bacon.move.Move;
import bacon.move.RegularMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BRSNode {

    private final double STABILITY_SCALAR = 1;
    private final double CLUSTERING_SCALAR = 1;
    private final double MOBILITY_SCALAR = 1;
    private final double BONUS_SCALAR = 1;

    private final int layer;
    private final int searchDepth;
    private final int branchingFactor;
    private List<BuildMove> orderedMoves;
    private List<BRSNode> children;
    private final boolean isMaxNode;
    private GameState state;
    private double value;

    BRSNode(int layer, int searchDepth, int branchingFactor, boolean isMaxNode) {
        this.layer = layer;
        this.searchDepth = searchDepth;
        this.branchingFactor = branchingFactor;
        this.isMaxNode = isMaxNode;

        this.state = Game.getGame().getCurrentState();
        this.children = new ArrayList<>();
    }

    Move getBestMove() {
        return orderedMoves.get(0);
    }

    double doBRS() {
        double bestValue = -Double.MAX_VALUE;

        this.orderedMoves = computeBestMoves();

        for (BuildMove move : orderedMoves) {
            // TODO recursive steps
        }

        this.value = bestValue;
        return bestValue;
    }

    /**
     * Computes the n best moves, where n is the branching factor (beam width), and orders them in a list.
     * All other moves are discarded.
     *
     * @return the n best moves, ordered
     */
    private List<BuildMove> computeBestMoves() {

        Set<? extends BuildMove> legalMoves = null;
        if (isMaxNode) {
            legalMoves = LegalMoves.getLegalRegularMoves(state, state.getMe().getPlayerNumber());
            if (legalMoves.isEmpty())
                legalMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe().getPlayerNumber());
            if (legalMoves.isEmpty()) {
                // TODO check min nodes
            }
        } else {
            // TODO check min nodes
        }

        // me
        if (isMaxNode) {
            BuildMove[] bestMoves = new BuildMove[this.branchingFactor];
            double[] values = new double[this.branchingFactor];
            Arrays.fill(values, -Double.MAX_VALUE);

            // check if tile belongs to the n best moves (until now)
            // doing some kind of insertion sort
            for (BuildMove move : legalMoves) {
                move.doMove();

                // TODO write evaluation method
                double eval = computeEvaluationValue(move);

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

                move.undoMove();
            }

            return Arrays.asList(bestMoves);
        }
        // TODO "all" other players (since we're doing BRS)
        else {
            /*for (BuildMove move : legalMoves) {
                move.doMove();

                // .. eval

                move.undoMove();
            }*/
            return null;
        }

    }

    private double computeEvaluationValue(BuildMove move) {

        if (move.getType() == Move.Type.REGULAR) {
            if (((RegularMove) move).getRequest().type == BonusRequest.Type.NONE) {
                return STABILITY_SCALAR * StabilityHeuristic.stability(state, move.getPlayer().number)
                        + MOBILITY_SCALAR * Heuristics.mobility(state, move.getPlayer().number);
            }
            else if (((RegularMove) move).getRequest().type == BonusRequest.Type.BOMB_BONUS) {
                return STABILITY_SCALAR * StabilityHeuristic.stability(state, move.getPlayer().number)
                        + MOBILITY_SCALAR * Heuristics.mobility(state, move.getPlayer().number)
                        + BONUS_SCALAR * Heuristics.bonusBomb(state, move.getPlayer().number);
            }
            else if (((RegularMove) move).getRequest().type == BonusRequest.Type.OVERRIDE_BONUS) {
                return STABILITY_SCALAR * StabilityHeuristic.stability(state, move.getPlayer().number)
//                            + CLUSTERING_SCALAR * Heuristics.clustering(evalState, meNumbr)
                        + MOBILITY_SCALAR * Heuristics.mobility(state, move.getPlayer().number)
                        + BONUS_SCALAR * Heuristics.bonusOverride(state, move.getPlayer().number);
            }
        } else if (move.getType() == Move.Type.OVERRIDE) {
            return STABILITY_SCALAR * StabilityHeuristic.stability(state, move.getPlayer().number);
        }

        throw new IllegalStateException("Cannot evaluate bomb heuristic in brs tree. I shouldn't be here...");
    }
}
