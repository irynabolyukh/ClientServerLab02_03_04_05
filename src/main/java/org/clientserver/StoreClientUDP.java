package org.clientserver;

import org.clientserver.entities.MessageGenerator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class StoreClientUDP {

    private static final int CLIENT_PORT = 1234;

    public static void main(String[] args) {

        for (int i=0; i<10; i++) {

            new Thread(() -> {
                try (final DatagramSocket serverSocket = new DatagramSocket(0)) {
                    System.out.println(serverSocket.getLocalPort());

//                    final String message = "message from client";
//                    final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                    final byte[] bytes = MessageGenerator.generate();
                    final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), CLIENT_PORT);
                    serverSocket.send(packet);

                    final byte[] inputMessage = new byte[100];
                    final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);
                    serverSocket.receive(response);

                    final int realMessageSize = packet.getLength();
                    System.out.println("Response from server: " + new String(inputMessage, 0, realMessageSize, StandardCharsets.UTF_8));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


        }
    }

}
