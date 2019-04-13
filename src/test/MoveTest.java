import org.junit.Test;

import static org.junit.Assert.*;

public class MoveTest {
    private static String ascii =
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 i 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "0 0 0 0 0 b 0 0 0 0 0 0 0 0 0\n" +
                    "0 c 3 2 3 2 1 2 3 0 i 0 0 0 0\n" +
                    "0 0 0 0 3 0 3 1 2 0 0 0 0 0 0\n" +
                    "0 0 0 b 0 1 2 3 1 b 0 0 0 0 0\n" +
                    "0 0 0 0 0 0 x 0 0 0 0 0 b 0 0\n" +
                    "− − − − − 0 0 x 0 0 − − − − −\n" +
                    "− − − − − 0 x x x 0 − − − − −\n" +
                    "− − − − − 0 0 x c 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "− − − − − 0 0 0 0 0 − − − − −\n" +
                    "6 0 0 <−> 9 1 1\n" +
                    "7 14 4 <−> 7 0 0\n";
    private static Tile.Property[][] properties =  {
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.INVERSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },

            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.BONUS, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.CHOICE, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.INVERSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT },
            { Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.BONUS, Tile.Property.DEFAULT, Tile.Property.DEFAULT },

            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.EXPANSION, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.EXPANSION, Tile.Property.CHOICE, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE },
            { Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE,
                    Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT, Tile.Property.DEFAULT,
                    Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE, Tile.Property.HOLE }
    };

    private static int[] x = {7, 6, 7, 0, 0, 9, 5, 7, 6, 10, 1, 5, 9, 10, 10};
    private static int[] y = {6, 6, 6, 0, 5, 6, 7, 5, 9, 6, 6, 5, 8, 5, 4};
    private static int maxplayers = 4;

    @Test
    public void MoveTests(){
        Game game = Game.getInstance();
        Game.setBombRadius(2);

        String[] lines = ascii.split("\r?\n");

        Map map = Map.readFromString(15, 15, lines);
        Game .setMyMap(map);

        Player[] player = new Player[3];

        for(int i = 1; i < maxplayers; i++) {
            player[i-1] = Player.readFromString(i, 2, 2);
        }

        Game.setPlayer(player);

        Game.setCurrentPhase(Game.GamePhase.PHASEONE);

        executeRegularMove(map, player);

        executeOverrideMove(map, player);

        Game.setCurrentPhase(Game.GamePhase.PHASETWO);

        executeBombMove(map, player);
    }

    /**
     * Tests whether RegularMove is working correctly
     */
    public void executeRegularMove(Map map, Player[] player){

        for (int i = 1; i < maxplayers; i++){
            // Try to place a stone on an occupied tile
            RegularMove move = new RegularMove(i, map, player[i-1], x[i-1], y[i-1], 0 );
            move.doMove();
            assertEquals("Possible to place tile on occupied tile", i, map.getTileAt(x[i], y[i]).getOwner());
        }

        // Try to place a stone on a hole
        for(int i = 1; i < maxplayers; i++){
            RegularMove move = new RegularMove(3+i, map, player[i-1], x[3], y[3], 0 );
            move.doMove();
            assertEquals("Possible to place a stone on a hole", '-', map.getTileAt(x[4], y[4]).getOwner());
        }

        // Try to place a stone on a tile which is not valid
        for(int i = 1; i < maxplayers; i++){
            RegularMove move = new RegularMove(6+i, map, player[i-1], x[4], y[4], 0 );
            move.doMove();
            assertEquals("Possible to place a stone on any tile on the map", '-', map.getTileAt(x[5], y[5]).getOwner().getStatus());
        }

        // Try to place a stone on a tile and occupy stones
        for(int i = 1; i < maxplayers; i++){
            RegularMove move = new RegularMove(6+i, map, player[i-1], x[4+i], y[4+i], 0 );
            move.doMove();
            assertEquals("the stone is not correctly placed on the map ", i, map.getTileAt(x[4+i], y[5+i]).getOwner());
            if(i == 1){
                assertEquals("The stones are not changed: Player 1", i, map.getTileAt(x[4+i]-1, y[4+i]).getOwner().getPlayerNumber());
                assertEquals("The stones are not changed: Player 1", i, map.getTileAt(x[4+i]-2, y[4+i]).getOwner().getPlayerNumber());
            }
            else if (i == 2){
                assertEquals("The stones are not changed: Player 2", i, map.getTileAt(x[4+i]+1, y[4+i]).getOwner().getPlayerNumber());
                assertEquals("The stones are not changed: Player 2", i, map.getTileAt(x[4+i]+2, y[4+i]).getOwner().getPlayerNumber());
            }
            else {
                assertEquals("The stones are not changed: Player 3", i, map.getTileAt(x[4+i], y[4+i]-1).getOwner().getPlayerNumber());
                assertEquals("The stones are not changed: Player 3", i, map.getTileAt(x[4+i], y[4+i]-2).getOwner().getPlayerNumber());
            }
        }

        // Try to place a stone on an expansion stone
        for(int i = 1; i < maxplayers; i++){
            RegularMove move = new RegularMove(12+i, map, player[i-1], x[8], y[8], 0 );
            move.doMove();
            assertEquals("Possible to place a stone on an expansion stone", "x", map.getTileAt(x[8], y[8]).getOwner());
        }
        // Try to place a stone on a inversion field
        int[][] check = new int[][] {{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                                      {0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 0, 0, 0, 0},
                                      {0, 0, 0, 0, 1, 3, 3, 1, 3, 0, 0, 0, 0, 0, 0},
                                      {0, 0, 0, 0, 0, 2, 3, 1, 3, 0, 0, 0, 0, 0, 0}};

        RegularMove move = new RegularMove(16, map, player[3-1], x[9], y[9], 0);
        move.doMove();

        int[] real = new int[15];

        for(int i = 5; i < 9; i++){
            for(int j = 0; j < 15; j++){
                real[j] = map.getTileAt(j,i).getOwner().getPlayerNumber();
            }
            assertEquals("The stones in row " +i+  "are not switched correctly", check[][i] , real  );
        }

        // Try to place a stone on a choice field
        int[][] check = new int[][] {{0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0},
                                    {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0},
                                    {0, 0, 0, 0, 2, 3, 3, 2, 3, 0, 0, 0, 0, 0, 0},
                                    {0, 0, 0, 0, 0, 1, 3, 2, 3, 0, 0, 0, 0, 0, 0}};

        RegularMove move = new RegularMove(17, map, player[0], x[10], y[10], 2);
        move.doMove();

        int[] real = new int[15];

        for(int i = 5; i < 9; i++){
            for(int j = 0; j < 15; j++){
                real[j] = map.getTileAt(j,i);
            }
            assertEquals("The stones in row"+ i +"are not switched correctly", check[][i] , real);
        }
        // Try to place a stone on a bonus field and choose bomb
        RegularMove move = new RegularMove(17, map, player[1], x[11], y[11], 20);
        move.doMove();

        //check bomb count
        assertEquals("bomb count is wrong", 3, player[0].getBombCount());

        // Try to place a stone on a bonus field and choose override stone
        RegularMove move = new RegularMove(17, map, player[1], x[12], y[12], 25);
        move.doMove();

        //check override count
        assertEquals("Override count wrong", 3, player[0].getOverrideStoneCount());
    }

    /**
     * Tests whether OverrideMove is working correctly
     */
    public void executeOverrideMove(Map map, Player[] players){
        // Try to place a stone on an unoccupied tile
        OverrideMove move = new OverrideMove(1, map, players[0], x[13], y[13], 0);
        move.doMove();

        assertEquals("Overrides an empty tile", 1, map.getTileAt(x[13],y[13]).getOwner());

        // Check override count
        assertEquals("Override count wrong", 2, players[0].getOverrideStoneCount());

        // Try to place a stone on a hole
        OverrideMove move = new OverrideMove(1, map, players[0], x[14], y[14], 0);
        move.doMove();

        assertEquals("Overrides an empty tile", 1, map.getTileAt(x[14],y[14]).getOwner());

        // Check override count
        assertEquals("Override count wrong", 2, players[0].getOverrideStoneCount());

        // Try to place a stone on a tile which is occupied by the same player
        OverrideMove move = new OverrideMove(1, map, players[0], x[15], y[15], 0);
        move.doMove();

        assertEquals("Overrides an empty tile", 1, map.getTileAt(x[15],y[15]).getOwner());

        // Check override count
        assertEquals("Override count wrong", 2, players[0].getOverrideStoneCount());
        // Try to place a stone on a tile from another player
    }

    /**
     * Tests whether BombMove is working correctly
     */
    public void executeBombMove(Map map, Player[] players){

        BombMove move = new BombMove(1, map, players[], x[], y[], 0);

        // Try to place a bomb on an unoccupied tile

        // Try to place a bomb on a hole

        // Try to place a bomb on a tile which is occupied

        // Check bomb count
    }

}
