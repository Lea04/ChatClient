package org.oettel.businesslogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.ClientMessageType;
import org.oettel.model.message.Message;
import org.oettel.model.vectorclock.VectorClockSingleton;
import org.oettel.sender.UnicastSender;

import java.io.IOException;

public class OrderingReliabilityService {

    ObjectMapper mapper = new ObjectMapper();

    public void nack(ClientMessage chatMessage) throws IOException {

        int count = ClientConfigurationSingleton.getInstance().getSequenceNumber();

        for (int counter = (count + 1); counter <= chatMessage.getQueueIdCounter(); counter++) {
            Message nackMessage = new ClientMessage(ClientMessageType.NACK, "nack", counter);
            UnicastSender unicastSender = new UnicastSender();
            String messageJson = mapper.writeValueAsString(nackMessage);
            unicastSender.sendMessage(messageJson);
            unicastSender.close();
        }

    }
}
