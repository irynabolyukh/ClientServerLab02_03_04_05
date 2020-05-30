package org.clientserver;

import org.clientserver.classes.Processor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreServerUDP {

    public static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        final AtomicBoolean isRun = new AtomicBoolean(true);

        new Thread(() ->  {
            try{
                Thread.sleep(10_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isRun.set(false);
        }).start();

        try(final DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)){
            serverSocket.setSoTimeout(2_000);

            while (isRun.get()){
                try{
                    final byte[] inputMessage = new byte[100];
                    final DatagramPacket packet = new DatagramPacket(inputMessage, inputMessage.length);
                    serverSocket.receive(packet);

                    new Thread(() -> {
                        try{
                            final int realMessageSize = packet.getLength();
                            byte[] packetBytes = new byte[realMessageSize];
                            System.arraycopy(packet.getData(), 0, packetBytes, 0, packetBytes.length);

                            final byte[] bytes = Processor.process(packetBytes);
                            final DatagramPacket response = new DatagramPacket(bytes, bytes.length, packet.getAddress(), packet.getPort());
                            serverSocket.send(response);
                            //System.out.println("Sent response");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout");
//                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}