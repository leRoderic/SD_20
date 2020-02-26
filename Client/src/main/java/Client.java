
public class Client {

    // Has to have -h parameter.
    // Client parameters -s [SERVER] -p [PORT] -i [0 o 1]
    public static void main(String[] args) {

        String serverAdress;
        int serverPort, gameMode;

        if (args.length == 6 && args[0].equals("-s") && args[2].equals("-p") && args[4].equals("-i")){

            serverAdress = args[1];
            serverPort = Integer.parseInt(args[3]);
            gameMode = Integer.parseInt(args[5]);

            if (gameMode < 0 || gameMode > 1){
                System.out.println("Invalid gamemode. Avaiable options are 0 or 1.");
                System.exit(1);
            }

        }else if (args.length == 1 && args[0].equals("-h")){
            System.out.println("Usage: java Client -s <server> -p <port> [-i 0|1]");
        }else{
            System.out.println("Invalid parameters. Client parameters are: -s <server> -p <port> [-i 0|1]");
            System.exit(1);
        }

    }
}