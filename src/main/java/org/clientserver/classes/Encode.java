package org.clientserver.classes;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;

public class Encode {
    public static byte[] encode(Message message){
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, message);
        byte[] packetToBytes = packet.toPacket();
        return packetToBytes;
    }

}
