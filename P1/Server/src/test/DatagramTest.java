import main.Datagram;
import org.junit.Test;
import utils.ComUtils;
import utils.MockSocket;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DatagramTest {

    @Test
    /**
     * Tests ERRO datagram format.
     */
    public void erro_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            String msg = "This is an awesome error";
            dtg.sendErrorMessage(msg, msg.length());

            assertEquals("ERRO", utils.read_string());
            utils.read_space();
            int len = Integer.parseInt(utils.read_char());
            assertEquals("" + msg.length(), "" + len);
            utils.read_space();
            assertEquals(msg, utils.read_string_variable(len));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests DICE datagram format.
     */
    public void dice_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            int[] dice = {1,2,3,4,5}, diceR = {-1,-1,-1,-1,-1};
            dtg.dice(80, dice);

            assertEquals("DICE", utils.read_string());
            assertEquals(80, utils.read_nextInt());

            for(int i=0; i < 5; i++){
                utils.read_space();
                diceR[i] = Integer.parseInt(utils.read_char());
            }
            assertArrayEquals(dice, diceR);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests CASH datagram format.
     */
    public void cash_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            dtg.cash(152);

            assertEquals("CASH", utils.read_string());
            assertEquals(152, utils.read_nextInt());

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
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            String[] commands = {"DICE", "ERRO", "CASH", "LOOT", "PLAY"}, commandsR = {"","","","",""};

            for(int i = 0; i < commands.length; i++){
                utils.write_string(commands[i]);
            }

            for(int i = 0; i < commands.length; i++){
                commandsR[i] =  dtg.read_command();
            }

            assertArrayEquals(commands, commandsR);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests LOOT datagram format.
     */
    public void loot_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            dtg.loot(2);

            assertEquals("LOOT", utils.read_string());
            assertEquals(2, utils.read_nextInt());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests read next int in bytes function.
     */
    public void read_next_int_in_bytes_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            utils.write_space();
            utils.write_byte((byte) 1);

            assertEquals(1, dtg.read_next_int_in_bytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests PNTS datagram format.
     */
    public void points_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            dtg.points(89, 12);

            assertEquals("PNTS", utils.read_string());
            assertEquals(89, utils.read_nextInt());
            assertEquals(12, utils.read_next_int_in_bytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests PLAY datagram format.
     */
    public void play_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            dtg.play(1);

            assertEquals("PLAY", utils.read_string());
            utils.read_space();
            assertEquals("1", utils.read_char());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests WINS datagram format.
     */
    public void wins_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            dtg.wins(2);

            assertEquals("WINS", utils.read_string());
            utils.read_space();
            assertEquals("2", utils.read_char());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
