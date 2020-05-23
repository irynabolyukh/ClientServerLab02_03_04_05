package org.clientserver.entities;

import com.google.common.primitives.UnsignedLong;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MessageGenerator { //GENERATE MESSAGE FROM CLIENT
    public static byte[] generate() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Random random = new Random();
        int command = random.nextInt(Message.cTypes.values().length);
        String commandMsg = (Message.cTypes.values()[command]).toString();
        Message testMessage = new Message(command ,1, commandMsg.getBytes());
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, testMessage);
        byte[] packetToBytes = packet.toPacket();
        return packetToBytes;//encoded packet
    }
}

