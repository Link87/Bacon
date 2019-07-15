package bacon.ai;

import bacon.Config;
import bacon.Game;
import bacon.GamePhase;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.IterationHeuristic;
import bacon.ai.heuristics.LegalMoves;
import bacon.ai.heuristics.PancakeWatchdog;
import bacon.move.BombMove;
import bacon.move.Move;

import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A class for the actual ai.
 */
public class AI {

    private static final Logger LOGGER = Logger.getGlobal();

    private static final AI INSTANCE = new AI();

    private static int moveCounter = 0;

    /**
     * Singleton constructor that does nothing.
     */
    private AI() {}

    /**
     * Returns the singleton {@code AI} instance.
     *
     * @return the {@code AI} instance
     */
    public static AI getAI() {
        return INSTANCE;
    }

    /**
     * Request a {@link Move} from the {@code AI}.
     * <p>
     * Returns the best move that can be found in the given time or depth limit.
     *
     * @param timeout          the time the ai has for its computation
     * @param depth            the maximum search depth the ai is allowed to do
     * @param cfg              config containing settings for search algorithms
     * @param currentGameState current {@link GameState}
     * @return the next move
     */
    public Move requestMove(int timeout, int depth, Config cfg, GameState currentGameState) {

        LOGGER.log(Level.INFO, "Time limit " + timeout + "ms.");

        Statistics.getStatistics().init();

        int currentMoveNumber = currentGameState.getMoveCount();
        moveCounter++;


        Move bestMove = null;
        if (currentGameState.getGamePhase() == GamePhase.PHASE_ONE) {
            int randRollTime = 0;
            if (moveCounter % cfg.getRandRollFrequency() == 2 && currentMoveNumber != 1) {
                LOGGER.log(Level.INFO, "RR started in move #" + currentMoveNumber + " , moveCounter = " + moveCounter);
                long startTimeStamp = System.nanoTime();
                RandomRollout randroll = new RandomRollout(Game.getGame().getCurrentState(), cfg.getMaxRandRollIterations(),
                        startTimeStamp + cfg.getRandRollTimeBudget() * 1000000);
                long endTimeStamp = System.nanoTime();
                randRollTime = (int) (endTimeStamp - startTimeStamp) / 1000000;
                LOGGER.log(Level.INFO, "RR completed, elapsed time: " + randRollTime + "ms, completed iterations: " + (randroll.getTotalIteration() - 1));
                System.out.println("avgFree: " + currentGameState.getMap().getFinalfreeTiles()
                        + "  avgOcc: " + currentGameState.getMap().getFinalOccupied()
                        + "  avgInv: " + currentGameState.getMap().getFinalInversion()
                        + "  stdvInv: " + currentGameState.getMap().getFinalInversionStdv()
                        + "  avgCho: " + currentGameState.getMap().getFinalChoice()
                        + "  stdvCho: " + currentGameState.getMap().getFinalChoiceStdv()
                        + "  avgBon: " + currentGameState.getMap().getFinalBonus() );
            }


            PancakeWatchdog watchdog = new PancakeWatchdog(timeout - randRollTime);
            IterationHeuristic iterationHeuristic = new IterationHeuristic(timeout - randRollTime, depth);

            double alpha = -Double.MAX_VALUE;
            double beta = Double.MAX_VALUE;
            BRSNode root;
            while (iterationHeuristic.doIteration()) {
                root = new BRSNode(iterationHeuristic.getDepth(), cfg.getBeamWidth(), cfg.isPruningEnabled(),
                        cfg.isMoveSortingEnabled(), cfg.isAspirationWindowsEnabled(), alpha, beta, watchdog);
                root.evaluateNode();

                if (root.getBestMove() != null) {
                    bestMove = root.getBestMove();
                } else if (cfg.isAspirationWindowsEnabled() && !watchdog.isTriggered()) {
                    // aspiration window failure: restart search with default alpha/beta values
                    root = new BRSNode(iterationHeuristic.getDepth(), cfg.getBeamWidth(), cfg.isPruningEnabled(),
                            cfg.isMoveSortingEnabled(), false, -Double.MAX_VALUE, Double.MAX_VALUE, watchdog);
                    root.evaluateNode();
                    if (root.getBestMove() != null) bestMove = root.getBestMove();
                }

                if (cfg.isAspirationWindowsEnabled()) {
                    // update aspiration window for next BRS-iteration
                    root.aspWindow();
                    alpha = root.getAspWindowAlpha();
                    beta = root.getAspWindowBeta();
                }

                if (watchdog.isTriggered()) {
                    LOGGER.log(Level.WARNING, "Pancake triggered!");
                    break;
                }

                // stop ai from for example trying depth 10 if only 5 rounds remain
                if (BRSNode.getMaximumReachedDepth() < iterationHeuristic.getDepth()) break;
            }
        } else {
            IterationHeuristic iterationHeuristic = new IterationHeuristic(timeout, depth);
            PancakeWatchdog watchdog = new PancakeWatchdog(timeout);
            while (iterationHeuristic.doIteration()){
                BombNode root = new BombNode(iterationHeuristic.getDepth(),watchdog);
                root.evaluateNode();
                if(!watchdog.isTriggered()) {
                    bestMove = root.getBestMove();
                }
                else {
                    LOGGER.log(Level.WARNING, "Pancake triggered!");
                    break;
                }

                if(BombNode.getMaximumReachedDepth() < iterationHeuristic.getDepth()) break;
            }
        }

        LOGGER.log(Level.INFO, "Visited {0} states with {1} leaves: {2}.",
                new Object[]{Statistics.getStatistics().getTotalStateCount(), Statistics.getStatistics().getLeafCount(),
                        Statistics.getStatistics().getStateCounts().values().stream().map(String::valueOf).collect(Collectors.joining("|"))});

        long totalTimeNanos = Statistics.getStatistics().getElapsedNanos();
        IntSummaryStatistics stats = Statistics.getStatistics().getStateMeasurementResults();

        LOGGER.log(Level.INFO, "Computing best move took {0} ms, {1} μs avg per state.",
                new Object[]{totalTimeNanos / 1000000, totalTimeNanos / (1000 * Statistics.getStatistics().getTotalStateCount())});
        LOGGER.log(Level.INFO, "Computing times per leaf: avg {0} μs, min {1} μs, max {2} μs, leaf time total {3} μs.",
                new Object[]{(int) (stats.getAverage() / 1000), stats.getMin() / 1000, stats.getMax() / 1000, stats.getSum() / 1000});

        return bestMove;
    }
}
