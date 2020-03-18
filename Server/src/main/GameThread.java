package main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class GameThread implements Runnable{

    private Game game;

    public GameThread(Socket s1, Socket s2, boolean sp, HashMap<Integer, Integer> players) throws IOException {

        game = new Game(s1, s2, sp, players);
    }

    public void run(){
        try {
            game.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
