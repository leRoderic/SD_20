import main.Datagram;
import org.junit.Test;
import utils.ComUtils;
import utils.MockSocket;

import java.io.IOException;

public class DatagramTest {

    @Test
    public void erro_format_test() throws IOException {

        MockSocket s = new MockSocket();
        Datagram dtg = new Datagram(s);
        ComUtils utils = new ComUtils(s);

        try {
            String msg = "This is an awesome error";
            dtg.sendErrorMessage(msg, msg.length());


            //assertEquals(2, readed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
