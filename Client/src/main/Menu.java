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
        System.out.println("\nThese are the available commands:");
        System.out.println("\tSTRT");
        System.out.println("\tBETT");
        System.out.println("\tTAKE <LEN> <POS>*5");
        System.out.println("\tPASS");
        System.out.println("\tEXIT\n");
        System.out.println("And these is how the game works:");
        System.out.println("\t- You start by typing STRT.");
        System.out.println("\t- After the CASH message you can either start a new game (BETT) or exit (EXIT)");
        System.out.println("\t- After the DICE message you can either make a selection (TAKE), pass (PASS) or exit (EXIT)");
        System.out.println("\t- If you send a wrong command the game will be terminated");
        System.out.println("\t- If you don't have enough cash, you can't play");
        System.out.println("\t- In the multiplayer mode you may type STRT to start a new game. If nothing appears on the" +
                "\n\t screen is because it's the other player's turn and you have to wait.");
        System.out.println("___________________________________________________________________________");

        return sc.nextInt();
    }

}
