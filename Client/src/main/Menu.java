package main;

import java.util.Scanner;

/**
 * <h1>Menu class</h1>
 */
public class Menu {

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private Scanner sc =new Scanner(System.in);

    /**
     * Methodnto get client ID
     *
     * @return      Client ID
     */
    public int getClientID(){

        System.out.println("===========================================================================");
        System.out.println("Welcome to \"Ship, Captain and Crew\" game! Please type in your client ID.");
        System.out.print("> ");
        while (!sc.hasNextInt()){

            System.out.println(RED + "Invalid ID. " + RESET + "Type your client, fyi ID "+ YELLOW + "NEEDS " + RESET +
                    "to be a number.");
            System.out.print("> ");
            sc.next();
        }
        System.out.println("___________________________________________________________________________");

        return sc.nextInt();
    }

}
