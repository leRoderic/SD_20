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

    public Datagram(String serverAddress, int port, int gameMode) throws IOException {

        try{

            this.gameMode = gameMode;
            this.socket = new Socket(serverAddress, port);
            this.socket.setSoTimeout(500*1000);

        }catch (Exception e){

            if(e instanceof UnknownHostException)
                System.out.println(RED + "Error> " + RESET + "The IP address of the host could not be determined.");
            else if(e instanceof SecurityException)
                System.out.println(RED + "Error> " + RESET + "Connection not allowed for security reasons.");
            else if(e instanceof IOException)
                System.out.println(RED + "Error> " + RESET + "Socket could not be created.");
            if(e instanceof IllegalArgumentException)
                System.out.println(RED + "Error> " + RESET + "Port parameter is outside the specified range of valid " +
                        "port values.");
            System.exit(1);
        }
        this.utils = new ComUtils(this.socket);

    }

    /**
     * Client's start command. Writes/sends ID to server.
     * Format:  STRT<SP><ID>
     *
     * @param id client's ID
     * @throws IOException excep
     */
    public void cash(int coins) throws IOException {

        String c = Command.CASH.name();

        utils.write_command_pint(c, coins);
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
