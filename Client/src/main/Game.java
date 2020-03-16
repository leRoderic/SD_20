package main;

import java.io.*;


public class Game {

    private Menu menu;
    private Datagram datagram;
    private boolean partida = false;
    private int mode; //0->client 1->aleatori

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
                    } else if (com.equals("EXIT")) {
                        this.setPartida(false);
                    } else {
                        System.exit(1);
                    }
                } else if (comanda.equals("LOOT") || comanda.equals("PLAY") || comanda.equals("PNTS")) {
                    if (com.equals("EXIT")) {
                        this.setPartida(false);
                    } else {
                        System.exit(1);
                    }
                } else if (comanda.equals("DICE")) {
                    if (com.equals("PASS")) {
                    } else if (com.equals("EXIT")) {
                        this.setPartida(false);
                    } else if (com.equals("TAKE")) {
                    } else {
                        System.exit(1);
                    }
                }
            }
            //mode aleatori
            if (mode == 1) {
                int client_id = menu.getClientID();
                try {
                    datagram.strt(client_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    comanda = datagram.read_command();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (comanda.equals("CASH")) {
                    try {
                        datagram.bett();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (comanda.equals("DICE")) {
                    int numberRand = (int) Math.random() * 5;
                    if(numberRand != 0) {
                        int posCaptain = -1, posShip = -1, posCrew = -1;
                        boolean ship = false;
                        boolean captain = false;
                        boolean crew = false;
                        int[] dice = new int[0];
                        try {
                            dice = datagram.read_dice();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < dice.length; i++) {
                            if (dice[i] == 6) {
                                posShip = i;
                                ship = true;
                            }
                            if (ship && dice[i] == 5) {
                                posCaptain = i;
                                captain = true;
                            }
                            if (ship && dice[i] == 4) {
                                posCrew = i;
                                crew = true;
                            }
                        }

                        byte[] sel = new byte[0];
                        if (ship && captain && crew) {
                            byte sh = (byte) ((byte) posShip + 1);//datagram.int32ToBytes(posShip + 1, ComUtils.Endianness.BIG_ENNDIAN);
                            byte ca = (byte) ((byte) posCaptain + 1);//datagram.int32ToBytes(posCaptain + 1, ComUtils.Endianness.BIG_ENNDIAN);
                            byte cr = (byte) ((byte) posCrew + 1);//datagram.int32ToBytes(posCrew + 1, ComUtils.Endianness.BIG_ENNDIAN);
                            sel[0] = sh;
                            sel[1] = ca;
                            sel[2] = cr;
                            try {
                                datagram.take(menu.getClientID(), sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (ship && captain) {
                            byte sh = (byte) ((byte) posShip + 1);
                            byte ca = (byte) ((byte) posCaptain + 1);
                            sel[0] = sh;
                            sel[1] = ca;
                            try {
                                datagram.take(menu.getClientID(), sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (ship) {
                            byte sh = (byte) ((byte) posShip + 1);
                            sel[0] = sh;
                            try {
                                datagram.take(menu.getClientID(), sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //TAKE 0x00
                            try {
                                datagram.take(menu.getClientID(), sel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        try {
                            datagram.pass(menu.getClientID());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (comanda.equals("WINS")) {
                    if (comanda.equals("CASH")) {
                        try {
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
}
