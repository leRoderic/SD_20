package main;

import utils.ComUtils;
import java.io.IOException;
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
    private int winValue;
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";

    /**
     * Constructor for Datagram class.
     *
     * @param s the socket
     * @throws IOException
     */
    public Datagram(Socket s) throws IOException {

        this.socket = s;
        this.utils = new ComUtils(this.socket);
    }

    /**
     * Check if socket is still connected and active
     *
     * @return true: connected   false: !connected
     */
    public boolean isConnected(){

        return this.socket.isConnected();
    }

    /**
     * Getter of the client's win value.
     * The win value identifies the client when playing against another player. This value, with range 0-1, is set by
     * the server and is used when the server sends the WINS command to each client.
     *
     * @return the win value
     */
    public int getWinValue() {
        return winValue;
    }

    /**
     * Setter for the win value.
     *
     * @param winValue
     */
    public void setWinValue(int winValue) {
        this.winValue = winValue;
    }

    /**
     * Reads the next int of the input stream (space+int).
     *
     * @return read int
     * @throws IOException h
     */
    public int readNextInt() throws IOException {

        return utils.read_nextInt();
    }

    /**
     * Sends error message to the client.
     *
     * @param text  error message
     * @param len   length of the message
     * @throws IOException h
     */
    public void sendErrorMessage(String text, int len) throws IOException {

        // Format: ERRO <LEN> <ERROR_TEXT>

        String c = Command.ERRO.name();

        utils.write_string(c);
        utils.write_space();
        utils.write_char((char) (len + '0'));
        utils.write_space();
        utils.write_string_variable(len, text);
    }

    /**
     * Sends dice command to the client.
     *
     * @param id    client's id
     * @param vals  dice values
     * @throws IOException h
     */
    public void dice(int id, int[] vals) throws IOException{

        String c = Command.DICE.name();

        // Required conversion to char since that's how the protocol is designed.
        char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(id);

        for(int i=0; i <5; i++){
            utils.write_space();
            utils.write_char(digits[vals[i]]);
        }
    }

    /**
     * Sends cash command to the client.
     *
     * @param coins client's 'money'
     * @throws IOException h
     */
    public void cash(int coins) throws IOException {

        String c = Command.CASH.name();

        utils.write_command_pint(c, coins);
    }

    /**
     * Reads a command.
     *
     * @return  read command
     * @throws IOException h
     */
    public String read_command() throws IOException{

        return utils.read_string();
    }

    /**
     * Sends loot command.
     *
     * @throws IOException
     */
    public void loot(int coins) throws IOException {

        String c = Command.LOOT.name();

        utils.write_command_pint(c, coins);
    }

    /**
     * Reads a byte and returns the corresponding integer.
     *
     * @return  read int
     * @throws IOException h
     */
    public int read_next_int_in_bytes() throws IOException {

        return utils.read_next_int_in_bytes();
    }

    /**
     * Sends points command.
     *
     * @param id    client's id
     * @param points    points
     * @throws IOException h
     */
    public void points(int id, int points) throws IOException {

        String c = Command.PNTS.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_int32(id);
        utils.write_space();
        utils.write_byte(utils.int32ToBytes(points, ComUtils.Endianness.BIG_ENNDIAN)[3]); //THIS MAY CHANGE TODO
    }

    /**
     * Sends play command.
     *
     * @param t turn
     * @throws IOException h
     */
    public void play(int t) throws IOException{

        String c = Command.PLAY.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_char((char)(t + '0'));
    }

    /**
     * Sends wins command.
     *
     * @param t who won int
     * @throws IOException h
     */
    public void wins(int t) throws IOException{

        String c = Command.WINS.name();

        utils.write_command(c);
        utils.write_space();
        utils.write_char((char) (t + '0'));
    }

    /**
     * An enum containing all commands to violate the Open/Closed principle. Also for old times' sake.
     */
    private enum Command {
        CASH,
        LOOT,
        PLAY,
        DICE,
        PNTS,
        WINS,
        ERRO
    }
}
