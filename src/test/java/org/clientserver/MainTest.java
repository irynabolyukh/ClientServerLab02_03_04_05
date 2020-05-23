package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.apache.commons.codec.binary.Hex;
import org.clientserver.classes.DeEncriptor;
import org.clientserver.classes.Processor;
import org.clientserver.classes.impl.TCPNetwork;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest{

    @Test
    void checkWhether_InvalidMagicByte() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Packet(Hex.decodeHex("15"))
        );
    }

    @Test
    void check_DeEncriptor() throws NoSuchAlgorithmException, NoSuchPaddingException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        byte[] origMessageToBytes = originalMessage.toPacketPart();
        byte[] encode_decode = DeEncriptor.decode(DeEncriptor.encode(originalMessage.getWhole()));
        assert(Arrays.equals(origMessageToBytes, encode_decode));
    }

    @Test
    void check_DeEncriptor_forPacket() throws Exception {
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        byte[] origMessageToBytes = originalMessage.toPacketPart();
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, originalMessage);
        byte[] packetToBytes = packet.toPacket();//encoding packet
        Packet decoded_packet = new Packet(packetToBytes);
        Message decoded_message = decoded_packet.getBMsq();
        byte[] decoded_messageToBytes = decoded_message.toPacketPart();
        assert(Arrays.equals(origMessageToBytes, decoded_messageToBytes));
    }

    @Test
    void check_DeEncriptor_forPacket2() throws Exception {
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, originalMessage);
        byte[] packBytes = packet.toPacket();
        Packet packet1 = new Packet(packBytes);
        byte [] packBytes1 = packet1.toPacket();
        assert(Arrays.equals(packBytes, packBytes1));
    }

    @Test
    void checkWhether_InvalidCrc() {

    }

    @Test
    void checkWhether_SuccessfulFinished() {
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        for(int i = 0; i < 24; i++)
            executorService.submit(()->{
                TCPNetwork tcpNetwork = new TCPNetwork();
                tcpNetwork.receiveMessage();
            });
        try{
            executorService.shutdown();
            while(!executorService.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Processor.shutdown();
        System.out.println("End of main");
    }
}