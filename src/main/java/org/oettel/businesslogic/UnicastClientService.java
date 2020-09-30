package org.oettel.businesslogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.Main;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.ClientMessageType;
import org.oettel.model.message.Message;
import org.oettel.sender.UnicastSender;

import java.io.IOException;
import java.net.InetAddress;

public class UnicastClientService {

    public void respondToClientHeartbeat(InetAddress inetAddress) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Message heartbeatResponseMessage = new ClientMessage(ClientMessageType.CLIENT_HEARTBEAT_RESPONSE, "heartbeat_response");
        String receivedJson = mapper.writeValueAsString(heartbeatResponseMessage);
        UnicastSender unicastSender = new UnicastSender(inetAddress);
        unicastSender.sendMessage(receivedJson);
    }

    public void clientBroadcastRespondsHandler(InetAddress inetAddress) throws IOException {
        ClientConfigurationSingleton.getInstance().setLeader(inetAddress);
        Main.setRoot("/chat");
    }


}
