package org.clientserver.classes;
import org.clientserver.entities.Packet;
import org.clientserver.entities.Message;

import java.nio.charset.StandardCharsets;


public class Processor{
    public static byte[] process(byte[] packetFromUser) {//processes packet from USER, gets encoded packet
        Packet packet = new Packet(packetFromUser);//decoding packet from USER
        System.out.println("Message from client: " + new String(packet.getBMsq().getMessage(), StandardCharsets.UTF_8));
        Message answerMessage = new Message(1, 1, ("Server - OK!").getBytes(StandardCharsets.UTF_8));
        Packet answerPacket = new Packet(packet.getUserId(), packet.getbPktId(), answerMessage);
        return answerPacket.toPacket(); //returns encoded respond for USER
    }
}
