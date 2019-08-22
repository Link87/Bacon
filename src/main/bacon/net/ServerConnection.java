package bacon.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A connection to the game server.
 * <p>
 * A connection is opened upon creation of a new {@code ServerConnection} object. The connection has to be closed
 * by calling {@link ServerConnection#close()}
 */
public class ServerConnection implements Closeable {

    private static final Logger LOGGER = Logger.getGlobal();

    private Socket socket;

    /**
     * Creates a {@code ServerConnection} to the given host at the given port.
     * <p>
     * The port is used on this client and on the server as well.
     *
     * @param host name of host to connect to
     * @param port port to connect to
     * @throws IOException if an network error of any kind occurs
     */
    public ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }

    /**
     * Returns a {@link Message} send from the server.
     * <p>
     * Blocks until the {@code Message} is received.
     *
     * @return the received message deserialized into a {@code Message}
     */
    public Message awaitMessage() {
        try {
            var input = new DataInputStream(socket.getInputStream());
            var type = Message.Type.fromValue(input.readByte());
            var length = input.readInt();
            var data = new byte[length];
            input.readFully(data);

            return new Message(type, data);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString());
            return null;
        }
    }

    /**
     * Sends the given {@link Message} to the server.
     *
     * @param msg message to send
     */
    public void sendMessage(Message msg) {
        try {
            var output = new DataOutputStream(socket.getOutputStream());
            output.writeByte(msg.getType().getValue());
            output.writeInt(msg.getBinaryContent().length);
            output.write(msg.getBinaryContent());
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString());
        }
    }

    /**
     * Closes the {@code ServerConnection}. The connection can't be reopened.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString());
        }
    }

}
