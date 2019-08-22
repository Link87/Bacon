package bacon;

/**
 * Contains the configuration data passed from command line.
 */
public class Config {

    // Default values. Those are used if no fitting argument is passed.
    private static final boolean PRUNING_DEFAULT = true;
    private static final boolean MOVE_SORTING_DEFAULT = true;
    private static final int BEAM_WIDTH_DEFAULT = 0;
    private static final boolean ASPIRATION_WINDOWS_DEFAULT = false;
    private static final boolean ENABLE_ERR_DEFAULT = false;
    private static final int RAND_ROLL_FREQUENCY_DEFAULT = 10;
    private static final double RAND_ROLL_TIME_BUDGET_DEFAULT = 0.3;
    private static final int MAX_RAND_ROLL_ITERATIONS_DEFAULT = 20;

    private final boolean helpRequested;
    private final String host;
    private final int port;
    private final boolean pruning;
    private final boolean moveSorting;
    private final int beamWidth;
    private final boolean aspirationWindows;
    private final boolean enableErr;

    private final int randRollFrequency;
    private final double randRollTimeBudget;
    private final int maxRandRollIterations;

    /**
     * Creates an empty configuration, that only indicates a help request via {@code --help}.
     * <p>
     * Do not attempt to read configuration data from a {@code Config} created with this constructor.
     * The values are assigned arbitrarily and do not reflect the command line arguments that may have been passed to the ai.
     */
    private Config() {
        this.helpRequested = true;

        this.host = null;
        this.port = 0;
        this.pruning = PRUNING_DEFAULT;
        this.moveSorting = MOVE_SORTING_DEFAULT;
        this.beamWidth = BEAM_WIDTH_DEFAULT;
        this.aspirationWindows = ASPIRATION_WINDOWS_DEFAULT;
        this.enableErr = ENABLE_ERR_DEFAULT;

        this.randRollFrequency = RAND_ROLL_FREQUENCY_DEFAULT;
        this.randRollTimeBudget = RAND_ROLL_TIME_BUDGET_DEFAULT;
        this.maxRandRollIterations = MAX_RAND_ROLL_ITERATIONS_DEFAULT;
    }

    /**
     * Creates a new {@code Config} with the given configuration data.
     *
     * @param host              host name to connect to
     * @param port              port number to connect to
     * @param pruning           {@code true} if pruning should be used, {@code false} otherwise
     * @param moveSorting       {@code true} if move sorting should be used, {@code false} otherwise
     * @param beamWidth         width of beam used in beam search or {@code 0} if no beam search should be done.
     *                          This is ignored if {@code moveSorting} is set to {@code false}.
     * @param aspirationWindows {@code true} if aspiration windows should be used, {@code false} otherwise
     * @param rolloutFrequency  number of turns after which to do a random rollout
     * @param enableErr         {@code true} if errors and warnings should be printed to {@code stderr}, {@code false} otherwise.
     *                          Use this when running the ai locally.
     */
    public Config(String host, int port, boolean pruning, boolean moveSorting, int beamWidth, boolean aspirationWindows, int rolloutFrequency, boolean enableErr) {
        this.host = host;
        this.port = port;
        this.pruning = pruning;
        this.moveSorting = moveSorting;
        this.beamWidth = beamWidth;
        this.aspirationWindows = aspirationWindows;
        this.enableErr = enableErr;

        this.helpRequested = false;

        this.randRollFrequency = rolloutFrequency;
        this.randRollTimeBudget = RAND_ROLL_TIME_BUDGET_DEFAULT;
        this.maxRandRollIterations = MAX_RAND_ROLL_ITERATIONS_DEFAULT;

    }

    /**
     * Parses the arguments and returns the config.
     *
     * @param args array containing the command line arguments
     * @return config with parsed arguments or {@code null} when help is requested
     * @throws IllegalArgumentException when invalid arguments are passed
     */
    static Config fromArgs(String[] args) throws IllegalArgumentException {
        return new Parser().parseArgs(args);
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
     * When {@code true}, the other values are undefined and may be set arbitrarily!
     *
     * @return {@code true} if help should be displayed, {@code false} otherwise
     */
    boolean isHelpRequested() {
        return helpRequested;
    }

    /**
     * Returns whether pruning should be used in the search tree.
     *
     * @return {@code true} if pruning should be used, {@code false} otherwise
     */
    public boolean isPruningEnabled() {
        return pruning;
    }

    /**
     * Returns whether moves should be sorted and traversed in order.
     *
     * @return {@code true} if move sorting should be used, {@code false} otherwise
     */
    public boolean isMoveSortingEnabled() {
        return moveSorting;
    }

    /**
     * Returns the width of the beam used in beam search. Returns {@code 0} if beam search is turned off.
     * <p>
     * This value is ignored if move sorting is turned off entirely.
     *
     * @return width of beam used in beam search or {@code 0} if no beam search should be done
     */
    public int getBeamWidth() {
        return beamWidth;
    }

    /**
     * Returns whether aspiration windows should be used.
     *
     * @return {@code true} if aspiration are enabled, {@code false} otherwise
     */
    public boolean isAspirationWindowsEnabled() {
        return aspirationWindows;
    }

    /**
     * Returns whether to use the {@code stderr} stream for error logging.
     * <p>
     * Use this when running the ai locally. On the server this setting would instead lead to inconsistent logging.
     *
     * @return {@code true} if errors and warnings should be printed to {@code stderr}, {@code false} otherwise
     */
    boolean isErrEnabled() {
        return enableErr;
    }

    /**
     * Returns the frequency of random rollouts.
     *
     * @return frequency of random rollouts
     */
    public int getRandRollFrequency() {
        return randRollFrequency;
    }

    /**
     * Returns the time budget for a single rollout.
     *
     * @return the maximum time that should be used for random rollouts in milliseconds
     */
    public double getRandRollTimeBudget() {
        return randRollTimeBudget;
    }

    /**
     * Returns the maximum amount of times, random rollouts should be done at once.
     *
     * @return the maximum number of random rollout iterations
     */
    public int getMaxRandRollIterations() {
        return maxRandRollIterations;
    }

    /**
     * Parser that parses command line arguments and returns a {@link Config}.
     */
    private static class Parser {

        /**
         * Parses the given arguments and returns a {@link Config} with the according data.
         *
         * @param args array containing the command line arguments
         * @return config with parsed arguments or {@code null} when help is requested
         * @throws IllegalArgumentException when invalid arguments are passed
         */
        private Config parseArgs(String[] args) throws IllegalArgumentException {
            /* This parser works as a state machine. CLI switches without arguments are applied directly.
            Otherwise, the parser changes into an expecting state in which only a fitting argument value is accepted. */

            String host = null;
            int port = -1;
            boolean pruning = PRUNING_DEFAULT;
            boolean moveSorting = MOVE_SORTING_DEFAULT;
            int beamWidth = BEAM_WIDTH_DEFAULT;
            boolean enableErr = ENABLE_ERR_DEFAULT;
            boolean aspiration = ASPIRATION_WINDOWS_DEFAULT;
            int rollout = RAND_ROLL_FREQUENCY_DEFAULT;

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
                            case "--rollout":
                                expect = State.EXPECT_ROLLOUT_FREQ;
                                break;
                            case "--no-rollout":
                                rollout = 0;
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
                    case EXPECT_ROLLOUT_FREQ:
                        // read rollout frequency
                        if (arg.charAt(0) == '-')
                            throw new IllegalArgumentException();
                        try {
                            rollout = Integer.parseInt(arg);
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
            return new Config(host, port, pruning, moveSorting, beamWidth, aspiration, rollout, enableErr);
        }

        /**
         * The states the {@code Parser} can be in.
         */
        private enum State {
            EXPECT_ARG,
            EXPECT_HOST,
            EXPECT_PORT,
            EXPECT_BEAM_WIDTH,
            EXPECT_ROLLOUT_FREQ,
        }
    }
}
