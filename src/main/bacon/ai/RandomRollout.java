package bacon.ai;

import bacon.GameState;
import bacon.ai.heuristics.LegalMoves;
import bacon.move.Move;

public class RandomRollout {

    private GameState state;
    private int playerCount;
    private int iteration;
    private long startTime;
    private boolean[] playerHasMove;

    private int finalFreeTileCount;
    private int finalOccupiedCount;
    private int finalInversionCount;
    private int finalChoiceCount;
    private int finalBonusCount;


    public RandomRollout(GameState state, int iteration, long startTime) {
        this.state = state;
        this.playerCount = state.getTotalPlayerCount();
        this.iteration = iteration;
        this.startTime = startTime;
        this.playerHasMove = new boolean[playerCount];
        for (int i=0; i<playerCount; i++) {
            playerHasMove[i] = true;
        }

        this.finalFreeTileCount = 0;
        this.finalOccupiedCount = 0;
        this.finalInversionCount = 0;
        this.finalChoiceCount = 0;
        this.finalBonusCount = 0;
    }

    public void doRandRoll(int playerInTurn) {
        if (System.nanoTime() < startTime + 300000000) {
            boolean anyoneHasMove = false;
            for (int i = 0; i < this.playerCount; i++) {
                if (this.playerHasMove[i] == true) anyoneHasMove = true;
            }

            if (anyoneHasMove == true) {
                Move move = LegalMoves.quickLegalRegularMove(this.state, playerInTurn);
                if (move == null) move = LegalMoves.quickLegalOverrideMove(this.state, playerInTurn);
                if (move != null) {
                    //String before = state.getMap().toString();
                    //System.out.println(before);
                    move.doMove();
                    //String middle = state.getMap().toString();
                    doRandRoll((playerInTurn % playerCount) + 1);
                    move.undoMove();
                    //String after = state.getMap().toString();
                /*
                if (!before.equals(after)) {
                    System.out.println(before);
                    System.out.println(middle);
                    System.out.println(after);
                }
                */
                } else {
                    this.playerHasMove[playerInTurn - 1] = false;
                    doRandRoll((playerInTurn % playerCount) + 1);
                }
            } else {
                //System.out.println(this.state.getMap().toString());

                this.finalFreeTileCount = this.state.getMap().getFreeTiles().size();
                this.finalOccupiedCount = this.state.getMap().getOccupiedTileCount();
                this.finalInversionCount = this.state.getMap().getInversionTileCount();
                this.finalChoiceCount = this.state.getMap().getChoiceTileCount();
                this.finalBonusCount = this.state.getMap().getBonusTileCount();
/*
            System.out.println("free: " + this.finalFreeTileCount + " occupied: " + this.finalOccupiedCount + " inversion: " + this.finalInversionCount
                    + " choice: " + this.finalChoiceCount + " bonus: " + this.finalBonusCount);
*/
                this.state.getMap().confirmRollout();
                this.state.getMap().accumulateFinalfreeTiles(this.finalFreeTileCount, this.iteration);
                this.state.getMap().accumulateFinalOccupied(this.finalOccupiedCount, this.iteration);
                this.state.getMap().accumulateFinalInversion(this.finalInversionCount, this.iteration);
                this.state.getMap().accumulateFinalChoice(this.finalChoiceCount, this.iteration);
                this.state.getMap().accumulateFinalBonus(this.finalBonusCount, this.iteration);
/*
            System.out.println("free: " + state.getMap().getFinalfreeTiles() + " occupied: " + state.getMap().getFinalOccupied()
                    + " inversion: " + state.getMap().getFinalInversion() + " choice: " + state.getMap().getFinalChoice()
                    + " bonus: " + state.getMap().getFinalBonus());

 */
            }
        }

    }

}