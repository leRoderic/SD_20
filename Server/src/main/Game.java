package main;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

public class Game {

    private Datagram com;
    private Logger log;
    private State state;
    private int cash;
    private int gameMode;
    private int[] dices;
    private int[] dTaken;
    private Random random = new Random();

    public Game(Datagram d, Logger l, int gameMode) {

        this.com = d;
        this.log = l;
        this.state = State.INIT;
        this.cash = 10;
        this.gameMode = gameMode;
        this.dices = new int[]{-1, -1, -1, -1, -1};
        this.dices = new int[]{-1, -1, -1, -1, -1};
    }

    public void one_player (){

        String command = "";
        int pID = -1;

        while(!finished()) {

            switch(this.state){

                case INIT:
                    try {
                        command = com.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(command.equals("STRT")){
                        try {
                            pID = com.readNextInt();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        log.info("C: STRT " + pID);
                        this.state = State.BETT;
                        log.info("S: CASH " + this.cash);
                        try {
                            com.cash(this.cash);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BETT:
                    try {
                        command = com.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(command.equals("BETT")){
                        this.cash -= 2;
                        log.info("C: BETT");
                        log.info("S: LOOT 2");
                        try {
                            com.loot(2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        log.info("S: PLAY " + this.gameMode);
                        try {
                            com.play(this.gameMode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        throw_dices();
                        log.info("S: DICE " + dices_toString());
                        try {
                            com.dice(pID, dices);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.state = State.ROLL1;
                    }
                case ROLL1:
                    int len = 0, sel = 0;
                    try {
                        command = com.read_command();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(command.equals("TAKE")){
                        try {
                            len = com.read_int_in_bytes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_int_in_bytes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dTaken[sel-1] = 0;
                        }


                    }



            }

        }


    }

    private void throw_dices(){

        for(int i=0; i < 5; i++){

            if(dTaken[i] == -1)
                dices[i] = random.nextInt(7-1)+1;
        }
    }

    private String dices_toString(){

        String ret = "";
        for(int i=0; i<6; i++){
            ret = ret + " " + dices[i];
        }
        return ret;
    }

    private boolean finished(){

        if(this.state == State.EXIT || this.state == State.ERROR)
            return true;
        return false;
    }

    private enum State{
        INIT,
        BETT,
        ROLL1,
        ROLL2,
        EXIT,
        ERROR
    }
}
