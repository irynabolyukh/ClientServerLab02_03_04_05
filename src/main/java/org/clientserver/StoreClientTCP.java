package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.entities.MessageGenerator;
import org.clientserver.entities.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreClientTCP {
    private static final int CLIENT_PORT = 2222;
    private static final int RECONNECT_MAX = 3;
    private static final int AMOUNT_OF_CLIENTS = 50;

    //variables to check whether right amount of packets was (not) delivered
    //AMOUNT_OF_CLIENTS = NUMBER_RECEIVED + NUMBER_DEAD
    private static final AtomicInteger NUMBER_RECEIVED = new AtomicInteger(0);
    private static final AtomicInteger NUMBER_DEAD = new AtomicInteger(0);


    public static void main(String[] args) {
        for (int i = 1; i < AMOUNT_OF_CLIENTS+1; i++) {
            final int srcID = i;//userId
            final int pktID = 1;
            final int reconnect_num = 1;

            new Thread(() -> {
                try (final Socket socket = new Socket(InetAddress.getByName(null), CLIENT_PORT)) {
                    clientTCP(socket,srcID,pktID);
                }  catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Reconnecting");
                    reconnect(srcID, pktID, reconnect_num);
                }
            }).start();
        }
    }

    private static void reconnect(int srcID, int pktID, int reconnect_num) {
        try {
            final Socket socket = new Socket(InetAddress.getByName(null), CLIENT_PORT);
            socket.setSoTimeout(3_000*reconnect_num);
            clientTCP(socket, srcID, pktID);
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Reconnecting\tSERVER IS OFFLINE!!!");

            if(reconnect_num == RECONNECT_MAX){
                NUMBER_DEAD.incrementAndGet();
                System.out.println("SERVER IS DEAD:( \t\t NUMBER of DEAD connections: "+ NUMBER_DEAD);
            }
            else{
                int newPktId = pktID + 1;
                int reconnect = reconnect_num + 1;

                reconnect(srcID, newPktId, reconnect);
            }
        }
    }

    private static void clientTCP(Socket socket, int srcID, int pktID) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();

        final byte[] message_from_user = MessageGenerator.generate((byte)srcID, UnsignedLong.valueOf(pktID));
        outputStream.write(message_from_user);
        outputStream.flush();
        Packet packetFromUser = new Packet(message_from_user);

        final byte[] inputMessage = new byte[100];
        final int messageSize = inputStream.read(inputMessage);
        byte[] fullPacket = new byte[messageSize];
        System.arraycopy(inputMessage, 0, fullPacket, 0, messageSize);
        Packet receivedPacket = new Packet(fullPacket);

        if(packetFromUser.getbPktId().equals(receivedPacket.getbPktId()))
            System.out.println("CORRECT response!");
        else
            System.out.println("WRONG response!");

        NUMBER_RECEIVED.incrementAndGet();
        System.out.println("Response from server: " + new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8)
                + "\t for user with ID: " + receivedPacket.getSrcId()
                + "\t for packet with ID: " + receivedPacket.getbPktId()
                + "\t\tNUMBER of RECEIVED:" + NUMBER_RECEIVED);
    }
}
