package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.Player;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.PancakeWatchdog;
import bacon.move.BombMove;
import bacon.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BombNode {
    private final static GameState state = Game.getGame().getCurrentState();

    private static int maxDepth;
    private static PancakeWatchdog watchdog;
    /**
     * The maximum depth that was reached in the search.
     */
    private static int reachedDepth;

    private int layer;
    private Move bestMove;
    private double[] values;
    private int player;

    BombNode(int maxDepth, PancakeWatchdog watchdog) {
        BombNode.maxDepth = maxDepth;
        BombNode.watchdog = watchdog;
        BombNode.reachedDepth = 0;
        this.layer = 1;
        this.player = state.getMe();
    }

    private BombNode(int layer, int player) {
        this.layer = layer;
        this.player = player;
    }

    void evaluateNode() {
        int initialPlayer = player;
        List<BombMove> legalMoves;
        do {
            legalMoves = LegalMoves.getLegalBombMoves(state, player);
            if (!legalMoves.isEmpty()) break;
            player = (player % state.getTotalPlayerCount()) + 1;
        } while (initialPlayer != player);

        if (legalMoves.isEmpty()) {
            //game ends nobody can bomb
            Statistics.getStatistics().enterState(layer - 1);
            values = evaluateState();
            return;
        }

        BombNode.reachedDepth = Integer.max(BombNode.reachedDepth, this.layer);
        values = new double[state.getTotalPlayerCount()];
        Arrays.fill(values, -Double.MAX_VALUE);

        if (this.layer < maxDepth) {
            Statistics.getStatistics().enterState(layer - 1);

            List<BombMove> bestMoves = getBestMoves(legalMoves, (int) (legalMoves.size() * 0.2));
            for (BombMove move : bestMoves) {
                if (watchdog.isPancake()) break;

                BombNode childNode = new BombNode(this.layer + 1, (this.player % state.getTotalPlayerCount()) + 1);
                move.doMove();
                childNode.evaluateNode();
                move.undoMove();
                if (childNode.values[player - 1] > values[player - 1]) {
                    values = childNode.values;
                    this.bestMove = move;
                }
            }

        } else {
            //this.layer == maxDepth

            for (BombMove move : legalMoves) {
                Statistics.getStatistics().enterMeasuredState(this.layer - 1);
                move.doMove();
                double[] temp = evaluateState();
                move.undoMove();

                if (temp[player - 1] > values[player - 1]) {
                    this.values = temp;
                    this.bestMove = move;
                }
                Statistics.getStatistics().leaveMeasuredState();
                if (watchdog.isPancake()) break;
            }
        }
    }

    private List<BombMove> getBestMoves(List<BombMove> legalMoves, int number) {

        int beamWidth = Math.min(number, legalMoves.size());

        BombMove[] beam = new BombMove[beamWidth];
        double[] values = new double[beamWidth];
        Arrays.fill(values, -Double.MAX_VALUE);

        // check if tile belongs to the n best moves (until now)
        // doing some kind of insertion sort
        for (BombMove move : legalMoves) {
            move.doMove();
            double eval = evaluateState()[player - 1];
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

            if (watchdog.isPancake()) break;
        }

        return Arrays.asList(beam);
    }

    private double[] evaluateState() {
        double[] temp = new double[state.getTotalPlayerCount()];
        List<Player> ranking = new ArrayList<>(state.getTotalPlayerCount());
        for (int i = 1; i <= state.getTotalPlayerCount(); i++) {
            ranking.add(state.getPlayerFromId(i));
            temp[i - 1] = state.getPlayerFromId(i).getStoneCount() * 2;
        }

        ranking.sort((p1, p2) -> p2.getStoneCount() - p1.getStoneCount());

        for (int rank = 0; rank < ranking.size(); rank++) {
            Player rankingPlayer = ranking.get(rank);
            double relevance = 1;
            for (int i = rank - 1; i >= 0; i--) {
                temp[rankingPlayer.id - 1] = temp[rankingPlayer.id - 1] - relevance * ranking.get(i).getStoneCount();
                relevance = relevance * 0.5;
            }
            for (int i = rank + 1; i < state.getTotalPlayerCount(); i++) {
                temp[rankingPlayer.id - 1] = temp[rankingPlayer.id - 1] - relevance * ranking.get(i).getStoneCount();
                relevance = relevance * 0.5;
            }
        }

        return temp;
    }

    Move getBestMove() {
        return this.bestMove;
    }

    /**
     * Returns the maximum depth that was reached in the search.
     *
     * @return the maximum reached search depth
     */
    static int getMaximumReachedDepth() {
        return BombNode.reachedDepth;
    }
}
