package org.oettel.model.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)

@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = ClientMessage.class, name = "ClientMessage")
        })

abstract public class Message {
    private MessageType messageType;
    private String content;

    public Message() {
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {return content;}

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(MessageType messageType, String content) {
        this.messageType = messageType;
        this.content = content;
    }
}
