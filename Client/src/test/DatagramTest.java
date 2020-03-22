import main.Datagram;
import org.junit.Test;
import utils.ComUtils;
import utils.MockSocket;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DatagramTest {

    @Test
    public void erro_format_test() throws IOException {

        MockSocket s = new MockSocket();
        ComUtils utils = new ComUtils(s);

        try {
            utils.write_space();
            utils.write_char((char)(23));
            utils.write_space();
            utils.write_string_variable(23,"This is an awesome error");

            String msg = "This is an awesome error";
            String readed = utils.readErrorMessage();


            assertEquals(msg, readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests STRT datagram format.
     */
    public void start_format_test() throws IOException {

        MockSocket s = new MockSocket();
        ComUtils utils = new ComUtils(s);

        try {
            utils.write_command_pint("STRT",123);

            assertEquals("STRT",utils.read_string());
            utils.read_space();
            assertEquals(123,utils.read_int32());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests STRT datagram format.
     */
    public void pass_format_test() throws IOException {

        MockSocket s = new MockSocket();
        ComUtils utils = new ComUtils(s);

        try {
            utils.write_command_pint("PASS",123);

            assertEquals("PASS",utils.read_string());
            utils.read_space();
            assertEquals(123,utils.read_int32());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests STRT datagram format.
     */
    public void take_format_test() throws IOException {

        MockSocket s = new MockSocket();
        ComUtils utils = new ComUtils(s);

        try {
            int[] sel = new int[]{1,2,3,4,5};
            utils.write_take("TAKE",123,sel);
            int lSel = sel.length;

            assertEquals("TAKE",utils.read_string());
            utils.read_space();
            assertEquals(123, utils.read_int32());
            utils.read_space();
            assertEquals(utils.read_bytes(1)[0],(byte)(lSel));

            byte[] selbyte = new byte[]{1,2,3,4,5};
            for(int i = 0; i < lSel; i++){

                utils.read_space();
                assertEquals(utils.read_bytes(1)[0],selbyte[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test

    /**
     * Tests read command function.
     */
    public void read_command_test() throws IOException {

        MockSocket s = new MockSocket();
        ComUtils utils = new ComUtils(s);

        try {
            String[] commands = {"STRT", "ERRO", "BET", "TAKE", "EXIT"};
            String[] commandsR = {"","","","",""};

            for(int i = 0; i < commands.length; i++){
                utils.write_string(commands[i]);
            }

            for(int i = 0; i < commands.length; i++){
                commandsR[i] =  utils.read_string();
            }

            assertArrayEquals(commands, commandsR);



        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
