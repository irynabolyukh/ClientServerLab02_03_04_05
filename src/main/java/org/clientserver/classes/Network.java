package org.clientserver.classes;

import java.net.InetAddress;

public interface Network {
    void receiveMessage();
    void sendMessage(byte[] mess, InetAddress inetAddress) throws Exception;
}
