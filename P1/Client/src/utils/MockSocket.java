package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MockSocket extends Socket {

    private List<Byte> bList = new ArrayList<Byte>();

    public InputStream getInputStream(){
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return bList.remove(0);
            }
        };
    }

    public OutputStream getOutputStream(){

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bList.add((byte)b);
            }
        };
    }

}
