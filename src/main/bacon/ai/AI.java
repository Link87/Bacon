package bacon.ai;

import bacon.Game;
import bacon.move.Move;

public class AI {

    private static final AI INSTANCE = new AI();

    private AI() {}

    public static AI getAI() {
        return INSTANCE;
    }

    /**
     * Request a move from the ai.
     *
     * @param timeout the time the ai has for its computation
     * @param depth   the maximum search depth the ai is allowed to do
     * @return the next move
     */
    public Move requestMove(int timeout, int depth) {
        for (int x = 0; x < Game.getGame().getCurrentState().getMap().width; x++) {
            for (int y = 0; y < Game.getGame().getCurrentState().getMap().height; y++) {
                var move = Move.createNewMove(0, Game.getGame().getCurrentState().getMap(),
                        Game.getGame().getCurrentState().getMe(), x, y, 0);
                if (move.isLegal())
                    return move;
            }
        }
        return null;
    }

}
