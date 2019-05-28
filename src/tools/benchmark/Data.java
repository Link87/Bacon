package benchmark;

import bacon.GameState;
import bacon.Player;
import bacon.move.BonusRequest;
import bacon.move.Move;
import bacon.move.MoveFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Data {

    public static int[] condensePointsLine(String line){
        int[] out = new int[3];
        String[] elem = line.split(" ");
        out[0]=Integer.parseInt(elem[1].substring(0,elem[1].length()-1));
        if(line.contains("disqualified")){
            out[1]=-1;
            out[2]=0;
            return out;
        }
        out[1]=Integer.parseInt(elem[2]);
        //if line contains timeinformation
        if(line.contains("remaining")){
        out[2]=Integer.parseInt(elem[10].substring(0,elem[10].length()-4));
        }else {
            out[2]=-1;
        }

        return out;
    }

    public static int[] condenseMoveLine(String line){
        int[] out = new int[4];
        String[] elem = line.split(" ");
        out[0]=Integer.parseInt(elem[7]);

        String cords = elem[10];
        cords = cords.substring(1);
        cords = cords.replace(").",")");
        cords = cords.substring(0,cords.length()-1);
        String[] cordsAr = cords.split(",");
        out[1]= Integer.parseInt(cordsAr[0]);
        out[2]= Integer.parseInt(cordsAr[1]);

        if(line.contains("choice")){
            if(elem[12].contains("bomb")){
                out[3]=20;
            }else if(elem[12].contains("override")){
                out[3]=21;
            }else{
                out[3]=Integer.parseInt(elem[13].substring(0,elem[13].length()-1));
            }
        } else{
            out[3]=0;
        }

        return out;
    }
/*
    public static Move moveFromArray(GameState currentState, int[] elem) {
        Player player = currentState.getPlayerFromNumber(elem[0]);
        return MoveFactory.createMove(currentState, player, elem[1], elem[2],
                BonusRequest.fromValue(elem[3], currentState));
    }
*/
    public static String mapFromFile(String filePath){
        StringBuilder sB = new StringBuilder();
        Scanner scan = null;
        try {
            scan = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not read Map!");
            System.exit(-1);
        }
        while (scan.hasNextLine()){
            sB.append(scan.nextLine());
        }
        return (sB.toString());
    }

    public static int playerCountFromFile(String filePath){
        try {
            Scanner scan = new Scanner(new File(filePath));
            return scan.nextInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not read Map!");
            System.exit(-1);
        }
        return -1;
    }

    public static boolean isMoveLine(String line){
        if (line.contains("Received")){
            return true;
        }
        return false;
    }

    public static boolean isScoreLine(String line){
        if(line.contains("points")){
            return true;
        }
        return false;
    }

    public static boolean isFinalAnounce(String line){
        if(line.contains("Final")){
            return true;
        }
        return false;
    }
}
