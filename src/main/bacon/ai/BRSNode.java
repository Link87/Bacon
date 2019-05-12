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
    private final int searchDepth;
    private final int branchingFactor;
    private BuildMove bestMove;
    private boolean isMaxNode;
    private GameState state;
    /**
     * Type of move that lead to this node
     */
    private final Move.Type type;
    private double value;

    /**
     * Creates a new {@link BRSNode} instance from the given values.
     *
     * @param layer the layer of the node in the search tree
     * @param searchDepth the maximum search depth
     * @param branchingFactor the maximum branching factor
     * @param isMaxNode <code>true</code> if this is a max node, <code>false</code>, if not
     * @param type the type of the move that lead to this node
     */
    public BRSNode(int layer, int searchDepth, int branchingFactor, boolean isMaxNode, Move.Type type) {
        this.layer = layer;
        this.searchDepth = searchDepth;
        this.branchingFactor = branchingFactor;
        this.isMaxNode = isMaxNode;
        this.type = type;

        this.state = Game.getGame().getCurrentState();
    }

    /**
     * Returns the best move that can be made according to the heuristics and tree search. Only call after
     * <code>evaluateNode</code> has been called.
     *
     * @return the best move to do
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
        // do beam search to limit the branching factor
        List<BuildMove> beam = computeBeam();

        this.value = this.isMaxNode ? -Double.MAX_VALUE : Double.MAX_VALUE ;

        if (beam == null) {
            // no further moves can be done => the first phase ends here
            Statistics.getStatistics().enterState(layer);
            this.value = evaluateCurrentState(this.type);
        } else if (this.layer < this.searchDepth - 1) {
            // recurse if depth limit is not reached with next move
            Statistics.getStatistics().enterState(this.layer);
            for (BuildMove move : beam) {
                BRSNode childNode = new BRSNode(this.layer + 1, this.searchDepth, this.branchingFactor, !isMaxNode, move.getType());
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();

                if (this.isMaxNode) {
                    if (childNode.value > this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;
                    }
                } else {
                    if (childNode.value < this.value) {
                        this.value = childNode.value;
                        this.bestMove = move;
                    }
                }
            }

        } else {
            // directly evaluate best child node when next layer is on depth limit
            Statistics.getStatistics().enterMeasuredState(this.layer);
            BuildMove leafMove = beam.get(0);
            leafMove.doMove();
            this.value = evaluateCurrentState(leafMove.getType());
            leafMove.undoMove();
            this.bestMove = leafMove;
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

        Set<? extends BuildMove> legalMoves;
        if (isMaxNode) {
            legalMoves = getMaxMoves();
            if (legalMoves.isEmpty()) {
                this.isMaxNode = false;
                legalMoves = getMinMoves();
            }
        }
        else {
            legalMoves = getMinMoves();
            if (legalMoves.isEmpty()) {
                this.isMaxNode = true;
                legalMoves = getMaxMoves();
            }
        }

        // return null if no build moves are possible => first phase ends
        if (legalMoves.isEmpty())
            return null;

        int beamWidth = Math.min(branchingFactor, legalMoves.size());

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
        Set<? extends BuildMove> legalMoves;
        legalMoves = LegalMoves.getLegalRegularMoves(state, state.getMe().getPlayerNumber());
        if (legalMoves.isEmpty())
            legalMoves = LegalMoves.getLegalOverrideMoves(state, state.getMe().getPlayerNumber());

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
