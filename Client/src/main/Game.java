package main;

import utils.ComUtils;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


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

    public Game(Datagram datagram, Menu menu, int mode){
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

    private void partida(int mode) {
        String comanda = null;

        if(mode == 0){
            idCliente = menu.getClientID();
            if(sc.next().equals("STRT")){
                try {
                    datagram.strt(idCliente);
                    System.out.println("Start enviat");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(mode == 1) {
            try {
                datagram.strt(9999);//id por defecto: 9999
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (isPartida()) {
            //modo manual
            if (mode == 0) {
                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (comanda.equals("CASH")) {
                    System.out.println(comanda);
                    try {
                        datagram.read_space();
                        datagram.read_int();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sc.next().equals("BET")) {
                        try {
                            datagram.bett();
                            System.out.println("Bet enviat");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.exit(1);
                    }
                } else if (comanda.equals("LOOT")) {
                    System.out.println(comanda);
                    try {
                        datagram.read_space();
                        datagram.read_int();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if(comanda.equals("PLAY")){
                    System.out.println(comanda);
                    try {
                        datagram.read_space();
                        datagram.read_char();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (comanda.equals("DICE")) {
                    int[] dices = new int[5];
                    try {
                        dices = datagram.read_dice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(int i = 0; i<dices.length; i++){
                        System.out.println(dices[i]);
                    }
                    if (sc.next().equals("PASS")) {
                        try {
                            datagram.pass(idCliente);
                            System.out.println("Pass enviat");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (sc.next().equals("TAKE")) {
                        int[] numbers;
                        numbers = this.read_take();
                        try {
                            datagram.take(idCliente,numbers);
                            System.out.println("Take enviat");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.exit(1);
                    }
                } else if(comanda.equals("PNTS")){
                    try {
                        datagram.read_space();
                        datagram.read_int();
                        datagram.read_space();
                        datagram.read_byte(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else if(comanda.equals("WINS")){
                    try {
                        datagram.read_space();
                        datagram.read_char();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sc.next().equals("EXIT")) {
                        try {
                            datagram.exit();
                            System.out.println("Exit enviat");
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            //mode aleatori
            if (mode == 1) {
                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (comanda.equals("CASH")) {
                    try {
                        datagram.read_space();
                        datagram.read_int();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(comanda);
                    try {
                        datagram.bett();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (comanda.equals("LOOT")) {
                    try {
                        datagram.read_space();
                        datagram.read_int();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(comanda);
                }
                else if (comanda.equals("PLAY")) {
                    try {
                        datagram.read_space();
                        turno = datagram.read_char();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(comanda);
                }
                //if(turno.equals("0")){
                else if (comanda.equals("DICE")) {
                    System.out.println(comanda);
                    int[] dice = new int[5];
                    try {
                        dice = datagram.read_dice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(random.nextInt(5)!=0) {
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
                        if(!takeShip && ship){
                            takeShip = true;
                            if(!takeCaptain && captain){
                                takeCaptain = true;
                                if(!takeCrew && crew){
                                    takeCrew = true;
                                }
                            }
                            if(takeShip && takeCaptain && takeCrew) {
                                sel = new int[3];
                                int sh = posShip + 1;
                                int ca = posCaptain + 1;
                                int cr = posCrew + 1;
                                sel[0] = sh;
                                sel[1] = ca;
                                sel[2] = cr;
                                try {
                                    datagram.take(9999, sel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(takeShip && takeCaptain){
                                sel = new int[2];
                                int sh = posShip + 1;
                                int ca = posCaptain + 1;
                                sel[0] = sh;
                                sel[1] = ca;
                                try {
                                    datagram.take(9999, sel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(takeShip){
                                sel = new int[1];
                                int sh = posShip + 1;
                                sel[0] = sh;
                                try {
                                    datagram.take(9999, sel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if(takeShip && captain && !takeCaptain){
                            takeCaptain = true;
                            if(!takeCrew && crew){
                                takeCrew = true;
                            }
                            if(takeCaptain && takeCrew) {
                                sel = new int[2];
                                int ca = posCaptain + 1;
                                int cr = posCrew + 1;
                                sel[0] = ca;
                                sel[1] = cr;
                                try {
                                    datagram.take(9999, sel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else if(takeCaptain){
                                sel = new int[1];
                                int ca = posCaptain + 1;
                                sel[0] = ca;
                                try {
                                    datagram.take(9999, sel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if(takeShip && takeCaptain && crew && !takeCrew){
                            takeCrew = true;
                            sel = new int[1];
                            int cr = posCrew + 1;
                            sel[0] = cr;
                            try {
                                datagram.take(9999, sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //TAKE 0x00
                            sel = new int[0];
                            try {
                                datagram.take(9999, sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {
                            datagram.pass(9999);
                        } catch (IOException e) {
                            e.printStackTrace();
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
                        e.printStackTrace();
                    }
                    System.out.println(comanda + " " + bytePoint);
                }
                else if (comanda.equals("WINS")) {
                        try {
                            datagram.read_space();
                            datagram.read_char();
                            System.out.println(comanda);
                            try {
                                Thread.sleep(4000);//comentarlo con la profe
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            datagram.exit();
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
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
