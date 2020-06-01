package org.clientserver.entities;

import com.google.common.primitives.UnsignedLong;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MessageGenerator { //GENERATE MESSAGE FROM CLIENT
        public static byte[] generate(byte srcID, UnsignedLong pktId) {
        Random random = new Random();
        int command = random.nextInt(Message.cTypes.values().length);
        String commandMsg = (Message.cTypes.values()[command]).toString();

        //creates message with random command
        Message testMessage = new Message(command ,1, commandMsg.getBytes(StandardCharsets.UTF_8));
        Packet packet = new Packet(srcID, pktId, testMessage);

        byte[] packetToBytes = packet.toPacket();//encodes packet
        return packetToBytes;
    }
}