package benchmark;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static benchmark.Data.playerCountFromFile;

public class Benchmark {

    private static String mapsLocation = "src/tools/benchmark/maps/";
    private static int totalGames;
    //use "Runtime.getRuntime().availableProcessors()" for the number of your cores (in this default setting)
    //if ai gets disqualified while using multiple threads increasing PancakeWatchdog.SAFTY_GAP might help
    //(but only for benchmarking)
    //this is a setting we need to look closer
    private static int threadCount = 1;
    private static int benchmarkPosition = 1;
    private static String aiToBenchmark = "bacon";
    private static String constraint = "t1";
    private static String fillerAI = "ai.exe";
    private static String rival = "noRival";
    private static String argsB = "";
    private static String argsR = "";
    private static String argsF = "-E";
    private static boolean rivalMode = false;
    private static boolean rivalFarPlaced = false;
    private static boolean saveAiLogs = false;
    private static boolean saveResult = true;

    public static void main(String[] args) {

        adjustSettings(args);

        System.out.println(createArgsLine());

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        ArrayList<Callable<GameStatistic>> jobs = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date startdate = new Date();
        System.out.println("Starttime " + dateFormat.format(startdate));

        File folder = new File(mapsLocation);
        File[] listOfFiles = folder.listFiles();
        totalGames = 0;
        int gameId = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            jobs.add(createGameRunner(gameId, listOfFiles[i].toString(), 7777 + gameId - 1, 1));
            gameId++;
            totalGames++;
            if (rivalMode) {
                jobs.add(createGameRunner(gameId, listOfFiles[i].toString(), 7777 + gameId - 1, 2));
                gameId++;
                totalGames++;
            }
        }

        List<Future<GameStatistic>> gameStatistics = null;
        try {
            gameStatistics = pool.invokeAll(jobs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        List<GameStatistic> stats = new ArrayList<>();
        try {
            for (Future<GameStatistic> gameStatistic : gameStatistics) {
                stats.add(gameStatistic.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        EndStatistic benchStat = GameStatistic.createEndStatistic(stats, false);
        EndStatistic rivalStat = null;
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Statistic for " + aiToBenchmark);
        System.out.println(benchStat.toString());
        if (rivalMode) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            rivalStat = GameStatistic.createEndStatistic(stats, true);
            System.out.println("Statistic for " + rival);
            System.out.println(rivalStat.toString());
        }

        Date endTime = new Date();
        System.out.println("\r\nEndtime " + dateFormat.format(endTime));

        if (saveResult) {
            DateFormat fileSaveDateFormat = new SimpleDateFormat("dd.MM.yyyy#HH-mm-ss");
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/tools/benchmark/benchresults/" + fileSaveDateFormat.format(startdate) + ".txt"));
                writer.write(createArgsLine() + "\r\n");
                writer.write(dateFormat.format(startdate) + "\r\n");
                writer.write(dateFormat.format(endTime) + "\r\n\r\n");
                writer.write(Benchmark.aiToBenchmark + " Statistic:\r\n");
                writer.write(benchStat.toString() + "\r\n");
                writer.write("++++++++++++++++++++++++\r\n");
                if (rivalMode) {
                    writer.write(Benchmark.rival + " Statistic:\r\n");
                    writer.write(rivalStat.toString() + "\r\n");
                    writer.write("++++++++++++++++++++++++\r\n");
                }
                for (GameStatistic gs : stats) {
                    writer.write(gs.toString() + "\r\n");
                    writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~\r\n");
                }
                writer.close();
                System.out.println("Saved statistics in benchresults order!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void adjustSettings(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-constr")) {
                Benchmark.constraint = args[i + 1];
                i++;
            } else if (args[i].equals("-threads")) {
                Benchmark.threadCount = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-bench")) {
                Benchmark.aiToBenchmark = args[i + 1];
                i++;
            } else if (args[i].equals("-argsB")) {
                Benchmark.argsB = args[i + 1];
                i++;
            } else if (args[i].equals("-pos")) {
                Benchmark.benchmarkPosition = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-filler")) {
                Benchmark.fillerAI = args[i + 1];
                i++;
            } else if (args[i].equals("-argsF")) {
                Benchmark.argsF = args[i + 1];
                i++;
            } else if (args[i].equals("-rival")) {
                Benchmark.rivalMode = true;
                Benchmark.rival = args[i + 1];
                i++;
            } else if (args[i].equals("-argsR")) {
                Benchmark.argsR = args[i + 1];
                i++;
            } else if (args[i].equals("-placeRivalFar")) {
                Benchmark.rivalFarPlaced = true;
            } else if (args[i].equals("-saveAiLog")) {
                Benchmark.saveAiLogs = true;
            } else if (args[i].equals("-noSaveResult")) {
                Benchmark.saveResult = false;
            }
        }

    }

    private static String createArgsLine() {
        String booleanArgs = "";
        if (rivalFarPlaced) {
            booleanArgs = booleanArgs.concat("-placeRivalFar ");
        }
        if (saveAiLogs) {
            booleanArgs = booleanArgs.concat("-saveAiLogs ");
        }
        if (!saveResult) {
            booleanArgs = booleanArgs.concat("-noSaveResult ");
        }
        String benchArgs = "";
        if (!Benchmark.argsB.equals("")) {
            benchArgs = " -argsB " + Benchmark.argsB;
        }
        String fillerArgs = "";
        if (!Benchmark.argsF.equals("")) {
            fillerArgs = " -argsF " + Benchmark.argsF;
        }
        String rivalArgs = "";
        if (!Benchmark.argsR.equals("")) {
            rivalArgs = " -argsR " + Benchmark.argsR;
        }
        String rivalStuff = "";
        if (rivalMode) {
            rivalStuff = " -rival " + Benchmark.rival + rivalArgs;
        }
        return "-constr " + Benchmark.constraint + " -threads " + Benchmark.threadCount + " -bench " + Benchmark.aiToBenchmark +
                benchArgs + " -pos " + Benchmark.benchmarkPosition + " -filler " + Benchmark.fillerAI + fillerArgs +
                rivalStuff + " " + booleanArgs;
    }

    private static Callable<GameStatistic> createGameRunner(int gameID, String mapPath, int port, int permutation) {
        final Callable<GameStatistic> callable = new Callable<GameStatistic>() {

            @Override
            public GameStatistic call() throws Exception {
                System.out.println("Starting Game " + gameID + "/" + totalGames);

                int playerToBenchmark = benchmarkPosition;
                int playercount = playerCountFromFile(mapPath);
                //determine actual benchmark position
                playerToBenchmark = playerToBenchmark % playercount;
                if (playerToBenchmark == 0) {
                    playerToBenchmark = playercount;
                }

                //calculate where rival should be run (if it should)
                int rivalPosition = 0;
                if (rivalMode) {
                    if (rivalFarPlaced) {
                        rivalPosition = (int) (playerToBenchmark + Math.ceil(playercount / 2.0)) % playercount;
                        if (rivalPosition == 0) rivalPosition = playercount;
                    } else {
                        //rival near placed
                        rivalPosition = (playerToBenchmark + 1) % playercount;
                        if (rivalPosition == 0) rivalPosition = playercount;
                    }
                    if (permutation == 2) {
                        int helper = rivalPosition;
                        rivalPosition = playerToBenchmark;
                        playerToBenchmark = helper;

                    }
                }

                GameStatistic gameStatistic = new GameStatistic(gameID, mapPath, playercount, playerToBenchmark, rivalPosition);
                Process server = null;

                try {
                    server = new ProcessBuilder("src/tools/benchmark/binary/win/server.exe", mapPath, "-b " + port, "-" + constraint).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Could not start Server!");
                    System.exit(-1);
                }
                Thread readerThread = new Thread(createServerOutputHandler(server.getInputStream(), gameStatistic));
                readerThread.start();

                //run the ais
                for (int i = 1; i <= playercount; i++) {
                    if (i == playerToBenchmark) {
                        runAi(port, Benchmark.aiToBenchmark, Benchmark.argsB);
                    } else if (i == rivalPosition) {
                        runAi(port, Benchmark.rival, Benchmark.argsR);
                    } else {
                        runAi(port, Benchmark.fillerAI, Benchmark.argsF);
                    }
                }

                //wait for reading (and setting up gamestatistic) to end
                try {
                    readerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gameStatistic.determineMatchPoints();

                System.out.println(gameStatistic.toString());
                System.out.println("--------------------------");
                return gameStatistic;
            }

        };
        return callable;
    }

    private static void runAi(int port, String name, String optionalArgs) {
        if (name.equals("bacon")) {
            runJavaAi(port, "bin/jar/bacon.jar", optionalArgs);
        } else if (name.contains("jar")) {
            runJavaAi(port, "src/tools/benchmark/jars/" + name, optionalArgs);
        } else if (name.contains("exe")) {
            try {
                Process ai = new ProcessBuilder("src/tools/benchmark/binary/win/ai.exe", optionalArgs, "-p " + port).start();
                //the read command blocks this thread until ai has started printing -> is ready
                ai.getInputStream().read();
                ai.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runJavaAi(int port, String aiPath, String optionalArgs) {
        try {
            //TODO instead of relying on Ant Target jar in InteliJ config, compile and jar in init (but this turns out to be harder than it seems)
            Process javaAI = new ProcessBuilder("cmd.exe", "/c", "java -jar " + aiPath + " --server localhost --port " + port + " " + optionalArgs).start();
            //the read command blocks this thread until ai has started printing -> is ready
            javaAI.getInputStream().read();
            if (saveAiLogs) {
                Thread logSaver = new Thread(createAiOutputLogger(javaAI.getInputStream(), port - 7776, aiPath));
                logSaver.start();
            } else {
                javaAI.getInputStream().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not start Java Ai: " + aiPath);
            System.exit(-1);
        }
    }

    private static Runnable createServerOutputHandler(final InputStream serverTalk, GameStatistic gameStatistic) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    int data = serverTalk.read();
                    while (data != -1) {

                        if ((char) data == '\n') {
                            //removing nasty "\r"
                            String line = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
//                            System.out.println(line);
                            gameStatistic.handleServerLine(line);
                            stringBuilder.setLength(0);
                        } else {
                            stringBuilder.append((char) data);
                        }

                        data = serverTalk.read();
                    }
                    serverTalk.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }

    private static Runnable createAiOutputLogger(final InputStream inputStream, int number, String path) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    String name = Benchmark.aiToBenchmark;
                    if (path.contains(Benchmark.rival)) {
                        name = Benchmark.rival;
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/tools/benchmark/logs/game_" + number + "_" + name + ".txt"));
                    int data = inputStream.read();
                    while (data != -1) {

                        if ((char) data == '\n') {
                            writer.write("\r" + (char) data);
                        } else {
                            writer.write((char) data);
                        }
                        data = inputStream.read();

                    }
                    inputStream.close();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }

}
