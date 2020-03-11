package main;

import utils.ComUtils;
import java.io.*;

public class Game {

    private Menu menu;
    private Datagram datagram;
    private boolean partida = false;
    private int tirades;
    private EstatPartida estat;

    public Game(Datagram datagram, Menu menu){
        this.datagram = datagram;
        this.menu = menu;
        estat = EstatPartida.INIT_SHIP;
        partida = true;
        this.tirades = 3;

        partida();

    }

    public boolean isPartida() {
        return partida;
    }

    public void setPartida(boolean partida) {
        this.partida = partida;
    }

    public void partida(){
        String comanda = null;
        boolean takeShip = false;
        boolean takeCaptain = false;
        boolean takeCrew = false;

        while (this.isPartida()) {

            switch (this.estat) {

                case INIT_SHIP:
                    try {
                        comanda = datagram.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (comanda.equals("CASH")) {
                        String s = menu.read_next_command();
                        if(s.equals("BETT")){
                            //Client ha enviat per consola BETT
                        }
                        if(s.equals("EXIT")){
                            this.estat = EstatPartida.EXIT;
                        }
                    }
                    if (comanda.equals("LOOT")){
                        //No fa res
                    }
                    if (comanda.equals("DICE")){
                        if(menu.read_next_command().equals("TAKE")) {
                            boolean ship = false;
                            boolean captain = false;
                            boolean crew = false;
                            int posShip = -1, posCaptain = -1, posCrew = -1;
                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }
                            int[] dice = new int[0];
                            try {
                                dice = datagram.read_dice();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < dice.length; i++) {
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
                            if (ship) {
                                int[] take = new int[0];
                                try {
                                    take = datagram.read_take();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                for (int j = 0; j < take.length; j++) {
                                    if (take[j] == dice[posShip])
                                        takeShip = true;
                                    else if (captain && take[j] == dice[posCaptain])
                                        takeCaptain = true;
                                    else if (crew && take[j] == dice[posCrew])
                                        takeCrew = true;
                                }
                            }

                            if (takeShip && takeCaptain && takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else if (takeShip && takeCaptain)
                                this.estat = EstatPartida.CREW;
                            else if (takeShip)
                                this.estat = EstatPartida.CAPTAIN;
                            else
                                this.estat = EstatPartida.INIT_SHIP;
                        }
                        if(menu.read_next_command().equals("PASS")){

                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }

                            if (takeShip && takeCaptain && takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else if (takeShip && takeCaptain)
                                this.estat = EstatPartida.CREW;
                            else if (takeShip)
                                this.estat = EstatPartida.CAPTAIN;
                            else
                                this.estat = EstatPartida.INIT_SHIP;
                        }
                    }
                    break;
                case CAPTAIN:
                    try {
                        comanda = datagram.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (comanda.equals("DICE")){

                        if(menu.read_next_command().equals("TAKE")) {
                            boolean captain = false;
                            boolean crew = false;
                            int posCaptain = -1, posCrew = -1;

                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }
                            int[] dice = new int[0];
                            try {
                                dice = datagram.read_dice();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 5) {
                                    captain = true;
                                    posCaptain = i;
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
                            if (captain) {
                                int[] take = new int[0];
                                try {
                                    take = datagram.read_take();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                for (int j = 0; j < take.length; j++) {
                                    if (take[j] == dice[posCaptain])
                                        takeCaptain = true;
                                    else if (crew && take[j] == dice[posCrew])
                                        takeCrew = true;
                                }
                            }
                            if (takeCaptain && takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else if (takeCaptain)
                                this.estat = EstatPartida.CREW;
                            else
                                this.estat = EstatPartida.CAPTAIN;
                        }
                        if(menu.read_next_command().equals("PASS")) {

                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }

                            if (takeCaptain && takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else if (takeCaptain)
                                this.estat = EstatPartida.CREW;
                            else
                                this.estat = EstatPartida.CAPTAIN;
                        }
                    }
                    break;

                case CREW:

                    try {
                        comanda = datagram.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (comanda.equals("DICE")){

                        if(menu.read_next_command().equals("TAKE")) {
                            boolean crew = false;
                            int posCrew = -1;

                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }
                            int[] dice = new int[0];
                            try {
                                dice = datagram.read_dice();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 4) {
                                    crew = true;
                                    posCrew = i;
                                }
                            }
                            if (crew) {
                                int[] take = new int[0];
                                try {
                                    take = datagram.read_take();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                for (int j = 0; j < take.length; j++) {
                                    if (take[j] == dice[posCrew])
                                        takeCrew = true;
                                }
                            }
                            if (takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else
                                this.estat = EstatPartida.CREW;
                        }
                        if(menu.read_next_command().equals("PASS")) {

                            if (tirades != 0) {
                                tirades--;
                            } else {
                                this.estat = EstatPartida.FINISH;
                            }

                            if (takeCrew)
                                this.estat = EstatPartida.FINISH;
                            else
                                this.estat = EstatPartida.CREW;
                        }
                    }
                    break;

                case FINISH:
                    try {
                        comanda = datagram.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if( comanda.equals("WINS") || comanda.equals("CASH")){
                        if(menu.read_next_command().equals("BET")){
                            this.tirades = 3;
                            this.estat = EstatPartida.INIT_SHIP;
                        }
                        if(menu.read_next_command().equals("EXIT)")){
                            this.estat = EstatPartida.EXIT;
                        }
                    }
                    break;
                case EXIT:
                    this.setPartida(false);
                    break;
                default:
                    break;
            }
        }

    }


    public enum EstatPartida {
        INIT_SHIP,
        CAPTAIN,
        CREW,
        FINISH,
        EXIT};

}
