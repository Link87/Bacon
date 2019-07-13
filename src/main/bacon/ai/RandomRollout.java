package bacon.ai;

import bacon.GamePhase;
import bacon.GameState;
import bacon.Player;
import bacon.Tile;
import bacon.ai.heuristics.LegalMoves;
import bacon.move.BombMove;
import bacon.move.Move;
import bacon.move.OverrideMove;
import bacon.move.RegularMove;

import java.util.Set;

import static java.lang.Math.*;

public class RandomRollout {

    private GameState state;
    private int playerCount;
    private boolean[] playerHasMove;

    public RandomRollout(GameState state) {
        this.state = state;
        this.playerCount = state.getTotalPlayerCount();
        this.playerHasMove = new boolean[playerCount];
        for (int i=0; i<playerCount; i++) {
            playerHasMove[i] = true;
        }
    }

    public void doRandRoll(int playerInTurn) {

        boolean anyoneHasMove = false;
        for (int i=0; i<this.playerCount; i++){
            if (this.playerHasMove[i] == true) anyoneHasMove = true;
        }

        if (anyoneHasMove == true) {
            Move move = LegalMoves.quickLegalRegularMove(this.state, playerInTurn);
            if (move == null) move = LegalMoves.quickLegalOverrideMove(this.state, playerInTurn);
            if (move != null) {
                move.doMove();
                doRandRoll((playerInTurn % playerCount) + 1);
                move.undoMove();
            } else {
                this.playerHasMove[playerInTurn - 1] = false;
                doRandRoll((playerInTurn % playerCount) + 1);
            }
        } else {
            System.out.println(this.state.getMap().toString());
        }

    }

}