package org.clientserver.entities;

import lombok.Data;
import org.clientserver.classes.DeEncriptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
public class Message {
    public byte[] getMessage() { return message; }
    public Integer getcType() { return cType; }
    public Integer getbUserId() {
        return bUserId;
    }
    public byte[] getWhole() {
        return whole;
    }
    public int getMessageBytesLength() {
        return whole.length;
    }

    public enum cTypes {
        INSERT_PRODUCT,
        DELETE_PRODUCT,
        UPDATE_PRODUCT,
        GET_PRODUCT,
        GET_LIST_PRODUCTS,
        DELETE_ALL_IN_GROUP, // видалити всі продукти з даної групи
        INSERT_GROUP,
        DELETE_GROUP, // видалити всю групу і її рядочки
        UPDATE_GROUP,
        GET_GROUP,
        GET_LIST_GROUPS,
    }


    byte[] whole;
    Integer cType;
    Integer bUserId;
    byte[] message;

    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;

    public Message(byte[] whole) {//DECODEs the encoded message

        this.whole = whole;

        try {
            byte[] decoded = DeEncriptor.decode(whole);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            this.cType = buffer.getInt();
            this.bUserId = buffer.getInt();
            message = new byte[decoded.length - BYTES_WITHOUT_MESSAGE];
            buffer.get(message);

        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException
                | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public Message(Integer cType, Integer bUserId, byte[] message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;

        this.whole = toPacketPart();
    }

    public byte[] toPacketPart() { //ENCODEs message
        byte[] msg = ByteBuffer.allocate(BYTES_WITHOUT_MESSAGE + message.length)
                .putInt(cType)
                .putInt(bUserId)
                .put(message).array();
        byte[] res = new byte[BYTES_WITHOUT_MESSAGE + message.length];
        try {
            res = DeEncriptor.encode(msg);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
                | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return res;
    }
}