package org.clientserver.classes;

import org.clientserver.classes.impl.TCPNetwork;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Processor implements Runnable{
    private static ExecutorService service = Executors.newFixedThreadPool(6);
    private Packet packet;
    public Processor(Packet packet){
        this.packet = packet;
    }

    public static void process(byte [] message) throws Exception {
        service.submit(new Processor(new Packet(message)));
    }

    public static void shutdown(){
        try{
            service.shutdown();
            while(!service.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            InetAddress inetAddress = InetAddress.getLocalHost();
            new TCPNetwork().sendMessage(Encode.encode(new Message(1, packet.getBMsq().getbUserId(),
                                                                    packet.getBMsq().getMessage()+" -> Ok!")), inetAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
