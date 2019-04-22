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
        String nl = System.getProperty("line.separator");
        String helpInfo = "usage: bacon [--help] [-s | --server <server> -p | --port <port>]" + nl +
                "-s, --server <host>\t server to connect with (mandatory)" + nl +
                "-p, --port <port>  \t port to connect to (mandatory)" + nl +
                "    --help         \t displays this help text" + nl;

        System.out.println(helpInfo);
    }

}
