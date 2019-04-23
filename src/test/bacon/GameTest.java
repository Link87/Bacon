package bacon;

import bacon.net.Message;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.*;

public class GameTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void processMessage() {
        Game.getGame().processMessage(new Message(Message.Type.MAP_CONTENT, Maps.EXAMPLE.getBytes()));
        assertExampleData();
    }

    @Test
    public void startGame() throws IOException {
        final Config cfg = Config.fromArgs(new String[]{"-p", "51312", "-s", "localhost"});
        assertNotNull(cfg);

        try (var listener = new ServerSocket(cfg.getPort())) {

            new Thread(() -> {
                try {
                    var socket = listener.accept();
                    var in = new DataInputStream(socket.getInputStream());
                    assertEquals(Message.Type.GROUP_NUMBER, Message.Type.fromValue(in.readByte()));
                    assertEquals(1, in.readInt());
                    assertEquals(6, in.readByte());

                    var out = new DataOutputStream(socket.getOutputStream());
                    out.writeByte(Message.Type.MAP_CONTENT.getValue());
                    out.writeInt(Maps.EXAMPLE.getBytes().length);
                    out.write(Maps.EXAMPLE.getBytes());

                    out.writeByte(Message.Type.PLAYER_NUMBER.getValue());
                    out.writeInt(1);
                    out.writeByte(1);

                    out.writeByte(Message.Type.GAME_END.getValue());
                    out.writeInt(0);

                } catch (IOException ioe) {
                    fail(ioe.getMessage());
                }
            }).start();

            Game.getGame().startGame(cfg);
            assertExampleData();
        }
    }

    @Test
    public void readMap() {
        Game.getGame().readMap(Maps.EXAMPLE);
        assertExampleData();

    }

    private void assertExampleData() {
        var state = Game.getGame().getCurrentState();
        assertEquals(3, state.getTotalPlayerCount());
        assertEquals(6, state.getPlayerFromNumber(1).getOverrideStoneCount());
        assertEquals(4, state.getPlayerFromNumber(1).getBombCount());
        assertEquals(2, state.getBombRadius());
        assertEquals(15, state.getMap().height);
        assertEquals(15, state.getMap().width);
    }
}