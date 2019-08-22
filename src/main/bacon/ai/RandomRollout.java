package bacon.ai;

import bacon.GameState;
import bacon.Tile;
import bacon.ai.heuristics.LegalMoves;
import bacon.move.Move;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomRollout {

    private static final Logger LOGGER = Logger.getGlobal();

    private GameState state;
    private int playerCount;
    private long timeout;
    private int totalIteration;


    public RandomRollout(GameState state, int maxIterations, long timeout) {
        this.state = state;
        this.playerCount = state.getTotalPlayerCount();
        this.timeout = timeout;
        this.totalIteration = 1;

        state.getMap().newRandRollStats(maxIterations);
        while (System.nanoTime() < timeout && totalIteration <= maxIterations) {
            boolean[] playerHasMove = new boolean[playerCount];
            for (int i = 0; i < playerCount; i++) {
                playerHasMove[i] = true;
            }
            doRollout(state.getMe(), totalIteration, playerHasMove);
        }
    }

    private void doRollout(int playerInTurn, int iteration, boolean[] playerHasMove) {
        if (System.nanoTime() < timeout) {
            boolean anyoneHasMove = false;
            for (int i = 0; i < this.playerCount; i++) {
                if (playerHasMove[i]) anyoneHasMove = true;
            }

            if (anyoneHasMove) {
                Move move = LegalMoves.quickRegularMove(this.state, playerInTurn);
                if (move == null) move = LegalMoves.quickOverrideMove(this.state, playerInTurn);
                if (move != null) {
                    String before = state.getMap().toString();
                    //System.out.println(before);
                    boolean inversionMove = (state.getMap().getTileAt(move.getX(), move.getY()).getProperty() == Tile.Property.INVERSION);
                    boolean choiceMove = (state.getMap().getTileAt(move.getX(), move.getY()).getProperty() == Tile.Property.CHOICE);
                    if (inversionMove) {
                        state.getMap().getTileAt(move.getX(), move.getY()).setProperty(Tile.Property.DEFAULT);
                        state.getMap().addInversionTiles(-1);
                    } else if (choiceMove) {
                        state.getMap().getTileAt(move.getX(), move.getY()).setProperty(Tile.Property.DEFAULT);
                        state.getMap().addChoiceTiles(-1);
                    }
                    move.doMove();
                    String middle = state.getMap().toString();
                    doRollout((playerInTurn % playerCount) + 1, iteration, playerHasMove);
                    move.undoMove();
                    if (inversionMove) {
                        state.getMap().getTileAt(move.getX(), move.getY()).setProperty(Tile.Property.INVERSION);
                        state.getMap().addInversionTiles(1);
                    } else if (choiceMove) {
                        state.getMap().getTileAt(move.getX(), move.getY()).setProperty(Tile.Property.CHOICE);
                        state.getMap().addChoiceTiles(1);
                    }
                    String after = state.getMap().toString();

                    if (!before.equals(after)) {
                        LOGGER.log(Level.FINE, "BEFORE: " + before);
                        LOGGER.log(Level.FINE, "MIDDLE" + middle);
                        LOGGER.log(Level.FINE, "AFTER" + after);
                    }

                } else {
                    playerHasMove[playerInTurn - 1] = false;
                    doRollout((playerInTurn % playerCount) + 1, iteration, playerHasMove);
                }
            } else {
                //System.out.println(this.state.getMap().toString());

                int finalFreeTileCount = state.getMap().getFreeTiles().size();
                int finalOccupiedCount = state.getMap().getOccupiedTileCount();
                int finalInversionCount = state.getMap().getInversionTileCount();
                int finalChoiceCount = state.getMap().getChoiceTileCount();
                int finalBonusCount = state.getMap().getBonusTileCount();
                int unusedOverrideCount = 0;
                for (int i = 0; i < playerCount; i++) {
                        unusedOverrideCount += state.getPlayerFromId(i+1).getOverrideStoneCount();
                }

                state.getMap().updateRolloutStats(iteration, finalFreeTileCount, finalOccupiedCount, finalInversionCount, finalChoiceCount, finalBonusCount, unusedOverrideCount);
                totalIteration++;
            }
        }
    }

    public int getTotalIteration() {
        return totalIteration;
    }

}