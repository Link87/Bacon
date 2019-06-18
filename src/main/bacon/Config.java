package bacon;

/**
 * This class is the config passed from the command line.
 */
public class Config {

    private final boolean helpRequested;

    private final String host;
    private final int port;
    private final boolean pruning;
    private final boolean moveSorting;
    private final int beamWidth;
    private final boolean aspirationWindows;
    private final boolean enableErr;

    private static final boolean PRUNING_DEFAULT = true;
    private static final boolean MOVE_SORTING_DEFAULT = true;
    private static final int BEAM_WIDTH_DEFAULT = 5;
    private static final boolean ASPIRATION_WINDOWS_DEFAULT = true;
    private static final boolean ENABLE_ERR_DEFAULT = false;

    private Config() {
        this.helpRequested = true;

        this.host = null;
        this.port = 0;
        this.pruning = PRUNING_DEFAULT;
        this.moveSorting = MOVE_SORTING_DEFAULT;
        this.beamWidth = BEAM_WIDTH_DEFAULT;
        this.aspirationWindows = ASPIRATION_WINDOWS_DEFAULT;
        this.enableErr = ENABLE_ERR_DEFAULT;
    }

    private Config(String host, int port, boolean pruning, boolean moveSorting, int beamWidth, boolean aspirationWindows, boolean enableErr) {
        this.host = host;
        this.port = port;
        this.pruning = pruning;
        this.moveSorting = moveSorting;
        this.beamWidth = beamWidth;
        this.aspirationWindows = aspirationWindows;
        this.enableErr = enableErr;

        this.helpRequested = false;

    }

    public Config(boolean pruning, boolean moveSorting, int beamWidth, boolean aspirationWindows){
        this.host = null;
        this.port = 0;
        this.pruning = pruning;
        this.moveSorting = moveSorting;
        this.beamWidth = beamWidth;
        this.aspirationWindows = aspirationWindows;
        this.enableErr = false;
        this.helpRequested = true;
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
        return pruning;
    }

    public boolean isMoveSortingEnabled() {
        return moveSorting;
    }

    public int getBeamWidth() {
        return beamWidth;
    }

    /**
     * Returns whether aspiration windows should be used.
     *
     * @return <code>true</code> if aspiration are enabled, <code>false</code> otherwise
     */
    public boolean isAspirationWindowsEnabled() {
        return aspirationWindows;
    }

    /**
     * Returns whether to use the stderr stream for error logging.
     *
     * @return <code>true</code> if errors and warnings should be printed to stderr, <code>false</code> otherwise
     */
    boolean isErrEnabled() {
        return enableErr;
    }

    /**
     * Parses the arguments and returns the config.
     *
     * @param args array containing the command line arguments
     * @return config with parsed arguments or <code>null</code> when help is requested
     * @throws IllegalArgumentException when invalid arguments are passed
     */
    static Config fromArgs(String[] args) throws IllegalArgumentException {
        return new Parser().parseArgs(args);
    }

    private static class Parser {

        private Config parseArgs(String[] args) throws IllegalArgumentException {
            String host = null;
            int port = -1;
            boolean pruning = PRUNING_DEFAULT;
            boolean moveSorting = MOVE_SORTING_DEFAULT;
            int beamWidth = BEAM_WIDTH_DEFAULT;
            boolean enableErr = ENABLE_ERR_DEFAULT;
            boolean aspiration = ASPIRATION_WINDOWS_DEFAULT;

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
                                pruning = false;
                                break;
                            case "--no-sort":
                                moveSorting = false;
                                break;
                            case "--beam":
                            case "-b":
                                expect = State.EXPECT_BEAM_WIDTH;
                                break;
                            case "--no-beam":
                                beamWidth = 0;
                                break;
                            case "--asp":
                                aspiration = true;
                                break;
                            case "--no-asp":
                                aspiration = false;
                                break;
                            case "--err":
                                enableErr = true;
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
                    case EXPECT_BEAM_WIDTH:
                        // read beam width and expect another argument to follow
                        if (arg.charAt(0) == '-')
                            throw new IllegalArgumentException();
                        try {
                            beamWidth = Integer.parseInt(arg);
                        } catch (NumberFormatException nfe) {
                            throw new IllegalArgumentException();
                        }
                        expect = State.EXPECT_ARG;
                        break;
                }
            }

            // host and port have to be both present
            if (host == null || port == -1)
                throw new IllegalArgumentException();
            return new Config(host, port, pruning, moveSorting, beamWidth, aspiration, enableErr);
        }

        /**
         * The states the parser can be in.
         */
        private enum State {
            EXPECT_ARG,
            EXPECT_HOST,
            EXPECT_PORT,
            EXPECT_BEAM_WIDTH,
        }
    }
}
