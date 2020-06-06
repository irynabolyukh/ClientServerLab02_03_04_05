package org.clientserver;

import org.clientserver.classes.Processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreServerTCP {

    private static final int SERVER_PORT = 2222;
    private static final AtomicInteger SENT = new AtomicInteger(0);

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

        try (final ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            serverSocket.setSoTimeout(2_000);

            while (isRun.get()) {
                try{
                    Socket socket = serverSocket.accept();

                    final byte[] inputMessage = new byte[100];
                    final InputStream inputStream = socket.getInputStream();
                    final int messageSize = inputStream.read(inputMessage);

                    new Thread(() -> {
                        try {
                            byte[] fullPacket = new byte[messageSize];
                            System.arraycopy(inputMessage, 0, fullPacket, 0, messageSize);
                            final OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(Processor.process(fullPacket));

                            SENT.incrementAndGet();
                            outputStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout");
//                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n\nAMOUNT of SENT: " + SENT.get());
    }
}