package bacon;

/**
 * This class is the config passed from the command line.
 */
public class Config {

    private final boolean helpRequested;

    private final String host;
    private final int port;
    private final boolean noPrune;
    private final boolean moveSorting;
    private final int beamWidth;

    private static final boolean NO_PRUNE_DEFAULT = false;
    private static final boolean MOVE_SORTING_DEFAULT = true;
    private static final int BEAM_WIDTH_DEFAULT = 5;

    private Config() {
        this.helpRequested = true;

        this.host = null;
        this.port = 0;
        this.noPrune = false;
        this.moveSorting = false;
        this.beamWidth = 0;
    }

    private Config(String host, int port, boolean noPrune, boolean moveSorting, int beamWidth) {
        this.host = host;
        this.port = port;
        this.noPrune = noPrune;
        this.moveSorting = moveSorting;
        this.beamWidth = beamWidth;

        this.helpRequested = false;

    }

    /**
     * Returns the port number. The number is unchecked and may be invalid.
     *
     * @return the port number
     */
    int getPort() {
        return port;
    }

    /**
     * Returns the destination host name. The host is not checked and may be invalid.
     *
     * @return the host name
     */
    String getHost() {
        return host;
    }

    /**
     * Returns whether the user asked for a help text.
     * When <code>true</code>, the other values are undefined and may be set arbitrarily!
     *
     * @return <code>true</code> if help should be displayed, <code>false</code> otherwise
     */
    boolean isHelpRequested() {
        return helpRequested;
    }

    /**
     * Returns whether pruning should be used in the search tree.
     *
     * @return <code>true</code> if pruning should be used, <code>false</code> otherwise
     */
    public boolean isPruningEnabled() {
        return !noPrune;
    }

    public boolean isMoveSortingEnabled() {
        return moveSorting;
    }

    public int getBeamWidth() {
        return beamWidth;
    }

    /**
     * Parses the arguments and returns the config.
     *
     * @param args array containing the command line arguments
     * @return config with parsed arguments or <code>null</code> when help is requested
     * @throws IllegalArgumentException when invalid arguments are passed
     */
    public static Config fromArgs(String[] args) throws IllegalArgumentException {
        return new Parser().parseArgs(args);
    }

    private static class Parser {

        private Config parseArgs(String[] args) throws IllegalArgumentException {
            String host = null;
            int port = -1;
            boolean noPrune = NO_PRUNE_DEFAULT;
            boolean moveSorting = MOVE_SORTING_DEFAULT;
            int beamWidth = BEAM_WIDTH_DEFAULT;

            // the type of token that is expected to follow -- state machine lite
            State expect = State.EXPECT_ARG;

            for (String arg : args) {
                switch (expect) {
                    case EXPECT_ARG:
                        // handle argument and expect corresponding value to follow, if applicable
                        switch (arg) {
                            case "--help":
                            case "-h":
                                return new Config();
                            case "--server":
                            case "-s":
                                expect = State.EXPECT_HOST;
                                break;
                            case "--port":
                            case "-p":
                                expect = State.EXPECT_PORT;
                                break;
                            case "--no-prune":
                                noPrune = true;
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                        break;
                    case EXPECT_PORT:
                        // set port and expect another argument to follow
                        if (arg.charAt(0) == '-')
                            throw new IllegalArgumentException();
                        try {
                            port = Integer.parseInt(arg);
                        } catch (NumberFormatException nfe) {
                            throw new IllegalArgumentException();
                        }
                        expect = State.EXPECT_ARG;
                        break;
                    case EXPECT_HOST:
                        // set host and expect another argument to follow
                        if (arg.charAt(0) == '-')
                            throw new IllegalArgumentException();
                        host = arg;
                        expect = State.EXPECT_ARG;
                        break;
                }
            }

            // host and port have to be both present
            if (host == null || port == -1)
                throw new IllegalArgumentException();
            return new Config(host, port, noPrune, moveSorting, beamWidth);
        }

        /**
         * The states the parser can be in.
         */
        private enum State {
            EXPECT_ARG,
            EXPECT_HOST,
            EXPECT_PORT
        }
    }
}
