package org.clientserver.entities;

import com.google.common.primitives.UnsignedLong;
import java.util.Random;

public class MessageGenerator { //GENERATE MESSAGE FROM CLIENT
    public static byte[] generate() {
        Random random = new Random();
        int command = random.nextInt(Message.cTypes.values().length);
        String commandMsg = (Message.cTypes.values()[command]).toString();
        Message testMessage = new Message(command ,1, commandMsg.getBytes());
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, testMessage);
        byte[] packetToBytes = packet.toPacket();
        return packetToBytes;//encoded packet
    }
}