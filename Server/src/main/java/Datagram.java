import utils.ComUtils;
import java.io.*;

/**
 * <h1>Datagram class</h1>
 * Communication functions between Client and Server.
 *
 * @author  Abdel
 * @version 1.0
 * @since   24-02-2020
 */
public class Datagram {

    private ComUtils utils;

    /**
     * Show to the client the number of gems he has.
     * Format:  CASH<SP><COINS>
     *
     * @param client's COINS
     * @throws IOException excep
     */
    public void cash(int coins) throws IOException {

        String c = Command.CASH.name();
        int[]param = new int[1];

        param[0] = coins;

        utils.write_datagram(c, param);
    }

    /**
     * Amount of money in-game.
     * Format: LOOT<SP><COINS>
     *
     * @param amount of money
     * @throws IOException
     */
    public void loot(int coins) throws IOException {

        String c = Command.LOOT.name();
        int[]param = new int[1];

        param[0] = coins;

        utils.write_datagram(c, param);
    }

    /**
     * Indicate the players' turn.
     * 0->client | 1->server
     * Format: PLAY<SP><'0'|'1'>
     *
     * @param player turn
     * @throws IOException
     */
    public void play(int turn) throws IOException {

        String c = Command.PLAY.name();
        int[]param = new int[1];

        param[0] = turn;

        utils.write_datagram(c, param);
    }

    /**
     * Indicates all dice values.
     * Format: TAKE<SP><ID>5(<SP><VALUE>)
     *
     * @param id client's ID
     * @param dice values
     */
    public void dice(int id, int[] dice) throws IOException {

        String c = Command.DICE.name();
        int[] param = new int[dice.length+1];

        param[0] = id;

        for(int i = 1; i < dice.length; i++){

            param[i] = dice[i - 1];
        }

        utils.write_datagram(c, param);
    }

    /**
     * Client's TAKE command.
     * Format: TAKE<SP><ID><LEN>*5(<SP><POS>)
     *
     * @param id client's ID
     * @param sel client's dice selection
     */
    public void take(int id, int[] sel) throws IOException {

        String c = Command.TAKE.name();
        int[] param = new int[sel.length + 1];

        param[0] = id;

        for(int i = 1; i < param.length; i++){

            param[i] = sel[i - 1];
        }

        utils.write_datagram(c, param);
    }

    /**
     * Client's PASS command.
     * Format: PASS<SP><ID>
     *
     * @param id client's id
     * @throws IOException excep
     */
    public void pass(int id) throws IOException {

        String c = Command.PASS.name();
        int[] param = new int[1];

        param[0] = id;

        utils.write_datagram(c, param);

    }

    /**
     * Show the score obtained after every player turn.
     * Format: PNTS<SP><ID><SP><POINTS>
     *
     * @param id client's ID
     * @param client's POINTS
     * @throws IOException
     */
    public void pnts(int id, int points) throws IOException {

        String c = Command.PNTS.name();
        int[] param = new int[2];

        param[0] = id;
        param[1] = points;

        utils.write_datagram(c, param);
    }

    /**
     * Show witch player's won.
     * '0'->client | '1'->server | '2'->tie
     * Format: PLAY<SP><'0'|'1'|'2'>
     *
     * @param player who won
     * @throws IOException
     */
    public void wins(int won) throws IOException {

        String c = Command.WINS.name();
        int[]param = new int[1];

        param[0] = won;

        utils.write_datagram(c, param);
    }

    private enum Command {
        CASH,
        LOOT,
        PLAY,
        DICE,
        TAKE,
        PASS,
        PNTS,
        WINS
    }
}