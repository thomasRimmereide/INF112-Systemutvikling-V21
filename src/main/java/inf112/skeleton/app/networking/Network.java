package inf112.skeleton.app.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.jcraft.jogg.Packet;
import inf112.skeleton.app.networking.packets.Packets;

//In this class, common methods for both Server and Client will be implemented.
public class Network {
    static public final int udpPort = 54777, tcpPort = 54555;

    /**
     * Method used to register all needed classes for the server and client.
     *
     * @param endPoint
     */
    static public void register (EndPoint endPoint) {
        Kryo kyro = endPoint.getKryo();

        kyro.register(Packets.CardsPacket.class);
        kyro.register(Packets.MessagePacket.class);
        kyro.register(Packets.NamePacket.class);
        kyro.register(Packets.PlayerNumberPacket.class);
        kyro.register(Packets.ReadySignalPacket.class);
        kyro.register(Packets.RemovePlayerPacket.class);
        kyro.register(Packets.ShutDownRobotPacket.class);
        kyro.register(Packets.StartSignalPacket.class);


        kyro.register(registerName.class);
        kyro.register(updateNames.class);
        kyro.register(PacketMessage.class);

        kyro.register(String[].class);
        kyro.register(int[].class);
    }

    static public class registerName{
        public String name;
    }

    static public class updateNames{
        public String[] names;
    }

    static public class PacketMessage{
        public String message;

    }
}
