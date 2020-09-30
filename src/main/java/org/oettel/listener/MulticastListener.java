package org.oettel.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.businesslogic.MulticastClientService;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.InetMessage;
import org.oettel.model.message.Message;

import java.io.IOException;
import java.net.*;

import static org.oettel.configuration.Constants.MULTICAST_ADDRESS;
import static org.oettel.configuration.Constants.MULTICAST_PORT;

public class MulticastListener implements Runnable {
    private MulticastSocket multicastSocket;
    private boolean running;
    private byte[] buf = new byte[512];
    private InetAddress addressGroup;
    byte[] receivedJson = new byte[512];
    ObjectMapper mapper = new ObjectMapper();
    private MulticastClientService multicastClientService;

    public MulticastListener() throws IOException {
        this.addressGroup = InetAddress.getByName(MULTICAST_ADDRESS);
        this.multicastSocket = new MulticastSocket(MULTICAST_PORT);
        this.multicastSocket.joinGroup(new InetSocketAddress(addressGroup, ClientConfigurationSingleton.getInstance().getServerPort()), NetworkInterface.getByInetAddress(addressGroup));
        this.multicastClientService = new MulticastClientService();
    }

    @Override
    public void run() {
        System.out.println("Multicastlistener started...");
        running = true;
        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            receivedJson = packet.getData();
            Message message = null;
            try {
                message = mapper.readValue(receivedJson, Message.class);
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server Received Multicast from: " + packet.getAddress() + " :: Mesage Type: " + message.getMessageType() + " :: Mesage conntnet: " + message.getContent());
            switch (message.getMessageType()) {
                case CLIENT_MESSAGE:
                    try {
                        evaluateClientMessages(message, packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("do default");
                    break;

            }
        }
        multicastSocket.close();
        System.out.println("Multicastlistener stopped...");
    }

    private void evaluateClientMessages(Message message, DatagramPacket packet) throws IOException {
        ClientMessage clientMessage = (ClientMessage) message;
        switch (clientMessage.getClientMessageType()) {
            case CHAT_MESSAGE:
                multicastClientService.receiveChatMessage(clientMessage);
                break;
            case LEADER_ANNOUNCEMENT:
                multicastClientService.handleLeaderAnnouncement( packet.getAddress());
                break;
            case VECTOR_BROADCAST_RESPONSE:
                multicastClientService.setVectorClock(clientMessage);
                break;
        }
    }
}


