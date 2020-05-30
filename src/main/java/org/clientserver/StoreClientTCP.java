package org.clientserver;

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
    private static final AtomicInteger RECONNECT = new AtomicInteger(0);
    private static final AtomicInteger RECONNECT_MAX = new AtomicInteger(2000);
    private static final AtomicInteger NUMBER_RECEIVED = new AtomicInteger(0);
    private static final AtomicInteger NUMBER_DEAD = new AtomicInteger(0);
    private static final AtomicInteger AMOUNT_OF_CLIENTS = new AtomicInteger(1000);



    public static void main(String[] args) {
        for (int i = 0; i < AMOUNT_OF_CLIENTS.get(); i++) {
            new Thread(() -> {
                try (final Socket socket = new Socket(InetAddress.getByName(null), CLIENT_PORT)) {
                    clientTCP(socket);
                }  catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Reconnecting");
                    reconnect();
                }
            }).start();
        }
    }

    private static void reconnect() {
        try {
            final Socket socket = new Socket(InetAddress.getByName(null), CLIENT_PORT);
            socket.setSoTimeout(2_000);
            clientTCP(socket);
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Reconnecting");
            System.out.println("SERVER IS OFFLINE!!!");
            if(RECONNECT.get() == RECONNECT_MAX.get()){
                NUMBER_DEAD.incrementAndGet();
                System.out.println("SERVER IS DEAD:( \t\t NUMBER of DEAD: "+ NUMBER_DEAD);
            }
            else{
                RECONNECT.incrementAndGet();
                reconnect();
            }
        }
    }

    private static void clientTCP(Socket socket) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();

        final byte[] message_from_user = MessageGenerator.generate();
        outputStream.write(message_from_user);
        outputStream.flush();
        Packet packetFromUser = new Packet(message_from_user);

        final byte[] inputMessage = new byte[100];
        final int messageSize = inputStream.read(inputMessage);
        byte[] fullPacket = new byte[messageSize];
        System.arraycopy(inputMessage, 0, fullPacket, 0, messageSize);
        Packet receivedPacket = new Packet(fullPacket);

        if(packetFromUser.getbPktId().equals(receivedPacket.getbPktId()))
            System.out.println("CORRECT packet was sent!");
        else
            System.out.println("WRONG response");
        NUMBER_RECEIVED.incrementAndGet();
        System.out.println("Response: " + new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8) + "\n\t\tNUMBER:" + NUMBER_RECEIVED);
    }
}
