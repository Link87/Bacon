package bacon.ai;

import bacon.GameState;
import bacon.move.Move;
import bacon.Player;

import java.util.ArrayList;

/**
 * A singleton that determines an ArrayList of all possible legal move from a certain game state
 * for a certain player in any game phase
 */
public class LegalMoves {
    private static LegalMoves legalMoves = new LegalMoves();

    public static LegalMoves getLegalMoves() {
        return legalMoves;
    }

    private LegalMoves() {
    }

    /**
     * all legal moves possible from a certain given board state and player
     *
     * @return legal moves (including RegularMoves, OverrideMoves, BombMoves)
     */
    public ArrayList<Move> legalMoves(GameState state, Player player){
        ArrayList<Move> moves = new ArrayList<>();

        return moves;
    }

}
