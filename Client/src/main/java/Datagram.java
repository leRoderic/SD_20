import utils.ComUtils;
import java.io.*;

/**
 * <h1>Datagram class</h1>
 * Communication functions between Client and Server.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   24-02-2020
 */
public class Datagram {

    private ComUtils utils;

    /**
     * Client's start command. Writes/sends ID to server.
     * Format:  STRT<SP><ID>
     *
     * @param id client's ID
     * @throws IOException excep
     */
    public void strt(int id) throws IOException {

        String c = Command.STRT.name();
        int[] param = new int[1];
        param[0] = id;

        utils.write_datagram(c, param);
    }

    /**
     * Client's BETT command.
     * Format: BETT
     *
     * @throws IOException
     */
    public void bett() throws IOException {

        String c = Command.BETT.name();
        int[] param = null;

        utils.write_datagram(c, param);
    }

    /**
     * Client's TAKE command.
     * Format: TAKE<SP><ID><LEN>*5(<SP><POS>)
     *
     * @param id client's ID
     * @param sel client's dice selection
     */
    public void take(int id, int[] sel) throws IOException {

        String c = Command.TAKE.name();
        int[] param = new int[sel.length + 1];

        param[0] = id;

        for(int i = 1; i < param.length; i++){

            param[i] = sel[i - 1];
        }

        utils.write_datagram(c, param);
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
        int[] param = new int[1];
        param[0] = id;

        utils.write_datagram(c, param);

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

        utils.write_datagram(c, param);
    }

    private enum Command {
        STRT,
        BETT,
        TAKE,
        PASS,
        EXIT
    }
}
