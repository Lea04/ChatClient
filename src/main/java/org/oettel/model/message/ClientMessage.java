package org.oettel.model.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.oettel.model.vectorclock.VectorClockEntry;

import java.util.List;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)

@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = InetMessage.class, name = "InetMessage")
        })

public class ClientMessage extends Message {
    private ClientMessageType clientMessageType;
    private List<VectorClockEntry> vectorClockEntries;

    public ClientMessage() {
        super();
    }

    public ClientMessage(ClientMessageType clientMessageType, String content) {
        super(MessageType.CLIENT_MESSAGE, content);
        this.clientMessageType = clientMessageType;
    }

    public ClientMessage(ClientMessageType clientMessageType, String content, List<VectorClockEntry> vectorClockEntryList) {
        super(MessageType.CLIENT_MESSAGE, content);
        this.clientMessageType = clientMessageType;
        this.vectorClockEntries = vectorClockEntryList;
    }

    public ClientMessageType getClientMessageType() {
        return clientMessageType;
    }

    public List<VectorClockEntry> getVectorClockEntries() {
        return vectorClockEntries;
    }
}