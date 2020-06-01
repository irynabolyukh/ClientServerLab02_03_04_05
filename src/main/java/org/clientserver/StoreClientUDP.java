package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.entities.MessageGenerator;
import org.clientserver.entities.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoreClientUDP {

    private static final int CLIENT_PORT = 1234;
    private static final int AMOUNT_OF_CLIENTS = 4;
    private static final int AMOUNT_OF_PACKETS = 5;
    private static final int AMOUNT_OF_RETRIES = 3;


    public static void main(String[] args) {

        //створення 4-ьох клієнтів
        for (int i = 0; i < AMOUNT_OF_CLIENTS; i++) {

            final int srcID = i * 10 + i;

            new Thread(() -> {
                try (final DatagramSocket serverSocket = new DatagramSocket(0)) {

                    serverSocket.setSoTimeout(3_000);

                    Map<Integer, byte[]> historySent = new HashMap<>();
                    Map<Integer, byte[]> historyReceived = new HashMap<>();

                    //SENDING PACKETS
                    for (int j = 0; j < AMOUNT_OF_PACKETS; j++) {

                        final byte[] bytes = MessageGenerator.generate((byte) srcID, UnsignedLong.valueOf(j));
                        final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), CLIENT_PORT);

                        //емуляція втрати 3-го пакета
                        if (j != 3) {
                            serverSocket.send(packet);
                        }

                        historySent.put(j, bytes);

                        final byte[] inputMessage = new byte[100];
                        final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);

                        try {
                            serverSocket.receive(response);

                            final int realMessageSize = response.getLength();
                            byte[] responseBytes = new byte[realMessageSize];
                            System.arraycopy(response.getData(), 0, responseBytes, 0, responseBytes.length);
                            Packet responsePacket = new Packet(responseBytes);

                            historyReceived.put(responsePacket.getbPktId().intValue(), responseBytes);

                            System.out.println("Response for " + responsePacket.getSrcId() + " : " + new String(responsePacket.getBMsq().getMessage()));

                        } catch (SocketTimeoutException e) {
                            System.out.println("Socket timeout");
                        }
                    }

                    //TRYING TO RESEND LOST PACKETS
                    for (int k = 0; k < AMOUNT_OF_RETRIES; k++) {
                        if (!historySent.isEmpty()) {

                            Set<Integer> keyReceivedSet = historyReceived.keySet();

                            for (Integer key : keyReceivedSet) {
                                historySent.remove(key);  //clearing the historySent from packets that weren't lost
                            }
                            historyReceived.clear();

                            Set<Integer> keySentSet = historySent.keySet();

                            for (Integer key : keySentSet) {
                                byte[] bytes = historySent.get(key);

                                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(null), CLIENT_PORT);
                                serverSocket.send(packet);

                                historySent.put(key, bytes);

                                final byte[] inputMessage = new byte[100];
                                final DatagramPacket response = new DatagramPacket(inputMessage, inputMessage.length);

                                try {
                                    serverSocket.receive(response);

                                    final int realMessageSize = response.getLength();
                                    byte[] responseBytes = new byte[realMessageSize];
                                    System.arraycopy(response.getData(), 0, responseBytes, 0, responseBytes.length);
                                    Packet responsePacket = new Packet(responseBytes);

                                    historyReceived.put(responsePacket.getbPktId().intValue(), responseBytes);

                                    System.out.println("Response for " + responsePacket.getSrcId() + " : " + new String(responsePacket.getBMsq().getMessage()));

                                } catch (SocketTimeoutException e) {
                                    System.out.println("Socket timeout");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
