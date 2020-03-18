package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {

    private Datagram com1, com2;
    private BufferedWriter log;
    private State state;
    private int cash1, cash2;
    private boolean singlePlayer;
    private int[] dices;
    private int[] dTaken;
    private boolean captain, ship, crew, improveLast;
    private Random random = new Random();

    public Game(Socket s1, Socket s2, boolean singlePlayer) throws IOException {

        this.com1 = new Datagram(s1);
        this.com1.setWinValue(0);
        if(s2 != null) {
            this.com2 = new Datagram(s2);
            this.com2.setWinValue(1);
        }
        this.log = new BufferedWriter(new FileWriter("Server-" + Thread.currentThread().getName()  + ".log"));
        this.state = State.INIT;
        this.cash1 = 10;
        this.cash2 = 10;

        this.singlePlayer = singlePlayer;
        this.dices = new int[]{-1, -1, -1, -1, -1};
        this.dTaken = new int[]{-1, -1, -1, -1, -1};
    }

    private int one_player (Datagram com, int pCash, boolean isSP, String clientNumber) throws IOException {

        int pool = 0;
        String command = "";
        String errorMessage = "";
        int pID = -1;
        int len = 0, sel = 0, id = 0;
        int pPoints = 0, sPoints = 0;
        boolean finished = false;

        while(!finished) {

            switch(this.state){

                case INIT:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();

                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("STRT")){
                        try {
                            pID = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("C" + clientNumber + ": STRT " + pID + "\n");
                        this.state = State.BETT;
                        log.write("S: CASH " + pCash + "\n");
                        try {
                            com.cash(pCash);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                    }else if(command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    break;
                case BETT:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {

                        if(e instanceof SocketException){
                            log.close();
                            finished = true;
                        }else {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                    }
                    if(command.equals("BETT")){
                        pCash -= 2;
                        pool += 4;
                        log.write("C" + clientNumber + ": BETT\n");
                        log.write("S: LOOT 2\n");
                        try {
                            com.loot(2);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("S: PLAY 0\n");
                        try {
                            com.play(0);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        reset_taken_values();
                        throw_dices();
                        log.write("S: DICE " + pID + " " + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.state = State.ROLL1;
                    }else if (command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else {
                        errorMessage = "Command not understood";
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    break;
                case ROLL1:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("TAKE")){
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                                log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                                log.flush();
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                            }
                            dTaken[sel-1] = 0;
                            rec.add(dices[sel-1]);
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " " + len + selection_toString(rec) + "\n");
                        Collections.sort(rec);
                        take_updater(rec);
                        throw_dices();
                        log.write("S: DICE " + pID + " " + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.state = State.ROLL2;

                    }else if(command.equals("PASS")){
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("S: PASS " + id + "\n");
                        take_updater(arrayToArrayList(dices));
                        this.state = State.EXIT;
                    }else if (command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                        break;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    break;
                case ROLL2:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("TAKE")){
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                                log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                                log.flush();
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                            }
                            dTaken[sel-1] = 0;
                            rec.add(dices[sel-1]);
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " " + len + selection_toString(rec) + "\n");
                        Collections.sort(rec);
                        take_updater(rec);
                        throw_dices();
                        log.write("S: DICE " + pID + " " + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.improveLast = true;
                        this.state = State.EXIT;

                    }else if(command.equals("PASS")){
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        take_updater(arrayToArrayList(dices));
                        log.write("S: PASS " + id + "\n");
                        this.state = State.EXIT;
                    }else if(command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    break;
                case QUIT:
                    finished = true;
                    break;
                case EXIT:
                    if(this.improveLast)
                        improveSelection();
                    pPoints = getPoints();
                    log.write("S: PNTS " + pID + " " + pPoints + "\n");
                    try {
                        com.points(pID, pPoints);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(!isSP) {
                        return pPoints;
                    }
                    sPoints = serverPlays();
                    int win = -1;
                    if (pPoints > sPoints) {
                        win = 0;
                        pCash += pool;
                    }else if (sPoints < pPoints) {
                        win = 1;
                    }else if (sPoints == pPoints) {
                        win = 2;
                        pCash += 2;
                    }
                    pool = 0;
                    try {
                        log.write("S: WINS " + win + "\n");
                        com.wins(win);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    this.state = State.BETT;
                    break;
            }
        }
        return 0;
    }

    public void run() throws IOException {

        Datagram fCom, sCom, tCom;
        int fCash, sCash, tCash;
        int fPoints = 0, sPoints = 0;
        int pool = 0;
        String errorMessage = "";

        if(this.singlePlayer){
            one_player(this.com1, this.cash1, true, "");
        }else{

            if(random.nextInt(1)==0){
                fCom = this.com1;
                sCom = this.com2;
                fCash = this.cash1;
                sCash = this.cash2;
            }else{
                fCom = this.com2;
                sCom = this.com1;
                fCash = this.cash2;
                sCash = this.cash1;
            }

            while(true){
                pool += 4;
                fPoints = one_player(fCom, fCash, false, "" + fCom.getWinValue() + 1);
                sPoints = one_player(sCom, sCash, false, "" + sCom.getWinValue() + 1);

                if (fPoints > sPoints) {
                    fCash += pool;
                    pool = 0;
                    try {
                        log.write("S: WINS " + fCom.getWinValue() + "\n");
                        fCom.wins(fCom.getWinValue());
                        sCom.wins(fCom.getWinValue());
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + " " + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    // Swap turns --> loser first
                    tCom = fCom;
                    tCash = fCash;
                    fCom = sCom;
                    fCash = sCash;
                    sCom = tCom;
                    sCash = tCash;
                }else if (sPoints > fPoints) {
                    sCash += pool;
                    pool = 0;
                    try {
                        log.write("S: WINS " + sCom.getWinValue() + "\n");
                        fCom.wins(sCom.getWinValue());
                        sCom.wins(sCom.getWinValue());
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                }else if (sPoints == fPoints) {
                    try {
                        log.write("S: WINS " + 2 + "\n");
                        fCom.wins(2);
                        sCom.wins(2);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO " + errorMessage.length() + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                }
                // Manage in case someone disconnects
            }
        }
        this.log.close();
    }

    private void improveSelection(){
        if(!ship && value_in_dices(6)){
            this.ship = true;
            dTaken[get_value_index(6)] = 0;
            if(value_in_dices(5)) {
                this.captain = true;
                dTaken[get_value_index(5)] = 0;
                if (value_in_dices(4)) {
                    this.crew = true;
                    dTaken[get_value_index(4)] = 0;
                }
            }
        }else if(ship && !captain && value_in_dices(5)){
            this.captain = true;
            dTaken[get_value_index(5)] = 0;
            if(value_in_dices(4)){
                this.crew = true;
                dTaken[get_value_index(4)] = 0;
            }
        }else if(ship && captain && !crew && value_in_dices(4)){
            this.crew = true;
            dTaken[get_value_index(4)] = 0;
        }
    }

    private int serverPlays() throws IOException {

        reset_taken_values();
        throw_dices();
        String sel = "";
        log.write("S: DICE 1010 " + dices_toString() + "\n");
        for(int i=0; i < 2; i++) {

            if(random.nextInt(3)==0){
                log.write("S: PASS 1010 \n");
                break;
            }else{
                if(!ship && value_in_dices(6)){
                    this.ship = true;
                    dTaken[get_value_index(6)] = 0;
                    if(value_in_dices(5)){
                        this.captain = true;
                        dTaken[get_value_index(5)] = 0;
                        if(value_in_dices(4)){
                            this.crew = true;
                            dTaken[get_value_index(4)] = 0;
                            sel = "3 " + get_value_index(6) + " " + get_value_index(5) + " " + get_value_index(4);
                        }else
                            sel = "2 " + get_value_index(6) + " " + get_value_index(5);
                    }else
                        sel = "1 " + get_value_index(6);
                }else if(ship && !captain && value_in_dices(5)){
                    this.captain = true;
                    dTaken[get_value_index(5)] = 0;
                    if(value_in_dices(4)){
                        this.crew = true;
                        dTaken[get_value_index(4)] = 0;
                        sel = "2 " + get_value_index(5) + " " + get_value_index(4);
                    }else
                        sel = "1 " + get_value_index(5);
                }else if(ship && captain && !crew && value_in_dices(4)){
                    this.crew = true;
                    dTaken[get_value_index(4)] = 0;
                    sel = "1 " + get_value_index(4);
                }
                log.write("S: TAKE 1010 " + sel + "\n");
                throw_dices();
                log.write("S: DICE 1010 " + dices_toString() + "\n");
            }
        }
        log.write("S: PNTS 1010 " + getPoints() + "\n");
        
        return getPoints();
    }

    private ArrayList arrayToArrayList(int[] ar){

        ArrayList n = new ArrayList();
        for(int i=0; i<ar.length;i++){
            n.add(ar[i]);
        }
        return n;
    }

    private boolean value_in_dices(int c){

        for(int i=0; i<5; i++){
            if(dices[i] == c)
                return true;
        }
        return false;
    }

    private int get_value_index(int c){

        for(int i=0; i<5; i++){
            if(dices[i] == c)
                return i;
        }
        return -1;
    }

    private int getPoints(){

        int pnts = 0;
        if(!ship && !captain && !crew) {
            return 0;
        }
        else if(ship && captain && crew){
            for(int i=0; i<5;i++){
                pnts += dices[i];
            }
        }
        return Math.max(pnts-15, 0);
    }

    private void reset_taken_values(){

        this.improveLast = false;
        this.ship = this.captain = this.crew = false;
        this.dTaken = new int[]{-1, -1, -1, -1, -1};
    }

    private String selection_toString(ArrayList a){

        String ret = "";
        for(int i=0; i< a.size(); i++){

            ret = ret + " " + a.get(i);
        }
        return ret;
    }

    private void take_updater(ArrayList<Integer> a) {

        int i;
        for (int j = 0; j < a.size(); j++) {

            i = a.get(j);

            if (i == 6 && !captain && !ship && !crew)
                this.ship = true;
            else if (i == 5 && !captain && !crew && ship)
                this.captain = true;
            else if (i == 4 && captain && ship && !crew)
                this.crew = true;
        }
    }

    private boolean check_win_condition(){

        return captain && ship && crew;
    }

    private void throw_dices(){

        for(int i=0; i < 5; i++){

            if(dTaken[i] == -1)
                dices[i] = random.nextInt(7-1)+1;
        }
    }

    private String dices_toString(){

        String ret = "";
        for(int i=0; i<5; i++){
            ret = ret + " " + dices[i];
        }
        return ret;
    }

    private enum State{
        INIT,
        BETT,
        ROLL1,
        ROLL2,
        EXIT,
        ERROR,
        QUIT
    }
}
