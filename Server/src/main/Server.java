package main;

import utils.ComUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws IOException {

        int serverPort, gameMode, gameCounter = 0;
        ServerSocket server;
        String com;
        boolean singlePlayer;
        ComUtils utils;

        if (args.length == 4 && args[0].equals("-p") && args[2].equals("-m")){

            serverPort = Integer.parseInt(args[1]);
            gameMode = Integer.parseInt(args[3]);

            if (gameMode != 1 || gameMode != 2){
                System.out.println("Invalid gamemode. Avaiable options are 1 or 2.");
                System.exit(1);
            }

            if(gameMode == 1)
                singlePlayer = true;
            else
                singlePlayer = false;

            try {

                server = new ServerSocket(serverPort);
                System.out.println("Info> Server ready.");

                while(true){

                    Socket s = server.accept();
                    s.setSoTimeout(500*1000);
                    utils = new ComUtils(s);
                    gameCounter++;

                    com = utils.read_string();
                    if(com.equals("STRT")){

                        int pID = utils.read_nextInt();
                        //new Thread(new Game().run());
                    }

                }


            }catch(Exception e){

                if(e instanceof IOException){
                    System.out.println(RED + "Error> " + RESET + "I/O error occurred.");
                }else if(e instanceof SecurityException){
                    System.out.println(RED + "Error> " + RESET + "Connection not allowed for security reasons.");
                }else if(e instanceof IllegalArgumentException){
                    System.out.println(RED + "Error> " + RESET + "Port parameter is outside the specified range of valid " +
                            "port values.");
                }
            }

        }else if (args.length == 1 && args[0].equals("-h")){
            System.out.println("Usage: java Server -p <port> [-i 1|2]");
        }else{
            System.out.println("Invalid parameters. Server parameters are: -p <port> [-i 1|2]");
            System.exit(1);
        }

    }

    private void singlePlayerBehaviour(){

    }

    private void twoPlayerBehaviour(){

    }
}