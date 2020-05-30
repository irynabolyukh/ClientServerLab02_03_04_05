package org.clientserver;

import org.clientserver.entities.MessageGenerator;
import org.clientserver.entities.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class StoreClientUDP {

    public static void main(String[] args) {

        for (int i=0; i<10; i++) {

            new Thread(() -> {
                try (final DatagramSocket serverSocket = new DatagramSocket(0)) {
                    System.out.println(serverSocket.getLocalPort());

                    final byte[] bytes = MessageGenerator.generate();
                    final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), StoreServerUDP.SERVER_PORT);
                    serverSocket.send(packet);

                    final byte[] inputMessage = new byte[100];
                    final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);
                    serverSocket.receive(response);

                    final int realMessageSize = response.getLength();
                    byte[] responseBytes = new byte[realMessageSize];
                    System.arraycopy(response.getData(), 0, responseBytes, 0, responseBytes.length);
                    Packet responsePacket = new Packet(responseBytes);
                    System.out.println("Response from server: " + new String(responsePacket.getBMsq().getMessage()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


        }
    }

}
