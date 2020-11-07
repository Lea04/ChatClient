package org.oettel.configuration;

import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.Message;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientConfigurationSingleton {
    private static ClientConfigurationSingleton instance;
    private int serverPort;
    private InetAddress serverAddress;
    private InetAddress leader;
    private String clientName;
    private String lastReceivedChattMessage = "";
    private int sequenceNumber;
    List<ClientMessage> holdbackQueue = new ArrayList<>();
    List<ClientMessage> deliveryQueue = new ArrayList<>();

    private ClientConfigurationSingleton() {
    }

    public static ClientConfigurationSingleton getInstance() {
        if (ClientConfigurationSingleton.instance == null) {
            ClientConfigurationSingleton.instance = new ClientConfigurationSingleton();
        }
        return ClientConfigurationSingleton.instance;
    }

    public InetAddress getLeader() {
        return leader;
    }

    public void setLeader(InetAddress leader) {
        this.leader = leader;
    }

    public void setServerPort(final int serverPort) {
        this.serverPort = serverPort;
        System.out.println("Configure Server Port on Client: " + this.serverPort);
    }

    public void setServerAddress(final InetAddress serverAddress) {
        this.serverAddress = serverAddress;
        System.out.println("Configure Server Address on Client: " + this.serverAddress);
    }

    public int getServerPort() {
        return serverPort;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public void increaseSequenceNumber() {
        this.sequenceNumber = this.sequenceNumber+1;
    }

    public String getLastReceivedChattMessage() {
        return lastReceivedChattMessage;
    }

    public void setLastReceivedChattMessage(String lastReceivedChattMessage) {
        System.out.println(lastReceivedChattMessage);
        this.lastReceivedChattMessage = this.lastReceivedChattMessage + lastReceivedChattMessage;
    }

    public void addMessageToHoldbackQueue(ClientMessage message) {
        this.holdbackQueue.add(message);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public List<ClientMessage> getHoldbackQueue() {
        return holdbackQueue;
    }

    public List<ClientMessage> getDeliveryQueue() {
        return deliveryQueue;
    }

    public void setDeliveryQueue(List<ClientMessage> deliveryQueue) {
        this.deliveryQueue = deliveryQueue;
    }
}

