package org.clientserver.classes;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;

public class PackResponse { //PACKS ENCODED RESPONSE FOR CLIENT
//    public static byte[] packResponse(Message message){
//        byte[] response = ("Ok!").getBytes();
//        Message answer = new Message(message.getcType(),message.getbUserId(),response);
//        Packet packet = new Packet((byte)1, UnsignedLong.ONE, answer);
//        byte[] encodedPacket = packet.toPacket();
//        return encodedPacket;
//    }
public static byte[] packResponse(Packet packet){
    byte[] response = ("Ok!").getBytes();
    Message answer = new Message(packet.getBMsq().getcType(),packet.getBMsq().getbUserId(),response);
    Packet packetRespond = new Packet((byte)1, packet.getbPktId(), answer);//same packet id for answer
    byte[] encodedPacket = packetRespond.toPacket();
    return encodedPacket;
}
}
