package org.clientserver.classes;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PackRespond { //PACKS ENCODED RESPONSE FOR CLIENT
    public static byte[] packRespond(Message message){
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, message);
        byte[] pak = packet.toPacket();
        return pak;
    }
}
