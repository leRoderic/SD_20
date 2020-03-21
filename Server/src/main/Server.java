package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * <h1>Server class</h1>
 * Server's main class.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   24-02-2020
 */

public class Server {

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";
    static HashMap<Integer, Integer> players = new HashMap<Integer, Integer>(); // Stores player's cash.

    /**
     * Single player server mode.
     *
     * @param sv    server socket
     * @throws SocketException s
     */
    private static void singlePlayerBehaviour(ServerSocket sv) throws SocketException {


        while(true){
            System.out.println("Info> Waiting for player [0/1]");
            Socket s = null;
            try {
                s = sv.accept();
            } catch (IOException e) {
                System.out.println(RED + "Error> " + RESET + "I/O error occurred: " + e.getMessage());
            }
            s.setSoTimeout(500*1000);
            System.out.println("Info> Player connected, creating new game");
            try {
                Thread t = (new Thread(new GameThread(s, null, true, players)));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Two player server game mode.
     *
     * @param sv    server socket
     * @throws SocketException e
     */
    private static void twoPlayerBehaviour(ServerSocket sv) throws SocketException {

        System.out.println("Info> Server set as player vs player.");
        while(true){

            System.out.println("Info> Waiting for players [0/2]");
            Socket s1 = null, s2 = null;
            try {
                s1 = sv.accept();
            } catch (IOException e) {
                System.out.println(RED + "Error> " + RESET + "I/O error occurred: " + e.getMessage());
            }
            s1.setSoTimeout(500*1000);
            System.out.println("Info> Waiting for players [1/2]");
            try {
                s2 = sv.accept();
            } catch (IOException e) {
                System.out.println(RED + "Error> " + RESET + "I/O error occurred: " + e.getMessage());
            }
            s2.setSoTimeout(500*1000);
            System.out.println("Info> Players connected, creating new game.");
            try {
                Thread t = (new Thread(new GameThread(s1, s2, false, players)));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The 'main' program.
     * @param args  arguments passed to the program
     * @throws IOException e
     */
    public static void main(String[] args) throws IOException {

        int serverPort, gameMode;
        ServerSocket server;
        boolean singlePlayer;

        // Check if parameters are correct.
        if (args.length == 4 && args[0].equals("-p") && args[2].equals("-i")){

            serverPort = Integer.parseInt(args[1]);
            gameMode = Integer.parseInt(args[3]);

            // Check game mode is valid.
            if (gameMode > 1 && gameMode > 2){
                System.out.println("Invalid gamemode. Avaiable options are 1 or 2.");
                System.exit(1);
            }

            if(gameMode == 1)
                singlePlayer = true;
            else
                singlePlayer = false;

            try {
                InetAddress add = InetAddress.getByName(InetAddress.getLocalHost().toString().split("/")[1]);
                //InetAddress add = InetAddress.getByName("25.98.169.216");
                server = new ServerSocket(serverPort, 50, add);
                System.out.println("Info> Server ready with IP " + server.getInetAddress().toString().split("/")[1] +
                        " on port number " + serverPort);

                if(singlePlayer)
                    singlePlayerBehaviour(server);
                else
                    twoPlayerBehaviour(server);

            }catch(Exception e){

                if(e instanceof IOException){
                    System.out.println(RED + "Error> " + RESET + "I/O error occurred: " + e.getMessage());
                }else if(e instanceof SecurityException){
                    System.out.println(RED + "Error> " + RESET + "Connection not allowed for security reasons.");
                }else if(e instanceof IllegalArgumentException){
                    System.out.println(RED + "Error> " + RESET + "Port parameter is outside the specified range of valid " +
                            "port values.");
                }
            }

        }else if (args.length == 1 && args[0].equals("-h")){
            System.out.println("Usage: java Server -p <port> -i 1|2]");
        }else{
            System.out.println("Invalid parameters. Server parameters are: -p <port> -i 1|2");
            System.exit(1);
        }

    }
}