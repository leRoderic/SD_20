import utils.ComUtils;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;

/**
 * <h1>Datagram class</h1>
 * Communication functions between Client and Server.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   24-02-2020
 */
public class Datagram {

    private Socket socket;
    private ComUtils utils;
    private int gameMode;

    public Datagram(String serverAddress, int port, int gameMode) throws IOException {

        try{

            this.gameMode = gameMode;
            this.socket = new Socket(serverAddress, port);
            this.socket.setSoTimeout(500*1000);

        }catch (IOException e){

        }finally {
            this.utils = new ComUtils(this.socket);
        }
    }

    /**
     * Client's start command. Writes/sends ID to server.
     * Format:  STRT<SP><ID>
     *
     * @param id client's ID
     * @throws IOException excep
     */
    public void strt(int id) throws IOException {

        String c = Command.STRT.name();

        utils.write_command_pint(c, id);
    }

    /**
     * Client's BETT command.
     * Format: BETT
     *
     * @throws IOException
     */
    public void bett() throws IOException {

        String c = Command.BETT.name();

        utils.write_command(c);
    }

    /**
     * Client's TAKE command.
     * Format: TAKE<SP><ID><LEN>*5(<SP><POS>)
     *
     * @param id client's ID
     * @param sel client's dice selection
     */
    public void take(int id, byte[] sel) throws IOException {

        String c = Command.TAKE.name();

        utils.write_take(c, id, sel);
    }

    /**
     * Client's PASS command.
     * Format: PASS<SP><ID>
     *
     * @param id client's id
     * @throws IOException excep
     */
    public void pass(int id) throws IOException {

        String c = Command.PASS.name();

        utils.write_command_pint(c, id);
    }

    /**
     * Client's EXIT command.
     * Format: EXIT
     *
     * @throws IOException
     */
    public void exit() throws IOException {

        String c = Command.EXIT.name();
        int[] param = null;

        utils.write_command(c);
    }

    private enum Command {
        STRT,
        BETT,
        TAKE,
        PASS,
        EXIT
    }
}
