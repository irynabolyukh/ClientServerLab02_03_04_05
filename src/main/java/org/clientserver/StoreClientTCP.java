package org.clientserver;

import org.clientserver.entities.Message;
import org.clientserver.entities.MessageGenerator;
import org.clientserver.entities.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StoreClientTCP {

    public static final int CLIENT_PORT = 2222;

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try (final Socket socket = new Socket(InetAddress.getByName(null), CLIENT_PORT)) {

                    final InputStream inputStream = socket.getInputStream();
                    final OutputStream outputStream = socket.getOutputStream();

                    final byte[] message_from_user = MessageGenerator.generate();
                    outputStream.write(message_from_user);

                    final byte[] inputMessage = new byte[100];
                    final int messageSize = inputStream.read(inputMessage);
                    byte fullPacket[] = new byte[messageSize];
                    System.arraycopy(inputMessage, 0, fullPacket, 0, messageSize);
                    Packet receivedPacket = new Packet(fullPacket);
                    System.out.println(Arrays.toString(fullPacket));
                    System.out.println("Response: " + new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8));

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

}
