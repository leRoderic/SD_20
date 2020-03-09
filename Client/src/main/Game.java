package main;

import utils.ComUtils;
import java.io.*;
/**
 * <h1>Game class</h1>
 * Game (Ship, Captain & Crew) logic.
 *
 * @author leRoderic
 * @version 1.0
 * @since   24-02-2019
 */
public class Game {

    ComUtils comUtils;
    Datagram datagram;
    int points;
    boolean partida = false;
    int tirades = 3;

    public static enum EstatPartida {START,SHIP,CAPTAIN,CREW,END};
    private EstatPartida estat;

    public Game(ComUtils comUtils,Datagram datagram){
        this.comUtils = comUtils;
        this.datagram = datagram;
        estat = EstatPartida.START;
        partida = true;

        partida();

    }
    public EstatPartida getEstat() {
        return estat;
    }

    public void setEstat(EstatPartida estat) {
        this.estat = estat;
    }

    public boolean isPartida() {
        return partida;
    }

    public void setPartida(boolean partida) {
        this.partida = partida;
    }

    public void partida(){

        /*while (this.isPartida()) {

            switch (getEstat()) {

                String comanda = null;

                case START:
                    try {
                        comanda = comUtils.read_string();
                    } catch (IOException e) {
                        //Error de communicacio
                        System.exit(1);
                    }
                    if (comanda.equals("CASH")) {
                        try {
                            datagram.bett();
                        } catch (IOException) {
                            //Error
                        }
                    }
                    if (comanda.equals("LOOT")){
                        //No fa res
                    }

                    if (comanda.equals("DICE")){

                        boolean ship = false;
                        boolean captain = false;
                        boolean crew = false;
                        boolean takeShip = false;
                        boolean takeCaptain = false;
                        boolean takeCrew = false;
                        int posShip,posCaptain,posCrew;

                        if(tirades != 0) {
                            tirades--;
                        }else{
                            this.setEstat(EstatPartida.END);
                        }
                        int[] dice = comUtils.read_dice();
                        for(int i = 0; i<dice.length;i++) {
                            if (dice[i] == 6) {
                                ship = true;
                                posShip = i;
                            }
                        }
                        if(ship){
                            for(int i = 0; i<dice.length;i++){
                                if(dice[i] == 5){
                                    captain = true;
                                    posCaptain = i;
                                }
                            }
                        }
                        if(captain) {
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 4) {
                                    crew = true;
                                    posCrew = i;
                                }
                            }
                        }
                        if(ship){
                            int[] take =  comUtils.read_take();
                            for(int j = 0; j< take.length;j++){
                                if(take[j] ==  dice[posShip])
                                    takeShip = true;
                                else if(captain && take[j] ==  dice[posCaptain])
                                    takeCaptain = true;
                                else if(crew && take[j] ==  dice[posCrew])
                                    takeCrew = true;
                            }
                        }

                        if(takeShip && takeCaptain && takeCrew)
                            this.setEstat(EstatPartida.CREW);
                        else if(takeShip && takeCaptain)
                            this.setEstat(EstatPartida.CAPTAIN);
                        else if(takeShip)
                            this.setEstat(EstatPartida.SHIP);
                        else
                            this.setEstat(EstatPartida.START);
                    }
                    break;

                case SHIP:
                    try {
                        comanda = comUtils.read_string();
                    } catch (IOException e) {
                        //Error de communicacio
                        System.exit(1);
                    }
                    if (comanda.equals("PASS")) {
                        this.setEstat(EstatPartida.END);
                    }
                    if (comanda.equals("DICE")){

                        boolean captain = false;
                        boolean crew = false;
                        boolean takeCaptain = false;
                        boolean takeCrew = false;
                        int posCaptain,posCrew;

                        if(tirades != 0) {
                            tirades--;
                        }else{
                            this.setEstat(EstatPartida.END);
                        }
                        int[] dice = comUtils.read_dice();
                        for(int i = 0; i<dice.length;i++) {
                            if (dice[i] == 5) {
                                captain = true;
                                posCaptain = i;
                            }
                        }
                        if(captain) {
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 4) {
                                    crew = true;
                                    posCrew = i;
                                }
                            }
                        }
                        if(captain){
                            int[] take =  comUtils.read_take();
                            for(int j = 0; j< take.length;j++){
                                if(take[j] ==  dice[posCaptain])
                                    takeCaptain = true;
                                else if(crew && take[j] ==  dice[posCrew])
                                    takeCrew = true;
                            }
                        }
                        else if(takeCaptain && takeCrew)
                            this.setEstat(EstatPartida.CREW);
                        else if(takeCaptain)
                            this.setEstat(EstatPartida.CAPTAIN);
                        else
                            this.setEstat(EstatPartida.SHIP);
                    }
                    break;

                case CAPTAIN:

                    try {
                        comanda = comUtils.read_string();
                    } catch (IOException e) {
                        //Error de communicacio
                        System.exit(1);
                    }
                    if (comanda.equals("PASS")) {
                        this.setEstat(EstatPartida.END);
                    }
                    if (comanda.equals("DICE")){

                        boolean crew = false;
                        boolean takeCrew = false;
                        int posCrew;

                        if(tirades != 0) {
                            tirades--;
                        }else{
                            this.setEstat(EstatPartida.END);
                        }
                        int[] dice = comUtils.read_dice();
                        for(int i = 0; i<dice.length;i++) {
                            if (dice[i] == 4) {
                                crew = true;
                                posCrew = i;
                            }
                        }
                        if(crew){
                            int[] take =  comUtils.read_take();
                            for(int j = 0; j< take.length;j++){
                                if(take[j] ==  dice[posCrew])
                                    takeCrew = true;
                            }
                        }
                        if(takeCrew)
                            this.setEstat(EstatPartida.CREW);
                        else
                            this.setEstat(EstatPartida.CAPTAIN);
                    }
                    break;

                case CREW:

                    break;

                case END:
                        tirades = 3;
                        this.setPartida(false);
                    break;

                default:

                    break;
            }
        }*/
    }

}
