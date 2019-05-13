package bacon;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void fromArgs() {
        try {
            Config.fromArgs(new String[]{"-l"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"--port", "-3"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"--port", "3a"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"-p"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"-s", "localhost", "-p"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"-s", "-localhost"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        try {
            Config.fromArgs(new String[]{"-p", "--no-prune", "7777"});
            fail();
        } catch (IllegalArgumentException ignored) {}

        var valid = Config.fromArgs(new String[]{"-s", "localhost", "-p", "51312"});
        assertNotNull(valid);
        assertEquals("localhost", valid.getHost());
        assertEquals(51312, valid.getPort());
        assertTrue(valid.shouldPrune());

        var valid2 = Config.fromArgs(new String[]{"-s", "localhost", "-p", "51312", "--no-prune"});
        assertNotNull(valid2);
        assertEquals("localhost", valid2.getHost());
        assertEquals(51312, valid2.getPort());
        assertFalse(valid2.shouldPrune());

        var help = Config.fromArgs(new String[]{"--help"});
        assertTrue(help.shouldPrune());

    }
}
