package bacon.ai;

import bacon.GamePhase;
import bacon.GameState;
import bacon.ai.heuristics.Heuristics;
import bacon.ai.heuristics.LegalMoves;
import bacon.move.BombMove;
import bacon.move.Move;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @param currentGameState current  Game State
     * @return the next move
     */
    public Move requestMove(int timeout, int depth, GameState currentGameState) {
        long timestamp = System.nanoTime();
        int maxTime = Integer.MIN_VALUE, minTime = Integer.MAX_VALUE, stateCount = 1;

        Move bestMove = null;
        if (currentGameState.getGamePhase() == GamePhase.PHASE_ONE) {
            //TODO Command line switch interface de/activation of alpha-beta pruning
            BRSNode root = new BRSNode(depth, BRANCHING_FACTOR, true);
            root.evaluateNode();
            bestMove = root.getBestMove();

            // int stateDuration = (int) (System.nanoTime() - stateTimestamp);
            // if (stateDuration < minTime) minTime = stateDuration;
            // if (stateDuration > maxTime) maxTime = stateDuration;

        } else {
            Set<BombMove> moves = LegalMoves.getLegalBombMoves(currentGameState, currentGameState.getMe().number);
            double evalValue;
            double curBestVal = -Double.MAX_VALUE;
            for (BombMove move : moves) {
                long stateTimestamp = System.nanoTime();
                stateCount++;
                evalValue = Heuristics.bombingPhaseHeuristic(currentGameState, move);
                if (evalValue > curBestVal) {
                    curBestVal = evalValue;
                    bestMove = move;
                }

                int stateDuration = (int) (System.nanoTime() - stateTimestamp);
                if (stateDuration < minTime) minTime = stateDuration;
                else if (stateDuration > maxTime) maxTime = stateDuration;
            }
        }

        LOGGER.log(Level.INFO, "Found {0} move(s).", stateCount);

        long totalTimeNanos = System.nanoTime() - timestamp;
        LOGGER.log(Level.INFO, "Computing best move took {0} ms.", totalTimeNanos / 1000000.0);
        LOGGER.log(Level.INFO, "Computing times per move: avg {0} us, min {1} us, max {2} us.",
                new Object[]{totalTimeNanos / (1000 * stateCount), minTime / 1000, maxTime / 1000});

        return bestMove;
    }
}
