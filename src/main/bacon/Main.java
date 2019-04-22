package bacon;

public class Main {

    public static void main(String[] args) {

        Config config = null;
        try {
            config = Config.fromArgs(args);
            // print help if help should be displayed and exit
            if (config == null) {
                printHelp();
                System.exit(0);
            }
            // TODO otherwise start program with config

        } catch (IllegalArgumentException iae) {
            System.err.println("Sorry, I don't understand that.");
            printHelp();
            System.exit(1);
        }

        Game.getGame().startGame(config);

    }

    /**
     * Prints the cli help information for this program.
     */
    private static void printHelp() {
        String helpInfo = "";

        System.out.println(helpInfo);
    }

}
