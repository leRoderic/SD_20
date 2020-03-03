import java.util.Scanner;

public class Menu {

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\033[0;32m";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private Scanner sc =new Scanner(System.in);

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

    public String show_cash(int cash){

        if(cash >= 0)
            System.out.println("Your current balance is: " + GREEN + cash + RESET);
        else
            System.out.println("Your current balance is: " + RED + cash + RESET);

        System.out.println("You may type:\n\t" + CYAN + "BETT" + RESET + " - to start a new game\n\t" + CYAN + "EXIT"
                        + RESET + " - to exit the game");
        System.out.print("> ");

        String com;
        while (true){

            com = sc.next();
            if(com.equals("BETT") || com.equals("EXIT"))
                break;

            System.out.println(RED + "Invalid command." + RESET + " BETT or EXIT.");
            System.out.print("> ");
        }
        System.out.println("_______________________________________________________________");

        return com;
    }

    public void show_dices(){

        System.out.println("Current roll values are: " + CYAN + RESET);
        System.out.println("Taken values are: " + CYAN + RESET);
        System.out.println("You may use:\n\t" + CYAN + "TAKE" + RESET + " - to select any dice\n\t" + CYAN + "PASS"
                + RESET + " - to roll again");
        System.out.print("> ");

        String com;
        while (true){

            com = sc.next();
            if(com.equals("TAKE") || com.equals("PASS"))
                break;

            System.out.println(RED + "Invalid command." + RESET + " TAKE or PASS.");
            System.out.print("> ");
        }
        System.out.println("_______________________________________________________________");
    }

}
