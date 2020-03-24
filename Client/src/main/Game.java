package main;

import utils.ComUtils;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


/**
 * <h1>Game class</h1>
 */
public class Game {

    private Menu menu;
    private Datagram datagram;
    private boolean partida = false;
    private int mode; //0->client 1->aleatori
    private int idCliente;
    private Random random = new Random();
    private boolean takeShip = false, takeCaptain = false, takeCrew = false;
    private Scanner sc =new Scanner(System.in);
    private String turno = "";
    private String win = "";
    private int cash = 0;
    private int tirada;

    /**
     * Constructor of Game
     *
     * @param datagram  Instance of Datagram
     * @param menu      Instance of Menu
     * @param mode      Geme mode
     * @throws IOException excep
     */
    public Game(Datagram datagram, Menu menu, int mode) throws IOException {
        this.datagram = datagram;
        this.menu = menu;
        partida = true;
        this.mode = mode;
        partida(mode);

    }


    public boolean isPartida() {
        return partida;
    }

    public void setPartida(boolean partida) {
        this.partida = partida;
    }

    /**
     * Starts the game.
     *
     * @param mode      Game mode
     * @throws IOException excep
     */
    private void partida(int mode) throws IOException {
        String error = "";
        String comanda = null;
        boolean t = true;

        if(mode == 0){
            idCliente = menu.getClientID();
            System.out.print("Client "+idCliente+": ");
            if(sc.next().equals("STRT")){
                try {
                    datagram.strt(idCliente);
                } catch (IOException e) {
                    System.out.println("ERROR: Couldn't not send command STRT");
                }
            }
        }
        if(mode == 1) {
            System.out.println("===========================================================================");
            System.out.println("               Welcome to \"Ship, Captain and Crew\" game!                 ");
            System.out.println("===========================================================================");
            idCliente = random.nextInt(9999)+1;
            try {
                datagram.strt(idCliente);
                System.out.println("Client " + idCliente + ": STRT");
            } catch (IOException e) {
                System.out.println("ERROR: Couldn't not send command STRT");
            }
        }

        while (isPartida()) {
            //modo manual
            if (mode == 0) {
                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    System.out.println("ERROR: Couldn't not read command");
                    System.exit(1);
                }

                if (comanda.equals("ERRO")) {
                    try {
                        System.out.print(comanda + " ");
                        error = datagram.readErrorMessage();
                        System.out.println(error);
                        System.exit(0);
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command ERRO");
                        System.exit(1);
                    }
                } else if (comanda.equals("CASH")) {
                    try {
                        datagram.read_space();
                         cash = datagram.read_int();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command CASH");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + cash);
                    System.out.print("Client "+idCliente+": ");
                    String c = sc.next();
                    if (c.equals("BETT")) {
                        this.tirada = 3;
                        try {
                            datagram.bett();
                        } catch (IOException e) {
                            System.out.println("ERROR: Couldn't not send command BETT");
                            System.exit(1);
                        }
                    } else if (c.equals("EXIT")) {
                        try {
                            datagram.exit();
                            this.setPartida(false);
                        } catch (IOException e) {
                            System.out.println("ERROR: Couldn't not send command EXIT");
                            System.exit(1);
                        }

                    } else {
                        System.out.println("ERROR: You have entered a wrong command");
                        System.exit(1);
                    }
                } else if (comanda.equals("LOOT")) {
                    int loot = 0;
                    try {
                        datagram.read_space();
                        loot = datagram.read_int();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command LOOT");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + loot);
                } else if(comanda.equals("PLAY")){
                    try {
                        datagram.read_space();
                        turno = datagram.read_char();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command PLAY");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + turno);

                } else if (comanda.equals("DICE")) {
                    this.tirada = this.tirada-1;
                    System.out.print(comanda + " ");
                    int[] dices = new int[5];
                    try {
                        dices = datagram.read_dice();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command DICE");
                        System.exit(1);
                    }
                    for(int i = 0; i<dices.length; i++){
                        System.out.print(dices[i] + " ");
                    }
                    System.out.println("");
                    if(this.tirada >= 0) {
                        this.tirada = this.tirada-1;
                        System.out.print("Client " + idCliente + ": ");
                        String c = sc.next();
                        if (c.equals("TAKE")) {
                            int[] numbers;
                            numbers = this.read_take();
                            try {
                                datagram.take(idCliente, numbers);
                            } catch (IOException e) {
                                System.out.println("ERROR: Couldn't not send command TAKE");
                                System.exit(1);
                            }
                        } else if (c.equals("PASS")) {
                            try {
                                datagram.pass(idCliente);
                            } catch (IOException e) {
                                System.out.println("ERROR: Couldn't not send command PASS");
                                System.exit(1);
                            }
                        } else if (c.equals("EXIT")) {
                            try {
                                datagram.exit();
                                this.setPartida(false);
                            } catch (IOException e) {
                                System.out.println("ERROR: Couldn't not send command EXIT");
                                System.exit(1);
                            }
                        } else {
                            System.out.println("ERROR: You have entered a wrong command");
                            System.exit(1);
                        }
                    }
                } else if(comanda.equals("PNTS")){
                    try {
                        datagram.read_space();
                        datagram.read_int();
                        datagram.read_space();
                        byte[] b = new byte[4];
                        b[3] = datagram.read_byte(1)[0];
                        int bytePoint = datagram.bytesToInt32(b, ComUtils.Endianness.BIG_ENNDIAN);
                        System.out.println(comanda + " " + bytePoint);
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command PNTS");
                        System.exit(1);
                    }

                } else if(comanda.equals("WINS")){

                    try {
                        datagram.read_space();
                       win =  datagram.read_char();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command WINS");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + win);
                }
            }
            //mode aleatori
            if (mode == 1) {
                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    System.out.println("ERROR: Couldn't not read command");
                    System.exit(1);
                }
                if (comanda.equals("ERRO")) {
                    System.out.print(comanda + " ");
                    try {
                        error = datagram.readErrorMessage();
                        System.out.println(error);
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command ERRO");
                        System.exit(1);
                    }
                }
                else if (comanda.equals("CASH")) {
                    try {
                        datagram.read_space();
                        cash = datagram.read_int();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command CASH");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + cash);
                    if(t) {
                        this.tirada = 2;
                        t = false;
                        try {
                            datagram.bett();
                            System.out.println("Client " + idCliente +": BETT");
                        } catch (IOException e) {
                            System.out.println("ERROR: Couldn't not send command BETT");
                            System.exit(1);
                        }

                    } else {
                        try {
                            /*try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            datagram.exit();
                            System.out.println("Client " + idCliente +": EXIT");
                            setPartida(false);
                        } catch (IOException e) {
                            System.out.println("ERROR: Couldn't not send command EXIT");
                            System.exit(1);
                        }
                    }
                }
                else if (comanda.equals("LOOT")) {
                    int loot = 0;
                    try {
                        datagram.read_space();
                        loot = datagram.read_int();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command LOOT");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + loot);
                }
                else if (comanda.equals("PLAY")) {
                    try {
                        datagram.read_space();
                        turno =datagram.read_char();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command PLAY");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + turno);
                }

                else if (comanda.equals("DICE")) {
                    System.out.print(comanda + " ");
                    int[] dice = new int[5];
                    try {
                        dice = datagram.read_dice();
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command DICE");
                        System.exit(1);
                    }
                    for(int i = 0; i<dice.length; i++){
                        System.out.print(dice[i] + " ");
                    }
                    if(this.tirada > 0) {
                        this.tirada = this.tirada - 1;
                        if (random.nextInt(5) != 0) {
                            int posCaptain = -1, posShip = -1, posCrew = -1;
                            boolean ship = false;
                            boolean captain = false;
                            boolean crew = false;
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 6) {
                                    ship = true;
                                    posShip = i;
                                    break;
                                }
                            }
                            if (ship) {
                                for (int i = 0; i < dice.length; i++) {
                                    if (dice[i] == 5) {
                                        captain = true;
                                        posCaptain = i;
                                        break;
                                    }
                                }
                            }
                            if (captain) {
                                for (int i = 0; i < dice.length; i++) {
                                    if (dice[i] == 4) {
                                        crew = true;
                                        posCrew = i;
                                        break;
                                    }
                                }
                            }
                            int[] sel;
                            if (!takeShip && ship) {
                                takeShip = true;
                                if (!takeCaptain && captain) {
                                    takeCaptain = true;
                                    if (!takeCrew && crew) {
                                        takeCrew = true;
                                    }
                                }
                                if (takeShip && takeCaptain && takeCrew) {
                                    sel = new int[3];
                                    int sh = posShip + 1;
                                    int ca = posCaptain + 1;
                                    int cr = posCrew + 1;
                                    sel[0] = sh;
                                    sel[1] = ca;
                                    sel[2] = cr;
                                    try {
                                        datagram.take(idCliente, sel);
                                        System.out.println("Client " + idCliente +": TAKE");
                                    } catch (IOException e) {
                                        System.out.println("ERROR: Couldn't not send command TAKE");
                                        System.exit(1);
                                    }
                                } else if (takeShip && takeCaptain) {
                                    sel = new int[2];
                                    int sh = posShip + 1;
                                    int ca = posCaptain + 1;
                                    sel[0] = sh;
                                    sel[1] = ca;
                                    try {
                                        datagram.take(idCliente, sel);
                                        System.out.println("Client " + idCliente +": TAKE");
                                    } catch (IOException e) {
                                        System.out.println("ERROR: Couldn't not send command TAKE");
                                        System.exit(1);
                                    }
                                } else if (takeShip) {
                                    sel = new int[1];
                                    int sh = posShip + 1;
                                    sel[0] = sh;
                                    try {
                                        datagram.take(idCliente, sel);
                                        System.out.println("Client " + idCliente +": TAKE");
                                    } catch (IOException e) {
                                        System.out.println("ERROR: Couldn't not send command TAKE");
                                        System.exit(1);
                                    }
                                }
                            } else if (takeShip && captain && !takeCaptain) {
                                takeCaptain = true;
                                if (!takeCrew && crew) {
                                    takeCrew = true;
                                }
                                if (takeCaptain && takeCrew) {
                                    sel = new int[2];
                                    int ca = posCaptain + 1;
                                    int cr = posCrew + 1;
                                    sel[0] = ca;
                                    sel[1] = cr;
                                    try {
                                        datagram.take(idCliente, sel);
                                        System.out.println("Client " + idCliente +": TAKE");
                                    } catch (IOException e) {
                                        System.out.println("ERROR: Couldn't not send command TAKE");
                                        System.exit(1);
                                    }
                                } else if (takeCaptain) {
                                    sel = new int[1];
                                    int ca = posCaptain + 1;
                                    sel[0] = ca;
                                    try {
                                        datagram.take(idCliente, sel);
                                        System.out.println("Client " + idCliente +": TAKE");
                                    } catch (IOException e) {
                                        System.out.println("ERROR: Couldn't not send command TAKE");
                                        System.exit(1);
                                    }
                                }
                            } else if (takeShip && takeCaptain && crew && !takeCrew) {
                                takeCrew = true;
                                sel = new int[1];
                                int cr = posCrew + 1;
                                sel[0] = cr;
                                try {
                                    datagram.take(idCliente, sel);
                                    System.out.println("Client " + idCliente +": TAKE");
                                } catch (IOException e) {
                                    System.out.println("ERROR: Couldn't not send command TAKE");
                                    System.exit(1);
                                }
                            } else {
                                //TAKE 0x00
                                sel = new int[0];
                                try {
                                    datagram.take(idCliente, sel);
                                    System.out.println("Client " + idCliente +": TAKE");
                                } catch (IOException e) {
                                    System.out.println("ERROR: Couldn't not send command TAKE");
                                    System.exit(1);
                                }
                            }
                        } else {
                            try {
                                datagram.pass(idCliente);
                                System.out.println("Client " + idCliente +": PASS");
                            } catch (IOException e) {
                                System.out.println("ERROR: Couldn't not send command PASS");
                                System.exit(1);
                            }
                        }
                    }

                }
                else if (comanda.equals("PNTS")) {
                    int bytePoint = -1;
                    try {
                        datagram.read_space();
                        datagram.read_int();
                        datagram.read_space();
                        byte[] b = new byte[4];
                        b[3] = datagram.read_byte(1)[0];
                        bytePoint = datagram.bytesToInt32(b, ComUtils.Endianness.BIG_ENNDIAN);
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command PNTS");
                        System.exit(1);
                    }
                    System.out.println(comanda + " " + bytePoint);
                }
                else if (comanda.equals("WINS")) {
                    try {
                        datagram.read_space();
                        win = datagram.read_char();
                        System.out.println(comanda + " " + win);
                    } catch (IOException e) {
                        System.out.println("ERROR: Couldn't not read command WINS");
                        System.exit(1);
                    }
                }
            }
        }
    }


    public int[] read_take(){
        int len = 0;
        len = sc.nextInt();
        int[] numbers = new int[len];
        for(int i = 0; i<len; i++){
            int id = sc.nextInt();
            numbers[i] = id;
        }
        return numbers;
    }

}
