package org.oettel.model.message;

import java.net.InetAddress;

public class InetMessage extends ClientMessage{
    private InetAddress inet;

    public InetMessage() {
        super();
    }

    public InetMessage(ClientMessageType mt, String content) {
        super(mt, content);
    }

    public InetMessage(ClientMessageType mt, String content, InetAddress inet) {
        super(mt, content);
        this.inet = inet;
    }

    public InetAddress getInet() {
        return inet;
    }

    public void setInet(InetAddress inet) {
        this.inet = inet;
    }
}
