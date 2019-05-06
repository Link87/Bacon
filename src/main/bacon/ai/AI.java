package bacon.ai;

import bacon.*;
import bacon.move.BonusRequest;
import bacon.move.Move;
import bacon.move.MoveFactory;
import bacon.ai.heuristics.*;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AI {

    private static final Logger LOGGER = Logger.getGlobal();


    private static final AI INSTANCE = new AI();
    public double stbltyScaler = 1;
    public double clstrngScaler = 1;
    public double mobltyScaler = 1;
    public double bonusScalar = 1;

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
        int maxTime = Integer.MIN_VALUE, minTime = Integer.MAX_VALUE, stateCount = 0;

        Set<Tile> moveTiles;
        Move curBestMove = null;
        if (currentGameState.getGamePhase() == GamePhase.PHASE_ONE) {
            moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), Move.Type.REGULAR);
            if (moveTiles.isEmpty()) {
                moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), Move.Type.OVERRIDE);
                double evalValue;
                double curBestVal = -Double.MAX_VALUE;
                for (Tile tile : moveTiles) {
                    stateCount++;
                    Move moveToEval = MoveFactory.createMove(currentGameState, currentGameState.getMe(), tile.x, tile.y);
                    moveToEval.doMove();
                    int meNumbr = currentGameState.getMe().getPlayerNumber();
                    evalValue = stbltyScaler * StabilityHeuristic.stability(currentGameState, meNumbr);
                    if (evalValue > curBestVal) {
                        curBestVal = evalValue;
                        curBestMove = moveToEval;
                    }
                    moveToEval.undoMove();
                }

            } else {
                double evalValue;
                double curBestVal = -Double.MAX_VALUE;
                for (Tile tile : moveTiles) {
                    long stateTimestamp = System.nanoTime();
                    stateCount++;

                    if (tile.getProperty() == Tile.Property.CHOICE) {
                        for (int numbr = 1; numbr <= currentGameState.getTotalPlayerCount(); numbr++) {
                            Move moveToEval = MoveFactory.createMove(currentGameState, currentGameState.getMe(), tile.x, tile.y, BonusRequest.fromValue(numbr, currentGameState));
                            moveToEval.doMove();
                            int meNumbr = currentGameState.getMe().getPlayerNumber();
                            evalValue = stbltyScaler * StabilityHeuristic.stability(currentGameState, meNumbr)
//                                + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                                    + mobltyScaler * Heuristics.mobility(currentGameState, meNumbr);
                            if (evalValue > curBestVal) {
                                curBestVal = evalValue;
                                curBestMove = moveToEval;
                            }
                            moveToEval.undoMove();
                        }
                    } else if (tile.getProperty() == Tile.Property.BONUS) {
                        Move moveToEval = MoveFactory.createMove(currentGameState, currentGameState.getMe(), tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                        moveToEval.doMove();
                        int meNumbr = currentGameState.getMe().getPlayerNumber();
                        evalValue = stbltyScaler * StabilityHeuristic.stability(currentGameState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                                + mobltyScaler * Heuristics.mobility(currentGameState, meNumbr)
                                + bonusScalar * Heuristics.bonusBomb(currentGameState, meNumbr);
                        if (evalValue > curBestVal) {
                            curBestVal = evalValue;
                            curBestMove = moveToEval;
                        }
                        moveToEval.undoMove();

                        moveToEval = MoveFactory.createMove(currentGameState, currentGameState.getMe(), tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                        moveToEval.doMove();
                        meNumbr = currentGameState.getMe().getPlayerNumber();
                        evalValue = stbltyScaler * StabilityHeuristic.stability(currentGameState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                                + mobltyScaler * Heuristics.mobility(currentGameState, meNumbr)
                                + bonusScalar * Heuristics.bonusOverride(currentGameState, meNumbr);
                        if (evalValue > curBestVal) {
                            curBestVal = evalValue;
                            curBestMove = moveToEval;
                        }
                        moveToEval.undoMove();
                    } else {
                        Move moveToEval = MoveFactory.createMove(currentGameState, currentGameState.getMe(), tile.x, tile.y);
                        moveToEval.doMove();
                        int meNumbr = currentGameState.getMe().getPlayerNumber();
                        evalValue = stbltyScaler * StabilityHeuristic.stability(currentGameState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                                + mobltyScaler * Heuristics.mobility(currentGameState, meNumbr);
                        if (evalValue > curBestVal) {
                            curBestVal = evalValue;
                            curBestMove = moveToEval;
                        }
                        moveToEval.undoMove();
                    }

                    int stateDuration = (int) (System.nanoTime() - stateTimestamp);
                    if (stateDuration < minTime) minTime = stateDuration;
                    if (stateDuration > maxTime) maxTime = stateDuration;
                }
            }


        } else {
            moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), Move.Type.BOMB);
            double evalValue;
            double curBestVal = -Double.MAX_VALUE;
            Tile curBestTile = null;
            for (Tile tile : moveTiles) {
                long stateTimestamp = System.nanoTime();
                stateCount++;

                evalValue = Heuristics.bombingPhaseHeuristic(currentGameState, currentGameState.getMe().getPlayerNumber(), tile);
                if (evalValue > curBestVal) {
                    curBestVal = evalValue;
                    curBestTile = tile;
                }

                int stateDuration = (int) (System.nanoTime() - stateTimestamp);
                if (stateDuration < minTime) minTime = stateDuration;
                else if (stateDuration > maxTime) maxTime = stateDuration;
            }
            curBestMove = MoveFactory.createMove(currentGameState, currentGameState.getMe(), curBestTile.x, curBestTile.y);
        }

        //TODO remove after issue #13 is closed (this is a band aid)
        if (curBestMove == null) {
            for (int y = 0; y < currentGameState.getMap().height; y++) {
                for (int x = 0; x < currentGameState.getMap().width; x++) {
                    if (currentGameState.getMap().getTileAt(x, y).getProperty() == Tile.Property.EXPANSION)
                        return MoveFactory.createMove(currentGameState, currentGameState.getMe(), x, y);
                }
            }
        }

        LOGGER.log(Level.INFO, "Found {0} move(s).", stateCount);

        long totalTimeNanos = System.nanoTime() - timestamp;
        LOGGER.log(Level.INFO, "Computing best move took {0} ms.", totalTimeNanos / 1000000.0);
        LOGGER.log(Level.INFO, "Computing times per move: avg {0} us, min {1} us, max {2} us.",
                new Object[]{totalTimeNanos / (1000 * stateCount), minTime / 1000, maxTime / 1000});
        return curBestMove;
    }
}
