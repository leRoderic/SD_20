import java.util.Scanner;

public class Menu {

    private Scanner sc;

    public Menu(){

        this.sc = new Scanner(System.in);
    }

    public int getClientID(){

        System.out.print("Type your client ID > ");
        while (!sc.hasNextInt()){

            System.out.print("Invalid ID. Type your client ID (number) > ");
            sc.next();
        }
        //System.out.print("\n");

        return sc.nextInt();
    }

}
