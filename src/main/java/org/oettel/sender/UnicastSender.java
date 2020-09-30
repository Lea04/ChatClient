package org.oettel.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.configuration.ClientConfigurationSingleton;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class UnicastSender{
    private Socket socket;
    private ObjectMapper mapper;

    public UnicastSender() {
    }

    public UnicastSender(final InetAddress address) throws IOException {
        this.socket = new Socket(address, ClientConfigurationSingleton.getInstance().getServerPort());
        this.mapper = new ObjectMapper();
    }

    public void sendMessage(String message) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(message);
    }

    public void close() throws IOException {
        socket.close();
    }


//    @Override
//    public void run() {
//        System.out.println("#### Send initial heartbeat ####");
//        ServerConfigurationSingleton.getInstance().getReplicaServer().forEach(inetAddress -> {
//            Heartbeat heartbeat = new Heartbeat(inetAddress, false);
//            HeartbeatListSingleton.getInstance().addReplicaToHeartbeatList(heartbeat);
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                Message heartbeatMessage = new ServerMessage(ServerMessageType.HEARTBEAT, "heartbeat");
//                String receivedJson = mapper.writeValueAsString(heartbeatMessage);
//                this.rebuildSocket(inetAddress);
//                this.sendMessage(receivedJson);
//                this.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        System.out.println("#### Initial heartbeat was send####");
//    }

    private void rebuildSocket(InetAddress inetAddress) throws IOException {
        this.socket = new Socket(inetAddress, ClientConfigurationSingleton.getInstance().getServerPort());
    }
}

