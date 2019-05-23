package bacon.ai;

import bacon.Config;
import bacon.GamePhase;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.move.BombMove;
import bacon.move.Move;

import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AI {

    private static final Logger LOGGER = Logger.getGlobal();

    private static final int BRANCHING_FACTOR = 5;

    private static final AI INSTANCE = new AI();

    private AI() {
    }

    public static AI getAI() {
        return INSTANCE;
    }

    /**
     * Request a move from the ai.
     *
     * @param timeout          the time the ai has for its computation
     * @param depth            the maximum search depth the ai is allowed to do
     * @param cfg              config containing settings for search algorithms
     * @param currentGameState current Game State
     * @return the next move
     */
    public Move requestMove(int timeout, int depth, Config cfg, GameState currentGameState) {

        Statistics.getStatistics().init();

        Move bestMove = null;
        if (currentGameState.getGamePhase() == GamePhase.PHASE_ONE) {
            BRSNode root = new BRSNode(depth, BRANCHING_FACTOR, cfg.isPruningEnabled());
            root.evaluateNode();
            bestMove = root.getBestMove();
        } else {
            Set<BombMove> moves = LegalMoves.getLegalBombMoves(currentGameState, currentGameState.getMe());
            double evalValue;
            double curBestVal = -Double.MAX_VALUE;
            for (BombMove move : moves) {
                long stateTimestamp = System.nanoTime();
                Statistics.getStatistics().enterMeasuredState(0);
                evalValue = Heuristics.bombingPhaseHeuristic(currentGameState, move);
                if (evalValue > curBestVal) {
                    curBestVal = evalValue;
                    bestMove = move;
                }

                Statistics.getStatistics().leaveMeasuredState();

            }
        }

        LOGGER.log(Level.INFO, "Visited {0} states with {1} leaves: {2}.",
                new Object[] {Statistics.getStatistics().getTotalStateCount(), Statistics.getStatistics().getLeafCount(),
                Statistics.getStatistics().getStateCounts().values().stream().map(String::valueOf).collect(Collectors.joining("|"))});

        long totalTimeNanos = Statistics.getStatistics().getElapsedNanos();
        IntSummaryStatistics stats = Statistics.getStatistics().getStateMeasurementResults();

        if (totalTimeNanos < 1000000000) {
            LOGGER.log(Level.INFO, "Computing best move took {0} ms, {1} us avg per state.",
                    new Object[]{totalTimeNanos / 1000000, totalTimeNanos / (1000 * Statistics.getStatistics().getTotalStateCount())});
        } else {
            LOGGER.log(Level.WARNING, "Computing best move took {0} s, {1} us avg per state!",
                    new Object[]{totalTimeNanos / 1000000000.0, totalTimeNanos / (1000 * Statistics.getStatistics().getTotalStateCount())});
        }
        LOGGER.log(Level.INFO, "Computing times per leaf: avg {0} us, min {1} us, max {2} us, leaf time total {3} us.",
                new Object[]{(int) (stats.getAverage() / 1000), stats.getMin() / 1000, stats.getMax() / 1000, stats.getSum() / 1000});

        return bestMove;
    }
}
