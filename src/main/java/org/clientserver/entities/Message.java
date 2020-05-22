package org.clientserver.entities;

import lombok.Data;
import org.clientserver.classes.Cipher;

import java.nio.ByteBuffer;

@Data
public class Message {
    public String getMessage() { return message; }

    public Integer getbUserId() { return bUserId; }

    public void setBUserId(Integer bUserId) {
        this.bUserId = bUserId;
    }

    public void setCType(Integer cType) {
        this.bUserId = cType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    enum cTypes {
        GET_PRODUCT_COUNT,
        GET_PRODUCT,
        ADD_PRODUCT,
        ADD_PRODUCT_TITLE,
        SET_PRODUCT_PRICE,
        ADD_PRODUCT_TO_GROUP
    }

    Integer cType;
    Integer bUserId;
    String message;

    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;

    public Message() { }

    public Message(Integer cType, Integer bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    public byte[] toPacketPart() {
        return ByteBuffer.allocate(getMessageBytesLength())
                .putInt(cType)
                .putInt(bUserId)
                .put(message.getBytes()).array();
    }

    public int getMessageBytesLength() {
        return BYTES_WITHOUT_MESSAGE + getMessageBytes();
    }

    public Integer getMessageBytes() {
        return message.length();
    }

    public void encode() {
        message = Cipher.encode(message);
    }

    public void decode() {
        message = Cipher.decode(message);
    }
}