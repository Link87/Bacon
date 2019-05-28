package bacon;

import java.util.Arrays;
import java.util.logging.*;

public class Main {

    private static final Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) {

        Config config = null;
        try {
            config = Config.fromArgs(args);
            // print help if help should be displayed and exit
            if (config.isHelpRequested()) {
                printHelp();
                System.exit(0);
            }

        } catch (IllegalArgumentException iae) {
            System.err.println("Sorry, I don't understand that.");
            printHelp();
            System.exit(1);
        }

        // Replace all present root logger handler with our own ConsoleHandler
        LOGGER.setLevel(Level.FINE);
        Arrays.stream(Logger.getLogger("").getHandlers()).forEach(value -> Logger.getLogger("").removeHandler(value));
        DualConsoleHandler handler = new DualConsoleHandler(new PrivacyFormatter(), config.isErrEnabled());
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);

        LOGGER.log(Level.INFO, "\n" +
                "                                                                       \n" +
                " ,,                                                                    \n" +
                "*MM                                                      db      `7MMF'\n" +
                " MM                                                     ;MM:       MM  \n" +
                " MM,dMMb.   ,6\"Yb.  ,p6\"bo   ,pW\"Wq.`7MMpMMMb.         ,V^MM.      MM  \n" +
                " MM    `Mb 8)   MM 6M'  OO  6W'   `Wb MM    MM        ,M  `MM      MM  \n" +
                " MM     M8  ,pm9MM 8M       8M     M8 MM    MM        AbmmmqMA     MM  \n" +
                " MM.   ,M9 8M   MM YM.    , YA.   ,A9 MM    MM  ,,   A'     VML    MM  \n" +
                " P^YbmdP'  `Moo9^Yo.YMbmd'   `Ybmd9'.JMML  JMML.db .AMA.   .AMMA..JMML.\n" +
                "                                                                       \n" +
                " welcome to our secrets...\n" +
                "                                                                       \n" +
                "🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓🥓\n");

        LOGGER.log(Level.INFO, "Going to connect to {0}:{1}", new Object[]{config.getHost(), config.getPort()});
        LOGGER.log(Level.INFO, "Alpha-Beta-Pruning enabled: {0}", config.isPruningEnabled());
        LOGGER.log(Level.INFO, "Move-Sorting enabled: {0}", config.isMoveSortingEnabled());
        LOGGER.log(Level.INFO, "Number of kittens that were harmed during development: {0}", 0);

        Game.getGame().startGame(config);

    }

    /**
     * Prints the cli help information for this program.
     */
    private static void printHelp() {
        String nl = System.getProperty("line.separator");
        String helpInfo =
                "usage: bacon [--help] [-s <server> | --server <server> -p <port> | --port <port> [--no-prune]" + nl +
                "             [--no-sort] [-b <width> | --beam <width> | --no-beam] [--err]]" + nl +
                "-s, --server <host>\t server to connect with (mandatory)" + nl +
                "-p, --port <port>  \t port to connect to (mandatory)" + nl +
                "    --no-prune     \t disable alpha-beta-pruning" + nl +
                "    --no-sort      \t disable move sorting entirely" + nl +
                "-b, --beam <width> \t set beam width for forward pruning" + nl +
                "    --no-beam      \t disable beam search, same as '-b 0'" + nl +
                "    --err          \t write errors and warnings to stderr" + nl +
                "    --help         \t display this help text" + nl;

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

    /**
     * A {@link Handler} that prints INFO and below to System.out and everything above to System.err.
     */
    public static class DualConsoleHandler extends StreamHandler {

        private final ConsoleHandler stderrHandler = new ConsoleHandler();
        private final boolean printToErr;

        DualConsoleHandler(Formatter fmt, boolean printToErr) {
            super(System.out, fmt);
            this.printToErr = printToErr;
            this.stderrHandler.setFormatter(fmt);
        }

        @Override
        public void publish(LogRecord record) {
            if (this.printToErr && record.getLevel().intValue() > Level.INFO.intValue()) {
                this.stderrHandler.publish(record);
                this.stderrHandler.flush();
            } else {
                super.publish(record);
                super.flush();
            }
        }
    }

}
