package main;

import java.io.IOException;
import java.net.Socket;

public class GameThread extends Thread {

    private Game game;

    public GameThread(Socket s1, Socket s2, boolean sp) throws IOException {

        game = new Game(s1, s2, sp);
    }

    @Override
    public synchronized void start() {
        super.start();
        try {
            game.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
