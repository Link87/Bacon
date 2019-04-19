package bacon.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A connection to the game server.
 */
public class ServerConnection implements Closeable {

    private Socket socket;

    /**
     * Creates a ServerConnection to the given host at the given port.
     * The port is used on this client and on the server as well.
     *
     * @param host host to connect to
     * @param port port to use on both server and client
     */
    public ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }

    /**
     * Returns a message send from the server. Blocks until the message is received.
     *
     * @return the received message
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
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Sends the given message to the server.
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
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the ServerConnection. The connection can't be reopened.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
