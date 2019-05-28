package benchmark;

import java.text.DecimalFormat;

public class EndStatistic {
    public int totalGames;

    public int stones;
    public int bombs;
    public int overwrites;
    public int choice;
    public int matchPoints;
    public int disq;
    public int timeLeft;
    public int wins;

    public EndStatistic(){
        this.totalGames=0;

        this.stones=0;
        this.bombs =0;
        this.overwrites=0;
        this.choice =0;
        this.matchPoints =0;
        this.disq =0;
        this.timeLeft =0;
        this.wins=0;
    }

    public void add(int stones,int bombs, int overwrites, int choice, int points,int timeLeft){
        totalGames++;
        if(stones != -1){
            this.stones += stones;
        }
        if (points == 25) {
            wins++;
        } else if(points == -50){
            disq++;
        }
        if(timeLeft != -1){
            this.timeLeft += timeLeft;
        }
        this.matchPoints += points;
        this.bombs += bombs;
        this.overwrites += overwrites;
        this.choice += choice;
    }

    public String toString(){
        DecimalFormat df = new DecimalFormat("###.##");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(matchPoints+ " out of "+25*totalGames+ " possible Points (" + df.format((matchPoints/((double)totalGames*25))*100)+"%)\r\n");
        stringBuilder.append(wins+" out of "+totalGames+ " first Place ("+ df.format((wins/(double)totalGames)*100)+"%)\r\n");
        stringBuilder.append("Got "+overwrites+" Overwrites, "+bombs+" Bombs, "+choice+" Choice\r\n");
        stringBuilder.append("Total Stones "+stones+" Time left "+timeLeft+"\r\n");
        stringBuilder.append("Disqualified "+disq+" times");
        return stringBuilder.toString();
    }
}
