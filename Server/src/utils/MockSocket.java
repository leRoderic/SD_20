package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>MockSocket class</h1>
 * Socket that requires no connection to server.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   21-03-2020
 */

public class MockSocket extends Socket {

    private List<Byte> bList = new ArrayList<Byte>();

    /**
     * Inputstream getter.
     *
     * @return inputstream
     */
    public InputStream getInputStream(){
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return bList.remove(0);
            }
        };
    }

    /**
     * Outputstream getter.
     *
     * @return outputstream
     */
    public OutputStream getOutputStream(){

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bList.add((byte)b);
            }
        };
    }

}
