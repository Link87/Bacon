package bacon.net;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void typeEnumValue() {
        for (Message.Type type : Message.Type.values()) {
            assertEquals(type, Message.Type.fromValue(type.getValue()));
        }
    }
}