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

        while (this.isPartida()) {

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
                        int posShip;

                        if(tirades != 0) {
                            tirades--;
                        }else{
                            System.exit(1);
                        }
                        int[] dice = comUtils.read_dice();
                        for(int i = 0; i<dice.length;i++){
                            if(dice[i] == 6){
                                ship = true;
                                posShip = i;
                            }
                        }
                        if(ship){
                            int[] take =  comUtils.read_take();
                            for(int j = 0; j< take.length;j++){
                                if(take[j] ==  dice[i])
                                    this.setEstat(EstatPartida.SHIP);
                            }
                        }else{
                            this.setEstat(EstatPartida.START);
                        }
                    }

                    break;

                case SHIP:

                    break;

                case CAPTAIN:

                    break;

                case CREW:

                    break;

                case END:

                    break;

                default:

                    break;
            }
        }
    }

}
