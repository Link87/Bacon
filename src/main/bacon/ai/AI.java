package bacon.ai;

import bacon.Game;
import bacon.GameState;
import bacon.Map;
import bacon.Tile;
import bacon.move.BonusRequest;
import bacon.move.Move;
import bacon.move.MoveFactory;

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
                BonusRequest br = null;
                if(map.getTileAt(x,y).getProperty()== Tile.Property.CHOICE)  br = BonusRequest.fromValue(1,currentGameState);
                if(map.getTileAt(x,y).getProperty()== Tile.Property.BONUS) br = BonusRequest.fromValue(21,currentGameState);
                var move = MoveFactory.createMove(Game.getGame().getCurrentState(),
                        Game.getGame().getCurrentState().getMe(), x, y, br);
                if (move.isLegal())
                    return move;
            }
        }
        return null;
    }

}
