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
    public byte[] getMessage() {
        return message;
    }

    public Integer getcType() {
        return cType;
    }

    public Integer getbUserId() {
        return bUserId;
    }

    public byte[] getWhole() {
        return whole;
    }

    enum cTypes {
        GET_PRODUCT_COUNT,
        GET_PRODUCT,
        ADD_PRODUCT,
        ADD_PRODUCT_TITLE,
        SET_PRODUCT_PRICE,
        ADD_PRODUCT_TO_GROUP
    }

    byte[] whole;
    Integer cType;
    Integer bUserId;
    byte[] message;

    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;

    public Message(byte[] whole) {//ENCODED MESSAGE

        this.whole = whole;

        try {
            byte[] decoded = DeEncriptor.decode(whole);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            this.cType = buffer.getInt();
            this.bUserId = buffer.getInt();
            message = new byte[decoded.length - BYTES_WITHOUT_MESSAGE];
            buffer.get(message);

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public Message(Integer cType, Integer bUserId, byte[] message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;

        this.whole = toPacketPart();
    }

    public byte[] toPacketPart() {
        byte[] msg = ByteBuffer.allocate(BYTES_WITHOUT_MESSAGE + message.length)
                .putInt(cType)
                .putInt(bUserId)
                .put(message).array();
        byte[] res = new byte[8 + message.length];
        try {
            res = DeEncriptor.encode(msg);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getMessageBytesLength() {
        return whole.length;
    }
}