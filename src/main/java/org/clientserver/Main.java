package org.clientserver;
import org.clientserver.classes.Processor;
import org.clientserver.classes.impl.TCPNetwork;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
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
        //Processor.shutdown();
        System.out.println("End of main");
    }
}