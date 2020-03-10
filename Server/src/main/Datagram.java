package main;

import utils.ComUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";

    public Datagram(Socket s) throws IOException {

        this.gameMode = gameMode;
        this.socket = s;
        this.utils = new ComUtils(this.socket);
    }


    public int readNextInt() throws IOException {

        return utils.read_nextInt();
    }

    public void dice(int id, int[] vals) throws IOException{

        String c = Command.DICE.name();

        char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(id);

        for(int i=0; i <6; i++){
            utils.write_space();
            utils.write_char(digits[vals[i]]);
        }
    }

    public void cash(int coins) throws IOException {

        String c = Command.CASH.name();

        utils.write_command_pint(c, coins);
    }

    public String read_command() throws IOException{

        return utils.read_string();
    }

    /**
     * Client's BETT command.
     * Format: BETT
     *
     * @throws IOException
     */
    public void loot(int coins) throws IOException {

        String c = Command.LOOT.name();

        utils.write_command_pint(c, coins);
    }

    /**
     * Client's TAKE command.
     * Format: TAKE<SP><ID><LEN>*5(<SP><POS>)
     *
     * @param id client's ID
     * @param sel client's dice selection
     */
    public void take(int id, int len, byte[] sel) throws IOException {

        String c = Command.TAKE.name();

        utils.write_take(c, id, len, sel);
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

    public int read_next_int_in_bytes() throws IOException {

        return utils.read_next_int_in_bytes();
    }

    /**
     * Client's EXIT command.
     * Format: EXIT
     *
     * @throws IOException
     */
    public void points(int id, int points) throws IOException {

        String c = Command.PNTS.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(id);
        utils.write_space();
        utils.write_int32(points);
    }

    public void play(int t) throws IOException{

        String c = Command.PLAY.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(t);
    }

    public void wins(int t) throws IOException{

        String c = Command.WINS.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(t);
    }

    private enum Command {
        CASH,
        LOOT,
        PLAY,
        DICE,
        TAKE,
        PASS,
        PNTS,
        WINS
    }
}
