package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h1>Game class</h1>
 * Game logic of both modes, single and 2 player.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   24-02-2020
 */

public class Game {

    private Datagram com1, com2;
    private BufferedWriter log;
    private State state;
    private boolean singlePlayer;
    private int[] dices;
    private int[] dTaken; // Blocks selected dices
    private boolean captain, ship, crew, improveLast;
    private Random random = new Random();
    private HashMap<Integer, Integer> players;
    private final int UKNWN_LIMIT = 3; // Limits the syntax errors Clients can make

    /**
     * Game's class constructor.
     *
     * @param s1    socket for player1
     * @param s2    socket for player2
     * @param singlePlayer  gamemode
     * @param players   common cash data of the players
     * @throws IOException  e
     */
    public Game(Socket s1, Socket s2, boolean singlePlayer, HashMap<Integer, Integer> players) throws IOException {

        this.com1 = new Datagram(s1);
        this.com1.setWinValue(0); // To identify clients in multiplayer.
        // In single player mode only one socket is needed. The other is used in multiplayer mode.
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

    /**
     * Single player game mode.
     *
     * @param com   the socket used for the communications
     * @param isSP  gamemode
     * @param clientNumber  to identify clients on the log
     * @return  player points + id in case of single player, otherwise null or -1
     * @throws IOException e
     */
    private int[] one_player (Datagram com, boolean isSP, String clientNumber) throws IOException {

        int pool = 0;
        String command = "";
        String errorMessage = "";
        int pID = -1;
        int len = 0, sel = 0, id = 0, cCash = 0;
        int pPoints = 0, sPoints = 0;
        boolean finished = false;

        while(!finished) {

            switch(this.state){

                case INIT:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    if(command.equals("STRT")){
                        try {
                            pID = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        log.write("C" + clientNumber + ": STRT " + pID + "\n");
                        this.state = State.BETT;
                        synchronized (players){
                            // Retrieving player's cash. In case of a new player initial value is 10.
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
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }

                    }else if(command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                    }
                    break;
                case BETT:
                    synchronized (players){
                        // Synchronized block used to secure access the common player data.
                        if(players.containsKey(pID)){
                            cCash = players.get(pID);
                        }else{
                            cCash = 10;
                            players.put(pID, 10);
                        }
                    }
                    if(cCash < 2){
                        finished = true;
                        errorMessage = "You can't play because you are broke!";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        break;
                    }
                    try {
                        command = "";
                        command = com.read_command();
                    } catch (Exception e) {

                        if(e instanceof SocketException){
                            log.close();
                            finished = true;
                        }else {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                    }
                    if(command.equals("BETT")){
                        cCash -= 2;
                        pool += 4;
                        // ClientNumber is used to identify each client as the log is generated(C1 or C2).
                        log.write("C" + clientNumber + ": BETT\n");
                        log.write("S: LOOT 2\n");
                        try {
                            com.loot(2);
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        // Win value is also used to indicate turns. efficiency++
                        log.write("S: PLAY '" + com.getWinValue() + "' \n");
                        try {
                            com.play(com.getWinValue());
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        // Resets the taken dices to all not taken.
                        reset_taken_values();
                        // Throw dices.
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        this.state = State.ROLL1;
                    }else if (command.equals("EXIT")){
                        // Client can EXIT at any time.
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                    }else {
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                    }
                    break;
                case ROLL1:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    if(command.equals("TAKE")){
                        // After entering a valid command the stupid counter is set back to 0.
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        ArrayList<Integer> rec2 = new ArrayList();
                        // Reading client's dice selection.
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = "Exiting due to error " + e.getMessage();
                                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                                log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                                this.state = State.QUIT;
                                break;
                            }
                            if(take_validator(sel)){
                                dTaken[sel-1] = 0; // Blocks a dice from being thrown again.
                                rec.add(sel); // For log purposes.
                                rec2.add(dices[sel-1]); // Also, for log purposes.
                            }
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " 0x0" + len + selection_toString(rec) + "\n");
                        // take_updater needs the values sorted in decreasing order. Makes sense if you look at its
                        // implementation.
                        Collections.sort(rec2, Collections.<Integer>reverseOrder());
                        take_updater(rec2);
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        this.state = State.ROLL2;

                    }else if(command.equals("PASS")){
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        log.write("S: PASS " + id + "\n");
                        this.state = State.EXIT;
                    }else if (command.equals("EXIT")){
                        log.write("C" + clientNumber + ": EXIT\n");
                        this.state = State.QUIT;
                        break;
                    }else{
                        errorMessage = "Command not understood";
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush();
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                    }
                    break;
                case ROLL2:
                    try {
                        command = com.read_command();
                    } catch (Exception e) {
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    if(command.equals("TAKE")){
                        try {
                            id = com.readNextInt();
                            len = com.read_next_int_in_bytes();
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        ArrayList<Integer> rec = new ArrayList();
                        ArrayList<Integer> rec2 = new ArrayList();
                        for(int i =0; i<len; i++){
                            try {
                                sel = com.read_next_int_in_bytes();
                            } catch (Exception e) {
                                errorMessage = "Exiting due to error " + e.getMessage();
                                log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                                log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                                com.sendErrorMessage(errorMessage, errorMessage.length());
                                this.state = State.QUIT;
                                break;
                            }
                            if(take_validator(sel)){
                                dTaken[sel-1] = 0; // Blocks a dice from being thrown again.
                                rec.add(sel); // For log purposes.
                                rec2.add(dices[sel-1]); // Also, for log purposes.
                            }
                        }
                        log.write("C" + clientNumber + ": TAKE " + id + " 0x0" + len + selection_toString(rec) + "\n");
                        Collections.sort(rec2, Collections.<Integer>reverseOrder());
                        take_updater(rec2);
                        throw_dices();
                        log.write("S: DICE " + pID + dices_toString() + "\n");
                        try {
                            com.dice(pID, dices);
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
                        }
                        // If the client chooses to take values again, if better values are gained on the last roll they
                        // will automatically be improved on the player's score.
                        this.improveLast = true;
                        this.state = State.EXIT;

                    }else if(command.equals("PASS")){
                        try {
                            id = com.readNextInt();
                        } catch (Exception e) {
                            errorMessage = "Exiting due to error " + e.getMessage();
                            log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                            log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                            com.sendErrorMessage(errorMessage, errorMessage.length());
                            this.state = State.QUIT;
                            break;
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
                        this.state = State.QUIT;
                    }
                    break;
                case QUIT:
                    finished = true;
                    break;
                case EXIT:
                    if(this.improveLast)
                        improveSelection();
                    pPoints = getPoints(); // Retrieve player points.
                    log.write("S: PNTS " + pID + " " + pPoints + "\n");
                    try {
                        com.points(pID, pPoints);
                    } catch (Exception e) {
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    if(!isSP) {
                        return new int[]{pID, pPoints};
                    }
                    // The code below here only applies in single player mode as the Server's game is emulated and therefore
                    // communications between the other player are not needed. In case of multiplayer mode the points as
                    // well as the player's id are returned. See line 410.
                    sPoints = serverPlays(); // Now the server emulates a game.
                    int win = -1;
                    // Win value cannot be used here as there is only one real client playing.
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
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    log.write("S: CASH " + cCash + "\n");
                    try {
                        com.cash(cCash);
                    } catch (Exception e) {
                        errorMessage = "Exiting due to error " + e.getMessage();
                        log.write("S: ERRO '" + errorMessage.length() + "' " + errorMessage + "\n");
                        log.flush(); // If an error occurs, the log is dumped immediately to prevent losses of data.
                        com.sendErrorMessage(errorMessage, errorMessage.length());
                        this.state = State.QUIT;
                        break;
                    }
                    this.state = State.BETT;
                    synchronized (players){
                        // Updating player's dough safely with the hashmap's lock.
                        players.put(pID, cCash);
                    }
                    break;
            }
        }
        return null; // Useless return, but needed in order to avoid compiler whining.
    }

    /**
     * Selects and executes the game in the pre-determined game mode.
     *
     * @throws IOException e
     */
    public void run() throws IOException {

        Datagram fCom, sCom, tCom;
        int[] ret;
        int fID = 0, sID = 0;
        int fPoints = 0, sPoints = 0;
        int pool = 0, cCash1 = 0, cCash2 = 0;
        boolean fComExit = false, sComExit = false, swap = false;
        boolean fFirstTime = false, sFirstTime = false;
        String errorMessage = "";

        if(this.singlePlayer){
            // In case of single player.
            one_player(this.com1, true, "");
        }else{
            // Two player mode.

            // The player who starts first is selected randomly, so that's what this is.
            if(random.nextInt(1)==0){
                fCom = this.com1;
                sCom = this.com2;
            }else{
                fCom = this.com2;
                sCom = this.com1;
            }

            while(true){
                pool += 4;

                // First player plays.
                if(!fFirstTime){
                    fFirstTime = true;
                    this.state = State.INIT;
                }else{
                    this.state = State.BETT;
                }
                ret = one_player(fCom,false, Integer.toString(fCom.getWinValue()+ 1));
                if(!sFirstTime){
                    sFirstTime = true;
                    this.state = State.INIT;
                }else{
                    this.state = State.BETT;
                }

                if(ret == null){ // In case the player exits the game the loop will be broken.
                    fComExit = true;
                    break;
                }else{
                    // Otherwise, player's id and points are retrieved.
                    fID = ret[0];
                    fPoints = ret[1];
                }
                // The other player plays.
                ret = one_player(sCom,false, Integer.toString(sCom.getWinValue() + 1));
                // Same as before, in case the second player decides to skip normal execution and exits early.
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
                        // Updating cash in base of who has won and who has lost.
                        cCash1 = players.get(fID);
                        cCash2 = players.get(sID);
                        cCash1 = cCash1 + pool - 2;
                        cCash2 = cCash2 - 2;
                        players.put(fID, cCash1);
                        players.put(sID, cCash2);
                    }
                    pool = 0;
                    // If the first player has won, the other one starts the next round. They swap.
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
                    // If the winner was already the second to play there is no need to swap.
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
                    // In case of a tie, no swap has to be considered.
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
                // Swap of player turns happens here.
                if(swap){
                    tCom = fCom;
                    fCom = sCom;
                    sCom = tCom;
                }
            }
            // When a player exits the game and the other one is still playing or wanting to, an error message is sent
            // to him/her.
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
        // Closing the log, very important.
        this.log.close();
    }

    /**
     * Improves selection based on the current dice values.
     */
    private void improveSelection(){

        // Basically will try to get all 3 values (ship, captain, crew) or if it has already one, it will try to get
        // the remaining ones.
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

    /**
     * Server emulated play. Very smart AI.
     *
     * @return server points
     * @throws IOException excep
     */
    private int serverPlays() throws IOException {

        reset_taken_values();
        throw_dices();
        String sel = "";
        log.write("S: DICE 1010" + dices_toString() + "\n");
        for(int i=0; i < 2; i++) {

            // 1 out of 3 times it will choose PASS instead of TAKE.
            if(random.nextInt(3)==0){
                log.write("S: PASS 1010 \n");
                break;
            }else{
                // Similar AI to improve_selection.
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

    /**
     * Converts simple array to ArrayList.
     *
     * @param ar simple arr
     * @return arraylist with arr values
     */
    private ArrayList arrayToArrayList(int[] ar){

        ArrayList n = new ArrayList();
        for(int i=0; i<ar.length;i++){
            n.add(ar[i]);
        }
        return n;
    }

    /**
     * Checks if a value is avaiable on the current dice values.
     *
     * @param c value to search for
     * @return true: value found   false: value !found
     */
    private boolean value_in_dices(int c){

        for(int i=0; i<5; i++){
            if(dices[i] == c)
                return true;
        }
        return false;
    }

    /**
     * Searchs and returns the index of a value in the dices array.
     *
     * @param c value to look for
     * @return  index or -1 if !found
     */
    private int get_value_index(int c){

        for(int i=0; i<5; i++){
            if(dices[i] == c)
                return i;
        }
        return -1;
    }

    /**
     * Computes player points
     *
     * @return player points
     */
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

    /**
     * Resets fixed dice values as well as the main values status booleans.
     *
     */
    private void reset_taken_values(){

        this.improveLast = false;
        this.ship = this.captain = this.crew = false;
        this.dTaken = new int[]{-1, -1, -1, -1, -1};
    }

    /**
     * For log purposes. Turns an array into a String. Used to convert player's selection to a String so that it can
     * be written on the log.
     *
     * @param a the array to be string-ed of
     * @return string-ed array
     */
    private String selection_toString(ArrayList a){

        String ret = "";
        for(int i=0; i< a.size(); i++){

            ret = ret + " 0x0" + a.get(i);
        }
        return ret;
    }

    /**
     * Checks if a selection is valid.
     *
     * @param v value to check
     * @return valid or not valid
     */
    private boolean take_validator(int v){

        if(v == 4){
            // You can only select crew(4) if you have ship and captain.
            return ship&captain;
        }else if(v == 5){
            // You can only select captain(5) if you have ship.
            return ship;
        }
        return true;
    }

    /**
     * Based on the player's selection it will update the main value status.
     *
     * @param a player's selection
     */
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

    /**
     * Throws all the dices.
     *
     */
    private void throw_dices(){

        for(int i=0; i < 5; i++){

            // If a value is locked because its been already 'saved' it won't be thrown again.
            if(dTaken[i] == -1)
                dices[i] = random.nextInt(7-1)+1; // Range from 1 to 6.
        }
    }

    /**
     * For log purposes. Converts current dice values into a string.
     *
     * @return  string of the dice values
     */
    private String dices_toString(){

        String ret = "";
        for(int i=0; i<5; i++){
            ret = ret + " '" + dices[i] + "'";
        }
        return ret;
    }

    /**
     * All the states of the game logic.
     */
    private enum State{
        INIT,
        BETT,
        ROLL1,
        ROLL2,
        EXIT,
        QUIT
    }
}
