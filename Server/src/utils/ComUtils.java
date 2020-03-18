package utils;

import java.io.*;
import java.net.Socket;

/**
 * <h1>ComUtils class</h1>
 * Base library which provides essential functions for reading/writing operations and as well as data type or endianness
 * conversions.
 * <p>
 * <b>Note:</b> This class may be modified during the development of the project. Original source code can be found @
 * prac0_SD repo from GitHub user UB-GEI-SD.
 *
 * @author  UB-GEI-SD
 * @version 1.0
 * @since   11-02-2019
 */
public class ComUtils {

    private final int STRSIZE = 4; // All commands size is 4 bytes.
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public ComUtils(Socket s) throws IOException {

        dataInputStream = new DataInputStream(s.getInputStream());
        dataOutputStream = new DataOutputStream(s.getOutputStream());
    }

    /**
     * Reads a 32b integer.
     *
     * @return read integer
     * @throws IOException excep
     */
    public int read_int32() throws IOException {

        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
    }

    /**
     * Writes a 32b integer.
     *
     * @param number number to write
     * @throws IOException excep
     */
    public void write_int32(int number) throws IOException {

        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    public void write_byte(byte b) throws IOException {

        dataOutputStream.write(b);
    }

    /**
     * Read a char.
     *
     * @return read char
     * @throws IOException excep
     */
    public String read_char() throws IOException {

        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i] = (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    /**
     * Write command with an Integer as a parameter. STRT or PASS.
     *
     * @param c command
     * @param id client's id
     * @throws IOException
     */
    public void write_command_pint(String c, int id) throws IOException {

        write_string(c);
        write_space();
        write_int32(id);
    }

    /**
     * Write a command that requires no parameters. EXIT or BETT.
     *
     * @param c
     * @throws IOException
     */
    public void write_command(String c) throws IOException {

        write_string(c);
    }

    public void write_take(String c, int id, int len, byte[] sel) throws IOException {

        int lSel = sel.length;

        write_string(c);
        write_space();
        write_int32(id);
        write_space();
        write_int32(len);

        for(int i = 0; i < len; i++){

            write_space();
            write_byte(sel[i]);
        }
    }

    /*public int[] read_take() throws IOException {

        int[] numbers;
        read_string();//comanda
        read_space();
        read_int32();//ID
        read_space();
        int lenBytes = read_int32();//Len

        byte[] take = read_bytes(lenBytes);

        for(int i = 0; i < lenBytes; i++){
            //TODO numbers[i] = this.bytesToInt32(take[i], Endianness.BIG_ENNDIAN);
        }

        return numbers;
    }

    public int[] read_dice() throws IOException {

        int[] dice;

        read_string();
        read_space();
        read_int32();

        for(int i = 0; i < 5; i++){

            read_space();
            dice[i] = Integer.parseInt(String.valueOf(read_char()));

        }
        return dice;
    }*/


    /**
     * Write a char.
     *
     * @param c char to write
     * @throws IOException excep
     */
    public void write_char(char c) throws IOException {

        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = 1;

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        bStr[0] = (byte) c;

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0, STRSIZE);
    }

    /**
     * Write a string.
     *
     * @param str string
     * @throws IOException excep
     */
    public void write_string(String str) throws IOException {

        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0, STRSIZE);
    }

    /**
     * Reads a string.
     *
     * @return  read string
     * @throws IOException excep
     */
    public String read_string() throws IOException {

        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    /**
     * Write a blank space (' ') to the Data Output Stream.
     */
    public void write_space() throws IOException {

        byte bStr[] = new byte[1];
        bStr[0] = ' ';
        dataOutputStream.write(bStr, 0, 1);
    }

    /**
     * Reads a blank space (' ').
     */
    public void read_space() throws IOException {

        // Since all the function does is read a blank space, it's completely pointless save it or even returned it, at
        // least for now. Might need to be changed once the Server's logs are implemented.
        byte bStr[] = new byte[1];
        bStr = read_bytes(1);
    }

    public int read_nextInt() throws IOException {

        read_space();
        return read_int32();
    }

    /**
     * Convert 4 byte integer to bytes.
     *
     * @param number the integer to convert
     * @param endianness final endianness
     * @return the integer converted to bytes
     */
    public byte[] int32ToBytes(int number, Endianness endianness) {

        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /**
     * Convert from bytes to 4 byte integers.
     *
     * @param bytes the bytes to convert
     * @param endianness final endiannness
     * @return the integer converted
     */
    private int bytesToInt32(byte bytes[], Endianness endianness) {

        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    /**
     * Read a number of bytes.
     *
     * @param numBytes # of bytes to read
     * @return whatever has been read
     * @throws IOException excep
     */
    private byte[] read_bytes(int numBytes) throws IOException {

        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    public int read_next_int_in_bytes() throws IOException {

        read_space();
        byte[] asd = new byte[4];
        asd[3] = read_bytes(1)[0];
        return bytesToInt32(asd, Endianness.BIG_ENNDIAN);
    }

    /**
     * Read a variable sized String.
     *
     * @param size the size of the string
     * @return the read string
     * @throws IOException
     */
    public  String read_string_variable(int size) throws IOException {

        byte bHeader[] = new byte[size];
        char cHeader[] = new char[size];
        int numBytes = 0;

        // Read the needed bytes.
        bHeader = read_bytes(size);
        // Parsing String's size.
        for(int i = 0; i < size; i++){
            cHeader[i] = (char)bHeader[i];
        }

        numBytes = Integer.parseInt(new String(cHeader));

        // Read String.
        byte bStr[] = new byte[numBytes];
        char cStr[] = new char[numBytes];
        bStr = read_bytes(numBytes);
        for(int i = 0; i < numBytes; i++) {
            cStr[i] = (char) bStr[i];
        }
        return String.valueOf(cStr);
    }

    /**
     * Write a variable sized String.
     *
     * @param size  the string's size
     * @param str   the string
     * @throws IOException excep
     */
    public void write_string_variable(int size,String str) throws IOException {

        byte bHeader[] = new byte[size];
        String strHeader;
        int numBytes = 0;

        // Header creation.
        numBytes = str.length();

        strHeader = String.valueOf(numBytes);
        int len;
        if ((len = strHeader.length()) < size) {
            for (int i = len; i < size; i++) {
                strHeader = "0" + strHeader;
            }
        }
        for(int i = 0; i < size; i++) {
            bHeader[i] = (byte) strHeader.charAt(i);
        }
        // Sending header.
        dataOutputStream.write(bHeader, 0, size);
        // Sends String.
        dataOutputStream.writeBytes(str);
    }

    /**
     * Endianness types
     */
    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }
}


