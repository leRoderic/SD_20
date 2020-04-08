import main.Server;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;

/**
 * <h1>Server test class</h1>
 * Tests class functions.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   21-03-2020
 */

public class ServerTest extends Server {

    @Test
    /**
     * Tests Server help option.
     */
    public void main_help_test() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"-h"};
        main(args);
       assertEquals(out.toString().trim(), "Usage: java Server -p <port> -i 1|2");
    }

    @Test
    /**
     * Tests the correct number of parameters server needs.
     */
    public void argument_number_test() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"-p", "12"};
        main(args);
        assertEquals(out.toString().trim(), "Invalid parameters. Server parameters are: -p <port> -i 1|2");
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    /**
     * Tests behaviour when entering an invalid game mode.
     */
    public void argument_range_test() throws IOException {

        exit.expectSystemExitWithStatus(1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"-p", "12", "-i", "3"};
        main(args);
    }

    @Test(expected = Exception.class)
    /**
     * Tests behaviour when entering a port number outside the allowed range.
     */
    public void port_range_test() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"-p", "19875453135456", "-i", "2"};
        main(args);
    }
}
