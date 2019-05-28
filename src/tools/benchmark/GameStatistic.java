package benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static benchmark.Data.*;

public class GameStatistic {
    //    private ArrayList<int[]> moveList;
    public int gameID;
    private String mapPath;
    private int playercount;
    private boolean finalScors;

    private int playerToBenchmark;
    private int stonesPhaseOne;
    private int bombsCollected;
    private int overrideCollected;
    private int choiceTriggert;
    private int timeLeft;

    private int rivalToBenchmark;
    private int rivalStonesPhaseOne;
    private int rivalBombsCollected;
    private int rivalOverrideCollected;
    private int rivalChoiceTriggert;
    private int rivalTimeLeft;

    private ArrayList<PlayerPoints> playerpoints;
    private int[] matchPoints;

    public GameStatistic(int gameID, String mapPath, int playercount, int playerToBenchmark, int rivalToBenchmark) {
        this.gameID = gameID;
        this.mapPath = mapPath;
        this.playercount = playercount;
        this.finalScors = false;

        this.playerToBenchmark = playerToBenchmark;
        overrideCollected = 0;
        bombsCollected = 0;
        choiceTriggert = 0;

        //if there is no rival, rivalToBenchmark is 0
        this.rivalToBenchmark = rivalToBenchmark;
        rivalOverrideCollected = 0;
        rivalBombsCollected = 0;
        rivalChoiceTriggert = 0;

        //        moveList= new ArrayList<>();
        matchPoints = new int[playercount];
        playerpoints = new ArrayList<>();
    }

    public void handleServerLine(String line) {
        if (isMoveLine(line)) {
            processMove(line);
        } else if (isFinalAnounce(line)) {
            this.finalScors = true;
        } else if (finalScors && isScoreLine(line)) {
            processPoints(line);
        }
    }

    private void processMove(String line) {
        int[] info = condenseMoveLine(line);
        if (info[0] == playerToBenchmark) {
            if (info[3] == 21) {
                overrideCollected++;
            } else if (info[3] == 20) {
                bombsCollected++;
            } else if (info[3] != 0) {
                choiceTriggert++;
            }
        } else if (info[0] == rivalToBenchmark) {
            if (info[3] == 21) {
                rivalOverrideCollected++;
            } else if (info[3] == 20) {
                rivalBombsCollected++;
            } else if (info[3] != 0) {
                rivalChoiceTriggert++;
            }
        }
    }

    private void processPoints(String line) {
        int[] info = condensePointsLine(line);
        playerpoints.add(new PlayerPoints(info[0], info[1]));
        if (info[0] == playerToBenchmark) {
            this.timeLeft = info[2];
        } else if (info[0] == rivalToBenchmark) {
            this.rivalTimeLeft = info[2];
        }
    }

    public void determineMatchPoints() {
        //need to handle players we did not recive points for (disqualified players)
        for (int i = 0; i < playercount; i++) {
            boolean contains = false;
            for (PlayerPoints pp : playerpoints) {
                if (pp.player == i + 1) {
                    contains = true;
                }
            }
            if (!contains) {
                playerpoints.add(new PlayerPoints(i + 1, -1));
                matchPoints[i] = -50;
            }
        }

        List<List<PlayerPoints>> level = new ArrayList<>();
        int upperBound = Integer.MAX_VALUE;
        for (int i = 0; i < playercount; i++) {
            int scorelevel = 0;
            List<PlayerPoints> workList = new ArrayList<>();
            for (PlayerPoints pp : playerpoints) {
                if (pp.points < upperBound && pp.points != -1) {
                    if (pp.points > scorelevel) {
                        workList.clear();
                        workList.add(pp);
                        scorelevel = pp.points;
                    } else if (pp.points == scorelevel) {
                        workList.add(pp);
                    }
                }
            }
            if (workList.size() > 0) {
                upperBound = workList.get(0).points;
                level.add(workList);
            }
        }
        //if you are lower than rank 5 you get 0 points
        //same rank gets same points
        //two 1. Place ranks means no 2. place but next one is 3. Place
        int rank = 1;
        for (List<PlayerPoints> playerPoints : level) {
            for (PlayerPoints pp : playerPoints) {
                if (pp.points == -1) {
                    matchPoints[pp.player - 1] = -50;
                } else {
                    int mp = 0;
                    switch (rank) {
                        case 1:
                            mp = 25;
                            break;
                        case 2:
                            mp = 11;
                            break;

                        case 3:
                            mp = 5;
                            break;

                        case 4:
                            mp = 2;
                            break;

                        case 5:
                            mp = 1;
                            break;
                    }
                    matchPoints[pp.player - 1] = mp;
                }
            }

            rank = rank + playerPoints.size();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game " + gameID + "\r\n");
        sb.append(mapPath + "\r\n");
        for (PlayerPoints pp : playerpoints) {
            sb.append("Player " + pp.player + " Stones " + pp.points + "\r\n");
        }
        sb.append("Benchmarked: " + playerToBenchmark + " Points " + matchPoints[playerToBenchmark - 1] + "\r\n");
        sb.append("Collected " + overrideCollected + " Overwrites, " + bombsCollected + " Bombs, " + choiceTriggert + " Choice"+ "\r\n");
        sb.append("Had " + timeLeft + "ms left");
        if(rivalToBenchmark!=0){
            sb.append("\r\n");
            sb.append("Rival: " + rivalToBenchmark + " Points " + matchPoints[rivalToBenchmark - 1] + "\r\n");
            sb.append("Collected " + rivalOverrideCollected + " Overwrites, " + rivalBombsCollected + " Bombs, " + rivalChoiceTriggert + " Choice"+ "\r\n");
            sb.append("Had " + rivalTimeLeft + "ms left");
        }
        return sb.toString();
    }

    public static EndStatistic createEndStatistic(Collection<GameStatistic> gameStatistics,boolean forRival){
        EndStatistic endStatistic = new EndStatistic();
        if(!forRival){
            for (GameStatistic gs:gameStatistics) {
                int playernumber = gs.playerToBenchmark;
                int playerstones = 0;
                for (PlayerPoints pp:gs.playerpoints) {
                    if (pp.player == playernumber){
                        playerstones=pp.points;
                        break;
                    }
                }
                endStatistic.add(playerstones,gs.bombsCollected,gs.overrideCollected,gs.choiceTriggert,gs.matchPoints[playernumber-1],gs.timeLeft);
            }
        }else{
            for (GameStatistic gs:gameStatistics) {
                int playernumber = gs.rivalToBenchmark;
                int playerstones = 0;
                for (PlayerPoints pp:gs.playerpoints) {
                    if (pp.player == playernumber){
                        playerstones=pp.points;
                        break;
                    }
                }
                endStatistic.add(playerstones,gs.rivalBombsCollected,gs.rivalOverrideCollected,gs.rivalChoiceTriggert,gs.matchPoints[playernumber-1],gs.rivalTimeLeft);
            }
        }

        return endStatistic;
    }

    private class PlayerPoints {
        public int player;
        public int points;

        public PlayerPoints(int player, int points) {
            this.player = player;
            this.points = points;
        }
    }

}
