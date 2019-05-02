package bacon;

import java.util.Arrays;
import java.util.logging.*;

public class Main {

    private static final Logger LOGGER = Logger.getGlobal();


    public static void main(String[] args) {

        // Replace all present root logger handler with our own ConsoleHandler
        LOGGER.setLevel(Level.FINE);
        Arrays.stream(Logger.getLogger("").getHandlers()).forEach(value -> Logger.getLogger("").removeHandler(value));
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new PrivacyFormatter());
        LOGGER.addHandler(handler);

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

    /**
     * A {@link Formatter} that only emits the log level and message to minimize the information other teams can get.
     */
    public static class PrivacyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel() + ": " + formatMessage(record) + "\n";
        }
    }

}
