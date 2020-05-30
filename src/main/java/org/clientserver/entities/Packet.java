package org.clientserver.entities;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import lombok.Data;
import java.nio.ByteBuffer;

@Data
public class Packet {
    public static Byte getbMagic() {
        return bMagic;
    }
    public UnsignedLong getbPktId() {
        return bPktId;
    }
    public Byte getSrcId() { return srcId; }
    public Short getwCrc16_1() {
        return wCrc16_1;
    }
    public Short getwCrc16_2() {
        return wCrc16_2;
    }

    public final static Byte bMagic = 0x13;
    Byte srcId;//user ID
    UnsignedLong bPktId;
    Integer wLen;
    Short wCrc16_1;
    Message bMsq;
    Short wCrc16_2;



    public final static Integer packetPartFirstLengthWithoutwLen = bMagic.BYTES + Byte.BYTES + Long.BYTES;
    public final static Integer packetPartFirstLength = packetPartFirstLengthWithoutwLen + Integer.BYTES;
    public final static Integer packetPartFirstLengthWithCRC16 = packetPartFirstLength + Short.BYTES;

    public Packet(Byte srcId, UnsignedLong bPktId, Message bMsq) {
        this.srcId = srcId;
        this.bPktId = bPktId;
        this.bMsq = bMsq;
        wLen = bMsq.getMessageBytesLength();
    }

    public Packet(byte[] encodedPacket){ //UNPACKING/DECODING THE PACKET with ENCODED MESSAGE
        ByteBuffer buffer = ByteBuffer.wrap(encodedPacket);
        Byte expectedBMagic = buffer.get();
        if(!expectedBMagic.equals(bMagic)){
            throw new IllegalArgumentException("Invalid magic byte!");
        }
        srcId = buffer.get();
        bPktId = UnsignedLong.fromLongBits(buffer.getLong());
        wLen = buffer.getInt();
        wCrc16_1 = buffer.getShort();
        byte[] packetPartFirst = ByteBuffer.allocate(packetPartFirstLength)
                .put(bMagic)
                .put(srcId)
                .putLong(bPktId.longValue())
                .putInt(wLen)
                .array();
        final Short crc1Evaluated = calculateCRC16(packetPartFirst);
        if(!crc1Evaluated.equals(wCrc16_1)){
            throw new IllegalArgumentException("CRC1 expected: " + crc1Evaluated + ", out was " + getwCrc16_1());
        }
        byte[] messageBody = new byte[wLen];
        buffer.get(messageBody);
        bMsq = new Message(messageBody);//constructor to DECRYPT encoded MESSAGE
        wCrc16_2 = buffer.getShort();
        Integer packetPartSecondLength = bMsq.getMessageBytesLength();
        byte[] packetPartSecond = ByteBuffer.allocate(packetPartSecondLength)
                .put(bMsq.toPacketPart())
                .array();
        final Short crc2Evaluated = calculateCRC16(packetPartSecond);
        if(!crc2Evaluated.equals(wCrc16_2)){
            throw new IllegalArgumentException("CRC2 expected: " + crc2Evaluated + ", out was " + getwCrc16_2());
        }
    }

    //PACK THE PACKET WITH ENCODED MESSAGE FOR USER
    public byte[] toPacket() {
        Message message = getBMsq();
        byte[] packetPartFirst = ByteBuffer.allocate(packetPartFirstLength)
                .put(bMagic)
                .put(srcId)
                .putLong(bPktId.longValue())
                .putInt(wLen)
                .array();
        wCrc16_1 = calculateCRC16(packetPartFirst);
        Integer packetPartSecondLength = message.getMessageBytesLength();
        byte[] packetPartSecond = ByteBuffer.allocate(packetPartSecondLength)
                .put(message.toPacketPart())
                .array();
        wCrc16_2 = calculateCRC16(packetPartSecond);
        Integer packetLength = packetPartFirstLength + wCrc16_1.BYTES + packetPartSecondLength + wCrc16_2.BYTES;
        return ByteBuffer.allocate(packetLength).put(packetPartFirst).putShort(wCrc16_1).put(packetPartSecond).putShort(wCrc16_2).array();
    }

    public Message getBMsq() {
        return  bMsq;
    }

    public static Short calculateCRC16(byte[] packetPartFirst) {
        return (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetPartFirst);
    }
}