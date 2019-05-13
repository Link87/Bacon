package bacon;

/**
 * This class is the config passed from the command line.
 */
public class Config {

    private final boolean helpRequested;

    private final String host;
    private final int port;
    private final boolean noPrune;

    private Config() {
        this.helpRequested = true;

        this.host = null;
        this.port = 0;
        this.noPrune = false;
    }

    private Config(String host, int port, boolean noPrune) {
        this.host = host;
        this.port = port;
        this.noPrune = noPrune;

        this.helpRequested = false;

    }

    /**
     * Returns the port number. The number is unchecked and may be invalid.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the destination host name. The host is not checked and may be invalid.
     *
     * @return the host name
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns whether pruning should be used in the search tree.
     *
     * @return <code>true</code> if pruning should be used, <code>false</code> otherwise
     */
    public boolean isPruningEnabled() {
        return !noPrune;
    }

    /**
     * Returns whether the user asked for a help text.
     * When <code>true</code>, the other values are undefined and may be set arbitrarily!
     *
     * @return <code>true</code> if help should be displayed, <code>false</code> otherwise
     */
    public boolean isHelpRequested() {
        return helpRequested;
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
            boolean noPrune = false;

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
            return new Config(host, port, noPrune);
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
