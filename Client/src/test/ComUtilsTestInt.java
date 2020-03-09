import org.junit.Test;
import utils.ComUtils;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

/**
 * <h1>ComUtils test class</h1>
 * Tests write_int32 and read_int32 functions from ComUtils library.
 *
 * @author  UB-GEI-SD
 * @version 1.0
 * @since   11-02-2019
 */
public class ComUtilsTestInt {

    @Test
    public void write_read_int() throws IOException {

        try {

            Socket s = new Socket();
            ComUtils utils = new ComUtils(s);
            utils.write_int32(2);
           int readedInt = utils.read_int32();

            assertEquals(2, readedInt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
