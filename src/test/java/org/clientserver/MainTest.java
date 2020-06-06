package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.apache.commons.codec.binary.Hex;
import org.clientserver.classes.DeEncriptor;
import org.clientserver.entities.DaoProduct;
import org.clientserver.entities.Message;
import org.clientserver.entities.Packet;
import org.clientserver.entities.Product;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest{

    @Test
    void checkWhether_InvalidMagicByte() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Packet(Hex.decodeHex("15"))
        );
    }

    @Test
    void checkWhether_InvalidCrc(){
        final String input = "1300000000000000000a000000300a8b6c0221f35d79ec1715362980276b7c96a5ec7b0f8e40428fff0f7f54652c00dce9ea";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Packet(Hex.decodeHex(input))
        );
    }

    @Test
    void check_DeEncriptor() throws NoSuchAlgorithmException, NoSuchPaddingException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        byte[] origMessageToBytes = originalMessage.toPacketPart();
        byte[] encode_decode = DeEncriptor.decode(DeEncriptor.encode(originalMessage.getWhole()));
        assert(Arrays.equals(origMessageToBytes, encode_decode));
    }

    @Test
    void check_DeEncriptor_forPacket() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        byte[] origMessageToBytes = DeEncriptor.decode(originalMessage.toPacketPart());

        Packet packet = new Packet((byte)1, UnsignedLong.ONE, originalMessage);
        byte[] packetToBytes = packet.toPacket();//encoding packet

        Packet decoded_packet = new Packet(packetToBytes);
        Message decoded_message = decoded_packet.getBMsq();

        byte[] decoded_messageToBytes = DeEncriptor.decode(decoded_message.toPacketPart());
        assert(Arrays.equals(origMessageToBytes, decoded_messageToBytes));
    }

    @Test
    void check_DeEncriptor_forPacket2(){
        Message originalMessage = new Message(1,1, new String("hello from user").getBytes());
        Packet packet = new Packet((byte)1, UnsignedLong.ONE, originalMessage);

        byte[] packBytes = packet.toPacket();//encodes packet

        Packet packet1 = new Packet(packBytes);//decodes packet

        byte [] packBytes1 = packet1.toPacket();//encodes packet

        assert(Arrays.equals(packBytes, packBytes1));
    }

    @Test
    void check_insert_and_get_product(){
        Product product = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        DaoProduct  daoProduct = new DaoProduct("test");

        daoProduct.insertProduct(product);
        Product insertedProduct = daoProduct.getProduct(1);

        assert(product.equals(insertedProduct));

        daoProduct.deleteTable();
    }

    @Test
    void check_update_and_insert_product(){
        Product product1 = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        Product product2 = new Product(1,"пшоно",234.5,324,"good","rodyna",1);

        DaoProduct  daoProduct = new DaoProduct("test");
        daoProduct.insertProduct(product1);
        daoProduct.updateProduct(product2);

        Product updatedProduct = daoProduct.getProduct(1);

        assert(product2.equals(updatedProduct));

        daoProduct.deleteTable();
    }
}