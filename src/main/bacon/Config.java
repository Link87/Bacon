package bacon;

/**
 * This class is the config passed from the command line.
 */
public class Config {

    private String host;
    private int port;

    private Config(String host, int port) {
        this.host = host;
        this.port = port;
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
     * Parses the arguments and returns the config.
     * Returns <code>null</code> when help should be displayed.
     *
     * @param args array containing the command line arguments
     * @return config with parsed arguments or <code>null</code> when help is requested
     * @throws IllegalArgumentException when invalid arguments are passed
     */
    public static Config fromArgs(String[] args) throws IllegalArgumentException {

        String host = null;
        int port = -1;
        // the type of token that is expected to follow -- state machine lite
        ParserState expect = ParserState.EXPECT_ARG;

        for (String arg : args) {
            switch (expect) {
                case EXPECT_ARG:
                    // handle argument and expect corresponding value to follow, if applicable
                    switch (arg) {
                        case "--help":
                        case "-h":
                            return null;
                        case "--server":
                        case "-s":
                            expect = ParserState.EXPECT_HOST;
                            break;
                        case "--port":
                        case "-p":
                            expect = ParserState.EXPECT_PORT;
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
                    expect = ParserState.EXPECT_ARG;
                    break;
                case EXPECT_HOST:
                    // set host and expect another argument to follow
                    if (arg.charAt(0) == '-')
                        throw new IllegalArgumentException();
                    host = arg;
                    expect = ParserState.EXPECT_ARG;
                    break;
            }
        }

        // host and port have to be both present
        if (host == null || port == -1)
            throw new IllegalArgumentException();
        return new Config(host, port);
    }

    /**
     * The states the parser can be in.
     */
    private enum ParserState {
        EXPECT_ARG,
        EXPECT_HOST,
        EXPECT_PORT
    }
}
