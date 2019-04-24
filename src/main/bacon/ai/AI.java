package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.Map;
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
     * @param currentGameState current  Game State
     * @return the next move
     */
    public Move requestMove(int timeout, int depth, GameState currentGameState) {
        Map map = currentGameState.getMap();
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                var move = Move.createNewMove(0, map,
                        Game.getGame().getCurrentState().getMe(), x, y, 0);
                if (move.isLegal())
                    return move;
            }
        }
        return null;
    }

}
