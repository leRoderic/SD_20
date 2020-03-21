package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Game {

    private Datagram com1, com2;
    private BufferedWriter log;
    private State state;
    private boolean singlePlayer;
    private int[] dices;
    private int[] dTaken;
    private boolean captain, ship, crew, improveLast;
    private Random random = new Random();
    private HashMap<Integer, Integer> players;
    private final int UKNWN_LIMIT = 3;

    public Game(Socket s1, Socket s2, boolean singlePlayer, HashMap<Integer, Integer> players) throws IOException {

        this.com1 = new Datagram(s1);
        this.com1.setWinValue(0);
        if(s2 != null) {
            this.com2 = new Datagram(s2);
            this.com2.setWinValue(1);
        }

        // Original .log file name format was "Server-"Thread.currentThread().getName(). This caused the log file to have
        // always the same name (Server-main.log). Therefore to avoid overwriting older logs and to better match each log to
        // its thread, its been change to the current format.
        Date asd = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");

        this.log = new BufferedWriter(new FileWriter("Server_thread" + Thread.currentThread().getId() + "_"
                + formatter.format(asd).toString() + ".log"));
        this.state = State.INIT;
        this.players = players;
        this.singlePlayer = singlePlayer;
        this.dices = new int[]{-1, -1, -1, -1, -1};
        this.dTaken = new int[]{-1, -1, -1, -1, -1};
    }

    private int[] one_player (Datagram com, boolean isSP, String clientNumber) throws IOException {

        int pool = 0;
        String command = "";
        String errorMessage = "";
        int pID = -1, stpdCounter = 0;
        int len = 0, sel = 0, id = 0, cCash = 0;
        int pPoints = 0, sPoints = 0;
        boolean finished = false;

        while(!finished) {

            switch(this.state){

                case INIT:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();

                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("STRT")){
                        stpdCounter = 0;
                        try {
                            pID = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("C" + clientNumber + ": STRT " + pID + "\n");
                        this.state = State.BETT;
                        synchronized (players){

                            if(players.containsKey(pID)){
                                cCash = players.get(pID);
                            }else{
                                cCash = 10;
                                players.put(pID, 10);
                            }
                        }
                        log.write("S: CASH " + cCash + "\n");
                        try {
                            com.cash(cCash);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }

                    }else if(command.equals("EXIT")){
                        stpdCounter = 0;
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        stpdCounter++;
                        if(stpdCounter >= UKNWN_LIMIT){
                            this.state = State.QUIT;
                        }
                    }
                    break;
                case BETT:
                    synchronized (players){

                        if(players.containsKey(pID)){
                            cCash = players.get(pID);
                        }else{
                            cCash = 10;
                            players.put(pID, 10);
                        }
                    }
                    try {
                        command = "";
                        command = com.read_command();
                    } catch (Exception e) {

                        if(e instanceof SocketException){
                            log.close();
                            finished = true;
                        }else {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                    }
                    if(command.equals("BETT")){
                        stpdCounter = 0;
                        cCash -= 2;
                        pool += 4;
                        log.write("C" + clientNumber + ": BETT\n");
                        log.write("S: LOOT 2\n");
                        try {
                            com.loot(2);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("S: PLAY '" + com.getWinValue() + "' \n");
                        try {
                            com.play(com.getWinValue());
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        reset_taken_values();
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.state = State.ROLL1;
                    }else if (command.equals("EXIT")){
                        stpdCounter = 0;
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else {
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        stpdCounter++;
                        if(stpdCounter >= UKNWN_LIMIT){
                            this.state = State.QUIT;
                        }
                    }
                    break;
                case ROLL1:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("TAKE")){
                        stpdCounter = 0;
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        ArrayList<Integer> rec2 = new ArrayList();
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                                log.flush();
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                            }
                            dTaken[sel-1] = 0;
                            rec.add(sel);
                            rec2.add(dices[sel-1]);
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " 0x0" + len + selection_toString(rec) + "\n");
                        Collections.sort(rec2, Collections.<Integer>reverseOrder());
                        take_updater(rec2);
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.state = State.ROLL2;

                    }else if(command.equals("PASS")){
                        stpdCounter = 0;
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("S: PASS " + id + "\n");
                        this.state = State.EXIT;
                    }else if (command.equals("EXIT")){
                        stpdCounter = 0;
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                        break;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        stpdCounter++;
                        if(stpdCounter >= UKNWN_LIMIT){
                            this.state = State.QUIT;
                        }
                    }
                    break;
                case ROLL2:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(command.equals("TAKE")){
                        stpdCounter = 0;
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        ArrayList<Integer> rec2 = new ArrayList();
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = e.getMessage();
                                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                                log.flush();
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                            }
                            dTaken[sel-1] = 0;
                            rec.add(sel);
                            rec2.add(dices[sel-1]);
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " 0x0" + len + selection_toString(rec) + "\n");
                        Collections.sort(rec2, Collections.<Integer>reverseOrder());
                        take_updater(rec2);
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        this.improveLast = true;
                        this.state = State.EXIT;

                    }else if(command.equals("PASS")){
                        stpdCounter = 0;
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush();
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                        }
                        log.write("S: PASS " + id + "\n");
                        this.state = State.EXIT;
                    }else if(command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        stpdCounter++;
                        if(stpdCounter >= UKNWN_LIMIT){
                            this.state = State.QUIT;
                        }
                    }
                    break;
                case QUIT:
                    if(stpdCounter >= UKNWN_LIMIT){
                        errorMessage = "Finishing game due to unknown command received several times";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
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
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    if(!isSP) {
                        return new int[]{pID, pPoints};
                    }
                    sPoints = serverPlays();
                    int win = -1;
                    if (pPoints > sPoints) {
                        win = 0;
                        cCash += pool;
                    }else if (sPoints > pPoints) {
                        win = 1;
                    }else if (sPoints == pPoints) {
                        win = 2;
                        cCash += 2;
                    }
                    pool = 0;
                    try {
                        log.write("S: WINS '" + win + "'\n");
                        com.wins(win);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    log.write("S: CASH " + cCash + "\n");
                    try {
                        com.cash(cCash);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    this.state = State.BETT;
                    synchronized (players){

                        players.put(pID, cCash);
                    }
                    break;
            }
        }
        return null;
    }

    public void run() throws IOException {

        Datagram fCom, sCom, tCom;
        int[] ret;
        int fID = 0, sID = 0;
        int fPoints = 0, sPoints = 0;
        int pool = 0, cCash1 = 0, cCash2 = 0;
        boolean fComExit = false, sComExit = false, swap = false;
        String errorMessage = "";

        if(this.singlePlayer){
            one_player(this.com1, true, "");
        }else{

            if(random.nextInt(1)==0){
                fCom = this.com1;
                sCom = this.com2;
            }else{
                fCom = this.com2;
                sCom = this.com1;
            }

            while(true){
                pool += 4;

                ret = one_player(fCom,false, Integer.toString(fCom.getWinValue()+ 1));
                this.state = State.INIT;
                if(ret == null){
                    fComExit = true;
                    break;
                }else{
                    fID = ret[0];
                    fPoints = ret[1];
                }
                ret = one_player(sCom,false, Integer.toString(sCom.getWinValue()+ 1));
                this.state = State.INIT;
                if(ret == null){
                    sComExit = true;
                    break;
                }else{
                    sID = ret[0];
                    sPoints = ret[1];
                }

                if (fPoints > sPoints) {
                    try {
                        log.write("S: WINS '" + fCom.getWinValue() + "'\n");
                        log.write("S: WINS '" + fCom.getWinValue() + "'\n");
                        fCom.wins(fCom.getWinValue());
                        sCom.wins(fCom.getWinValue());
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    synchronized (players){

                        cCash1 = players.get(fID);
                        cCash2 = players.get(sID);
                        cCash1 = cCash1 + pool - 2;
                        cCash2 = cCash2 - 2;
                        players.put(fID, cCash1);
                        players.put(sID, cCash2);
                    }
                    pool = 0;
                    // Swap turns --> loser first
                    swap = true;
                }else if (sPoints > fPoints) {
                    try {
                        log.write("S: WINS '" + sCom.getWinValue() + "'\n");
                        log.write("S: WINS '" + sCom.getWinValue() + "'\n");
                        fCom.wins(sCom.getWinValue());
                        sCom.wins(sCom.getWinValue());
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    synchronized (players){

                        cCash1 = players.get(fID);
                        cCash2 = players.get(sID);
                        cCash2 = cCash2 + pool - 2;
                        cCash1 = cCash1 - 2;
                        players.put(fID, cCash1);
                        players.put(sID, cCash2);
                    }
                    pool = 0;
                }else if (sPoints == fPoints) {
                    try {
                        log.write("S: WINS '" + 2 + "'\n");
                        log.write("S: WINS '" + 2 + "'\n");
                        fCom.wins(2);
                        sCom.wins(2);
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        fCom.sendErrorMessage(errorMessage, errorMessage.length());
                        sCom.sendErrorMessage(errorMessage, errorMessage.length());
                    }
                    synchronized (players){

                        cCash1 = players.get(fID);
                        cCash2 = players.get(sID);
                    }
                    pool = 0;
                }
                try {
                    log.write("S: CASH " + cCash1 + "\n");
                    fCom.cash(cCash1);
                    log.write("S: CASH " + cCash2 + "\n");
                    sCom.cash(cCash2);
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                    log.flush();
                    fCom.sendErrorMessage(errorMessage, errorMessage.length());
                    sCom.sendErrorMessage(errorMessage, errorMessage.length());
                }
                this.log.flush();
                if(swap){
                    tCom = fCom;
                    fCom = sCom;
                    sCom = tCom;
                }
            }
            errorMessage = "The other player left, aborting game and exiting";
            if(fComExit){
                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                log.flush();
                this.com2.sendErrorMessage(errorMessage, errorMessage.length());
            }else if(sComExit){
                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                log.flush();
                this.com1.sendErrorMessage(errorMessage, errorMessage.length());
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
        log.write("S: DICE 1010" + dices_toString() + "\n");
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
                            sel = "0x03 0x0" + get_value_index(6) + " 0x0" + get_value_index(5) + " 0x0" + get_value_index(4);
                        }else
                            sel = "0x02 0x0" + get_value_index(6) + " 0x0" + get_value_index(5);
                    }else
                        sel = "0x01 0x0" + get_value_index(6);
                }else if(ship && !captain && value_in_dices(5)){
                    this.captain = true;
                    dTaken[get_value_index(5)] = 0;
                    if(value_in_dices(4)){
                        this.crew = true;
                        dTaken[get_value_index(4)] = 0;
                        sel = "0x02 0x0" + get_value_index(5) + " 0x0" + get_value_index(4);
                    }else
                        sel = "0x01 0x0" + get_value_index(5);
                }else if(ship && captain && !crew && value_in_dices(4)){
                    this.crew = true;
                    dTaken[get_value_index(4)] = 0;
                    sel = "0x01 0x0" + get_value_index(4);
                }
                log.write("S: TAKE 1010 " + sel + "\n");
                throw_dices();
                log.write("S: DICE 1010" + dices_toString() + "\n");
            }
        }
        log.write("S: PNTS 1010 '" + getPoints() + "'\n");
        
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

            ret = ret + " 0x0" + a.get(i);
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
            ret = ret + " '" + dices[i] + "'";
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
