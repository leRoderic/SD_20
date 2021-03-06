import org.junit.Test;
import utils.ComUtils;
import utils.MockSocket;
import java.io.IOException;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * <h1>ComUtils test class</h1>
 * Tests class functions.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   21-03-2020
 */

public class ComUtilsTest {

    @Test
    /**
     * Tests write_int32 function.
     */
    public void write_read_int_test() throws IOException {

        MockSocket s = new MockSocket();

        try {

            ComUtils comUtils = new ComUtils(s);
            comUtils.write_int32(2);
            int readed = comUtils.read_int32();

            assertEquals(2, readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests write and read char functions.
     */
    public void write_read_char_test() throws IOException {

        MockSocket s = new MockSocket();

        try {

            ComUtils comUtils = new ComUtils(s);
            comUtils.write_char((char)(23));
            String readed = comUtils.read_char();
            int a = Integer.parseInt(readed);
            assertEquals("23", readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests write and read string functions.
     */
    public void write_read_string_test() throws IOException {

        MockSocket s = new MockSocket();

        try {

            ComUtils comUtils = new ComUtils(s);
            comUtils.write_string("EXIT");
            String readed = comUtils.read_string();

            assertEquals("EXIT", readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests read and write string variable functions.
     */
    public void write_read_variable_string_test() throws IOException {

        MockSocket s = new MockSocket();
        String tst = "Talk is cheap, show me the code - Linus Torvalds";
        try {

            ComUtils comUtils = new ComUtils(s);
            comUtils.write_string_variable(tst.length(), tst);
            String readed = comUtils.read_string_variable(tst.length());

            assertEquals(tst, readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests int32 to bytes function with big endian.
     */
    public void int32_to_bytes_big_endian_test() throws IOException {

        MockSocket s = new MockSocket();

        try {
            ComUtils comUtils = new ComUtils(s);
            byte[] readed = comUtils.int32ToBytes(18, ComUtils.Endianness.BIG_ENNDIAN);
            byte[] exp = {0, 0, 0, 18};

            assertArrayEquals(readed, exp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests int32 to bytes function with little endian.
     */
    public void int32_to_bytes_litle_endian_test() throws IOException {

        MockSocket s = new MockSocket();

        try {
            ComUtils comUtils = new ComUtils(s);
            byte[] readed = comUtils.int32ToBytes(18, ComUtils.Endianness.LITTLE_ENDIAN);
            byte[] exp = {18, 0, 0, 0};

            assertArrayEquals(readed, exp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests bytes to int32 function with big endian.
     */
    public void bytes_to_int32_big_endian_test() throws IOException {

        MockSocket s = new MockSocket();

        try {
            ComUtils comUtils = new ComUtils(s);
            byte[] in = {0, 0, 0, 22};
            int readed = comUtils.bytesToInt32(in, ComUtils.Endianness.BIG_ENNDIAN);

            assertEquals(readed, 22);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests bytes to int32 function with little endian.
     */
    public void bytes_to_int32_litle_endian_test() throws IOException {

        MockSocket s = new MockSocket();

        try {
            ComUtils comUtils = new ComUtils(s);
            byte[] in = {22, 0, 0, 0};
            int readed = comUtils.bytesToInt32(in, ComUtils.Endianness.LITTLE_ENDIAN);

            assertEquals(readed, 22);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Tests write and read byte functions.
     */
    public void write_read_byte_test() throws IOException {

        MockSocket s = new MockSocket();

        try {
            ComUtils comUtils = new ComUtils(s);
            comUtils.write_byte((byte) 5);
            byte[] readed = comUtils.read_bytes(1);

            assertEquals(readed[0], 5);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
