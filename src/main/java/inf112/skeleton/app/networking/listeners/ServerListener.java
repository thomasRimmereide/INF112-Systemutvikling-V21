package inf112.skeleton.app.networking.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.networking.packets.Packets;
import inf112.skeleton.app.player.Player;
import inf112.skeleton.app.shared.Action;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class ServerListener extends Listener {
    private final Server server;
    private final boolean[] allPlayersReady;
    private final HashMap<Integer, ArrayList<HashMap<Integer, Action>>> cardsReceived;
    private int numberOfPlayers = 0;
    private final String[] playerNames;
    public final boolean[] ShutdownPlayer;
    public final int MAX_PLAYERS = 3;
    public HashMap<Integer, ArrayList<Float>> playerInfoGlobal = new HashMap<>();
    private int checkIfAllClientsAreReady=0;



    /**
     *
     * @param server Kryonet server that is being used
     */
    public ServerListener(Server server) {
        this.server = server;
        playerNames = new String[MAX_PLAYERS];
        cardsReceived = new HashMap<>();
        allPlayersReady = new boolean[MAX_PLAYERS];
        ShutdownPlayer = new boolean[MAX_PLAYERS];


        for (int i = 0; i < MAX_PLAYERS; i++) {
            ShutdownPlayer[i] = false;
        }
    }


    public void connected(Connection connection) {
        System.out.println("Player " + (numberOfPlayers) + " has connected to the server");
        numberOfPlayers++;

        Packets.PlayerNumberPacket playerPacket = new Packets.PlayerNumberPacket();
        playerPacket.numberOfPlayersConnected = numberOfPlayers;
        server.sendToAllTCP(playerPacket);

        Packets.PlayerIdPacket playerIdPacket = new Packets.PlayerIdPacket();
        playerIdPacket.playerNumber = numberOfPlayers;
        server.sendToAllTCP(playerIdPacket);

        Packets.SendMapNameToPlayer sendMapNameToPlayer = new Packets.SendMapNameToPlayer();
        server.sendToAllTCP(sendMapNameToPlayer);


        if (numberOfPlayers >= 2) {

            Packets.playerInfo updatedPlayerInfo = new Packets.playerInfo();
            updatedPlayerInfo.playerInfo = playerInfoGlobal;
            server.sendToAllTCP(updatedPlayerInfo);


            this.startGameSession();

        }
    }


    /**
     * If a player disconnects from the server this method will get
     * called and it will print which player disconnected.
     * @param connection connection
     */
    public void disconnected(Connection connection) {
        System.out.println("Player: (" + numberOfPlayers + ") has been disconnected");
        numberOfPlayers--;
        playerNames[connection.getID()] = null;
        Packets.PlayerNumberPacket numberOfPlayers = new Packets.PlayerNumberPacket();
        numberOfPlayers.numberOfPlayersConnected = this.numberOfPlayers;
        server.sendToAllTCP(numberOfPlayers);
    }


    public void startGameSession() {
        System.out.println("Instructing all clients to start the game");
        Packets.StartGamePackage p = new Packets.StartGamePackage();
        server.sendToAllTCP(p);
    }

    /** When something is sent to the server this method gets called and sorts
     *  out what type of message it is before it sends it all clients
     *
     * @param connection connection
     * @param object object
     */
    public void received(Connection connection, Object object) {

        if (object instanceof Packets.CardsPacket) {


        } else if (object instanceof Packets.StartSignalPacket) {
            Packets.StartSignalPacket startSignalPacket = (Packets.StartSignalPacket) object;
            server.sendToAllTCP(startSignalPacket);

        } else if (object instanceof Packets.StartGamePackage) {
            Packets.StartGamePackage ready = (Packets.StartGamePackage) object;
            allPlayersReady[connection.getID()] = ready.signal;
            ready.allReady = allPlayersReady;
            server.sendToAllTCP(ready);

        } else if (object instanceof Packets.ShutDownRobotPacket) {
            Packets.ShutDownRobotPacket robotShutdown = (Packets.ShutDownRobotPacket) object;
            ShutdownPlayer[connection.getID()] = true;
            robotShutdown.playersShutdown = ShutdownPlayer;
            server.sendToAllTCP(robotShutdown);
            ShutdownPlayer[connection.getID()] = false;

        } else if (object instanceof Packets.RemovePlayerPacket) {
            numberOfPlayers--;
            Packets.PlayerNumberPacket numberOfPlayersConnected = new Packets.PlayerNumberPacket();
            numberOfPlayersConnected.numberOfPlayersConnected = numberOfPlayers;
            server.sendToAllTCP(numberOfPlayersConnected);

        } else if (object instanceof Packets.playerInfo) {
            System.out.println("Motatt!");
            Packets.playerInfo playerInfo = (Packets.playerInfo) object;
            System.out.println(playerInfo.playerInfo);
            Set<Integer> findPlayerIdKey = playerInfo.playerInfo.keySet();

            for (Integer key: findPlayerIdKey) {
                Integer foundKey = key;
                playerInfoGlobal.put(foundKey, playerInfo.playerInfo.get(foundKey));
                System.out.println(foundKey);
                System.out.println(playerInfoGlobal);
            }
            Packets.playerInfo sendPlayerInfo = new Packets.playerInfo();
            sendPlayerInfo.playerInfo = playerInfoGlobal;
            server.sendToAllTCP(sendPlayerInfo);


        } else if (object instanceof Packets.SendAction) {
            checkIfAllClientsAreReady++;
            Packets.SendAction receivedAction = (Packets.SendAction) object;

            Set<Integer> findPlayerIdKey = receivedAction.actionList.keySet();

            for (Integer key: findPlayerIdKey) {
                Integer foundKey = key;
                cardsReceived.put(foundKey, receivedAction.actionList.get(foundKey));
                System.out.println(foundKey);
                System.out.println(cardsReceived);
            }
            if(checkIfAllClientsAreReady == numberOfPlayers) {
                receivedAction.actionList = cardsReceived;
                server.sendToAllTCP(receivedAction);

            }
        }



    }
}
