/**
 * A Player class which contains player data and performs player actions
 */

public class Player {

    public int number;
    public int stonesOnMap;
    public int numberOfOverrideStones;
    public int bombs;

    public Player(int n, int nOS, int b) {
        number = n;
        numberOfOverrideStones = nOS;
        bombs = b;
    }

    public void setStoneOnMap(Map map, int x, int y) {
        map.setStone(x, y, this.number);
        this.stonesOnMap++;
    }

    public void useOverrideStones(Map map, int x, int y) {
        this.numberOfOverrideStones --;
        map.override(x, y);
    }

    public void useBomb(Map map, int x, int y) {
        this.bombs --;
        map.throwBomb(x, y);
    }



}