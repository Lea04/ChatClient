package org.oettel.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.businesslogic.UnicastClientService;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class UnicastListener implements Runnable {
    private ServerSocket serverSocket;
    private boolean running;
    private ObjectMapper mapper;
    private Message message;
    private UnicastClientService unicastServerService;

    public UnicastListener(UnicastClientService unicastServerService) throws IOException {
        this.unicastServerService = unicastServerService;
        this.serverSocket = new ServerSocket(ClientConfigurationSingleton.getInstance().getServerPort());
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        System.out.println("Messagetlistener started...");
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                Scanner inputScanner = new Scanner(inputStream);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String responseFromScanner = inputScanner.nextLine();
                message = mapper.readValue(responseFromScanner, Message.class);
                switch (message.getMessageType()) {
                    case CLIENT_MESSAGE:
                        evaluateClientMessages(message, socket.getInetAddress());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("MessageListener stopped...");
    }

    private void evaluateClientMessages(Message message, InetAddress inetAddress) throws IOException {
        ClientMessage clientMessage = (ClientMessage) message;
        switch (clientMessage.getClientMessageType()) {
            case CLIENT_BROADCAST_RESPONSE:
                unicastServerService.clientBroadcastRespondsHandler(inetAddress);
                break;
            case CLIENT_HEARTBEAT:
                unicastServerService.respondToClientHeartbeat(inetAddress);
                break;
            case NACK:
                unicastServerService.receiveNackMessage(clientMessage);
                break;
            default:
                break;
        }
    }
}

