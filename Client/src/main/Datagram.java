package main;

import utils.ComUtils;
import java.io.*;
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
    public void take(int id, int[] sel) throws IOException {

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
    public String read_command() throws IOException{

        return utils.read_string();
    }
    public void read_space() throws  IOException{
        utils.read_space();
    }
    public void read_int() throws  IOException{
        utils.read_int32();
    }
    public void read_char() throws  IOException{
        utils.read_char();
    }
    public byte[] read_byte(int n) throws  IOException{
        return utils.read_bytes(n);
    }
    public int[] read_dice() throws IOException {

        int[] dice = new int[5];

        //utils.read_string();
        utils.read_space();
        utils.read_int32();

        for(int i = 0; i < 5; i++){

            utils.read_space();
            dice[i] = Integer.parseInt(String.valueOf(utils.read_char()));

        }
        return dice;
    }

    public int[] read_take() throws IOException {

        int[] numbers = new int[0];
        utils.read_string();//comanda
        utils.read_space();
        utils.read_int32();//ID
        utils.read_space();
        int lenBytes = utils.read_int32();//Len



        for(int i = 0; i < lenBytes; i++){
            byte[] take = utils.read_bytes(4);
            numbers[i] = utils.bytesToInt32(take, ComUtils.Endianness.BIG_ENNDIAN);
        }

        return numbers;
    }

    public byte[] int32ToBytes(int number, ComUtils.Endianness endianness){
        return utils.int32ToBytes(number, endianness);
    }

    public int bytesToInt32(byte[] number, ComUtils.Endianness endianness){
        return utils.bytesToInt32(number, endianness);
    }


    private enum Command {
        STRT,
        BETT,
        TAKE,
        PASS,
        EXIT
    }
}
