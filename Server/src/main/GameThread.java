package main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * <h1>GameThread class</h1>
 * Implements multithread capabilities.
 *
 * @author  leRoderic
 * @version 1.0
 * @since   24-02-2020
 */

public class GameThread implements Runnable{

    private Game game;

    /**
     * Constructor of GameThread.
     *
     * @param s1    socket for player1
     * @param s2    socket for player2
     * @param sp    gamemode
     * @param players   common hashmap of players
     * @throws IOException e
     */
    public GameThread(Socket s1, Socket s2, boolean sp, HashMap<Integer, Integer> players) throws IOException {

        game = new Game(s1, s2, sp, players);
    }

    /**
     * Starts the game logic.
     *
     */
    public void run(){
        try {
            game.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
