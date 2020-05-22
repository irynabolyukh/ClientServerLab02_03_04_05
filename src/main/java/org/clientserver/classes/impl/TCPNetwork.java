package org.clientserver.classes.impl;

import org.clientserver.classes.Cipher;
import org.clientserver.classes.Processor;
import org.clientserver.entities.MessageGenerator;
import org.clientserver.classes.Network;
import org.clientserver.entities.Packet;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class TCPNetwork implements Network {
    @Override
    public void receiveMessage(){
        try {
            Processor.process(MessageGenerator.generate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(byte[] mess, InetAddress inetAddress){
        try {
            Packet packet = new Packet(mess);//package with response
            System.out.println("\n~~~~~~~~~~~~~~~~~\n#" + Thread.currentThread().getId()+" Send respond to user: "
                                 + packet.getBMsq().getMessage() + "\nTo: "+inetAddress.toString()
                                    + "\nTime: " + LocalDateTime.now() + "\n~~~~~~~~~~~~~~~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
