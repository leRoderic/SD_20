package main;

import utils.ComUtils;

import java.io.*;
import java.util.Random;


public class Game {

    private Menu menu;
    private Datagram datagram;
    private boolean partida = false;
    private int mode; //0->client 1->aleatori
    private int idCliente;
    private Random random = new Random();
    private boolean takeShip = false, takeCaptain = false, takeCrew = false;

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
            String c = menu.read_next_command();
            if(c.equals("STRT")){
                try {
                    datagram.strt(idCliente);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(mode == 1) {
            try {
                datagram.strt(9999);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (isPartida()) {
            if (mode == 0) {

                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String com = menu.read_next_command();
                if (comanda.equals("CASH")) {
                    if (com.equals("BET")) {
                        try {
                            datagram.bett();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (com.equals("EXIT")) {
                        try {
                            datagram.exit();
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.exit(1);
                    }
                } else if (comanda.equals("LOOT") || comanda.equals("PLAY") || comanda.equals("PNTS")) {
                    if (com.equals("EXIT")) {
                        try {
                            datagram.exit();
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.exit(1);
                    }
                } else if (comanda.equals("DICE")) {
                    if (com.equals("PASS")) {
                        try {
                            datagram.pass(idCliente);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (com.equals("EXIT")) {
                        try {
                            datagram.exit();
                            this.setPartida(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (com.equals("TAKE")) {
                        /*try {
                            datagram.take(idCliente,);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    } else {
                        System.exit(1);
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
                        datagram.read_char();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(comanda);
                }
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
                            System.out.println(dice[i]);
                            if (dice[i] == 6) {
                                ship = true;
                                posShip = i;
                            }
                        }
                        if (ship) {
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 5) {
                                    captain = true;
                                    posCaptain = i;
                                }
                            }
                        }
                        if (captain) {
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 4) {
                                    crew = true;
                                    posCrew = i;
                                }
                            }
                        }

                        byte[] sel;
                        if (ship && captain && crew) {
                            //if(!takeShip && !takeCaptain && !takeCrew) {
                                sel = new byte[3];
                                byte sh = datagram.int32ToBytes(posShip + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                                byte ca = datagram.int32ToBytes(posCaptain + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                                byte cr = datagram.int32ToBytes(posCrew + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                                sel[0] = sh;
                                sel[1] = ca;
                                sel[2] = cr;
                                try {
                                    datagram.take(9999, sel);
                                    System.out.println("take 3");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                takeShip = true;
                                takeCaptain = true;
                                takeCrew = true;
                            //}
                        } else if (ship && captain) {
                            //if(!takeShip && !takeCaptain) {
                                sel = new byte[2];
                                byte sh = datagram.int32ToBytes(posShip + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                                byte ca = datagram.int32ToBytes(posCaptain + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                                sel[0] = sh;
                                sel[1] = ca;
                                try {
                                    datagram.take(9999, sel);
                                    System.out.println("take 2");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                takeShip = true;
                                takeCaptain = true;
                            //}
                        } else if (ship) {
                            sel = new byte[1];
                            byte sh = datagram.int32ToBytes(posShip + 1, ComUtils.Endianness.BIG_ENNDIAN)[3];
                            sel[0] = sh;
                            try {
                                datagram.take(9999, sel);
                                System.out.println("take 1");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //TAKE 0x00
                            sel = new byte[0];
                            try {
                                datagram.take(9999, sel);
                                System.out.println("take 0");
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
                                Thread.sleep(1200);
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
}
