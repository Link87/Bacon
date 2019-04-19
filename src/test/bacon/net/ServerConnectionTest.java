package bacon.net;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

public class ServerConnectionTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void awaitMessage() throws IOException {
        final int port = 51312;

        try (var listener = new ServerSocket(port)) {
            new Thread(() -> {
                try {
                    var socket = listener.accept();
                    var out = new DataOutputStream(socket.getOutputStream());
                    out.writeByte(Message.Type.DISQUALIFICATION.getValue());
                    out.writeInt(9);
                    out.write("blah blub".getBytes());
                } catch (IOException ioe) {
                    fail(ioe.getMessage());
                }
            }).start();

            try(var connection = new ServerConnection("localhost", port)) {
                Message message = connection.awaitMessage();
                assertEquals("blah blub", new String(message.getBinaryContent(), StandardCharsets.US_ASCII));
                assertEquals(Message.Type.DISQUALIFICATION, message.getType());
            }
        }

    }

    @Test
    public void sendMessage() throws IOException, InterruptedException {
        final Semaphore sem = new Semaphore(1);
        sem.acquire();

        final int port = 51312;

        try (var listener = new ServerSocket(port)) {

            new Thread(() -> {
                try {
                    var socket = listener.accept();
                    var in = new DataInputStream(socket.getInputStream());
                    assertEquals(Message.Type.GROUP_NUMBER, Message.Type.fromValue(in.readByte()));
                    assertEquals(1, in.readInt());
                    var groupNo = new byte[1];
                    in.readFully(groupNo);
                    assertArrayEquals(new byte[]{6}, groupNo);
                    sem.release();
                } catch (Exception ioe) {
                    fail(ioe.getMessage());
                }

            }).start();

            try(var connection = new ServerConnection("localhost", port)) {
                connection.sendMessage(new Message(Message.Type.GROUP_NUMBER, new byte[]{6}));
                sem.acquire();
            }
        }
    }
}