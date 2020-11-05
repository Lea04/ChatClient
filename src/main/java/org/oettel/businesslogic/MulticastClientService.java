package org.oettel.businesslogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.oettel.Main;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.ClientMessageType;
import org.oettel.model.message.InetMessage;
import org.oettel.model.message.Message;
import org.oettel.model.vectorclock.VectorClockEntry;
import org.oettel.model.vectorclock.VectorClockSingleton;
import org.oettel.sender.UnicastSender;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MulticastClientService {


    private OrderingReliabilityService orderingReliabilityService;

    public void receiveChatMessage(ClientMessage chatMessage) throws IOException {
        VectorClockSingleton.getInstance().updateVectorclock();

        //if((ClientConfigurationSingleton.getInstance().getSequenceNumber()+1)!= chatMessage.getQueueIdCounter()){
            this.nack(chatMessage);
        //}
        //else{
            chatMessage.getVectorClockEntries().forEach(externalVectorClock -> {
                VectorClockSingleton.getInstance().updateExternalVectorclockEntries(externalVectorClock);
            });
            String content = chatMessage.getContent();
            ClientConfigurationSingleton.getInstance().setLastReceivedChattMessage(content + "\n");
            Main.setRoot("/chat");

            ClientConfigurationSingleton.getInstance().increaseSequenceNumber();

        //}
    }


    public void handleLeaderAnnouncement(InetAddress address) {
        ClientConfigurationSingleton.getInstance().setLeader(address);
    }

    public void setVectorClock(ClientMessage message) {
        if (VectorClockSingleton.getInstance().getVectorClockEntryList().size() == 0) {
            VectorClockSingleton.getInstance().setVectorClockEntryList(message.getVectorClockEntries());
        } else {
            List<VectorClockEntry> newList = new ArrayList<>();
            VectorClockSingleton.getInstance().getVectorClockEntryList().forEach(vectorClockInternalEntry -> {
                if (message.getVectorClockEntries().stream().anyMatch(vectorClockExternalEntry -> vectorClockInternalEntry.getIpAdress().equals(vectorClockExternalEntry.getIpAdress()))) {
                    newList.add(vectorClockInternalEntry);
                }
            });
            message.getVectorClockEntries().forEach(externalVectorClockEntry -> {
                boolean mustEntryBeSafed = true;
                for (VectorClockEntry ingernalVectorClock : VectorClockSingleton.getInstance().getVectorClockEntryList()) {
                    if (externalVectorClockEntry.getIpAdress().toString().contains(ingernalVectorClock.getIpAdress().toString())) {
                        mustEntryBeSafed = false;
                    }
                }
                if (mustEntryBeSafed) {
                    newList.add(externalVectorClockEntry);
                }
            });
            VectorClockSingleton.getInstance().setVectorClockEntryList(newList);
        }
    }

    ObjectMapper mapper = new ObjectMapper();

    public void nack(ClientMessage chatMessage) throws IOException {

        int count = ClientConfigurationSingleton.getInstance().getSequenceNumber();

        if (count < chatMessage.getQueueIdCounter()){
            for (int counter = (count + 1); counter <= chatMessage.getQueueIdCounter(); counter++) {
                Message nackMessage = new ClientMessage(ClientMessageType.NACK, "nack", counter);
                UnicastSender unicastSender = new UnicastSender(ClientConfigurationSingleton.getInstance().getLeader());
                String messageJson = mapper.writeValueAsString(nackMessage);
                unicastSender.sendMessage(messageJson);
                unicastSender.close();
            }
        }
    }

}
