package bacon.ai;

import bacon.*;
import bacon.move.BonusRequest;
import bacon.move.Move;
import bacon.move.MoveFactory;
import bacon.ai.heuristics.*;

import java.util.Set;

public class AI {

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
        Set<Tile> moveTiles =null;
        Move curBestMove = null;
        if (currentGameState.getGamePhase() == GamePhase.PHASE_ONE) {
            moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), MoveType.REGULAR);
            if (moveTiles.isEmpty()) {
                moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), MoveType.OVERRIDE);
            }
            double evalValue;
            double curBestVal = -Double.MAX_VALUE;
            for (Tile tile : moveTiles) {
                if (tile.getProperty() == Tile.Property.CHOICE) {
                    for (int numbr = 1; numbr <= currentGameState.getTotalPlayerCount(); numbr++) {
                        GameState evalState = currentGameState.getDeepCopy();
                        Move moveToEval = MoveFactory.createMove(evalState, evalState.getMe(), tile.x, tile.y, BonusRequest.fromValue(numbr, evalState));
                        moveToEval.doMove();
                        int meNumbr = evalState.getMe().getPlayerNumber();
                        evalValue = stbltyScaler * StabilityHeuristic.stability(evalState, meNumbr)
//                                + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                                + mobltyScaler * Heuristics.mobility(evalState, meNumbr);
                        if (evalValue > curBestVal) {
                            curBestVal = evalValue;
                            curBestMove = moveToEval;
                        }
                    }
                } else if (tile.getProperty() == Tile.Property.BONUS) {
                    GameState evalState = currentGameState.getDeepCopy();
                    Move moveToEval = MoveFactory.createMove(evalState, evalState.getMe(), tile.x, tile.y, new BonusRequest(BonusRequest.Type.BOMB_BONUS));
                    moveToEval.doMove();
                    int meNumbr = evalState.getMe().getPlayerNumber();
                    evalValue = stbltyScaler * StabilityHeuristic.stability(evalState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                            + mobltyScaler * Heuristics.mobility(evalState, meNumbr)
                            + bonusScalar * Heuristics.bonusBomb(evalState, meNumbr);
                    if (evalValue > curBestVal) {
                        curBestVal = evalValue;
                        curBestMove = moveToEval;
                    }

                    evalState = currentGameState.getDeepCopy();
                    moveToEval = MoveFactory.createMove(evalState, evalState.getMe(), tile.x, tile.y, new BonusRequest(BonusRequest.Type.OVERRIDE_BONUS));
                    moveToEval.doMove();
                    meNumbr = evalState.getMe().getPlayerNumber();
                    evalValue = stbltyScaler * StabilityHeuristic.stability(evalState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                            + mobltyScaler * Heuristics.mobility(evalState, meNumbr)
                            + bonusScalar * Heuristics.bonusOverride(evalState, meNumbr);
                    if (evalValue > curBestVal) {
                        curBestVal = evalValue;
                        curBestMove = moveToEval;
                    }
                } else {
                    GameState evalState = currentGameState.getDeepCopy();
                    Move moveToEval = MoveFactory.createMove(evalState, evalState.getMe(), tile.x, tile.y);
                    moveToEval.doMove();
                    int meNumbr = evalState.getMe().getPlayerNumber();
                    evalValue = stbltyScaler * StabilityHeuristic.stability(evalState, meNumbr)
//                            + clstrngScaler * Heuristics.clustering(evalState, meNumbr)
                            + mobltyScaler * Heuristics.mobility(evalState, meNumbr);
                    if (evalValue > curBestVal) {
                        curBestVal = evalValue;
                        curBestMove = moveToEval;
                    }
                }
            }
        } else {
            moveTiles = LegalMoves.getLegalMoveTiles(currentGameState, currentGameState.getMe().getPlayerNumber(), MoveType.BOMB);
            double evalValue;
            double curBestVal = -Double.MAX_VALUE;
            Tile curBestTile = null;
            for (Tile tile : moveTiles) {
                evalValue = Heuristics.bombingPhaseHeuristic(currentGameState, currentGameState.getMe().getPlayerNumber(), tile);
                if (evalValue > curBestVal) {
                    curBestVal = evalValue;
                    curBestTile = tile;
                }
            }
            curBestMove = MoveFactory.createMove(currentGameState, currentGameState.getMe(), curBestTile.x, curBestTile.y);
        }

        //TODO remove after issue #13 is closed (this is a band aid)
        if(curBestMove == null){
            for (int y = 0; y < currentGameState.getMap().height; y++) {
                for (int x = 0; x < currentGameState.getMap().width; x++) {
                    if(currentGameState.getMap().getTileAt(x,y).getProperty()== Tile.Property.EXPANSION)
                        return MoveFactory.createMove(currentGameState, currentGameState.getMe(), x, y);
                }
            }
        }

        return curBestMove;
    }
}
