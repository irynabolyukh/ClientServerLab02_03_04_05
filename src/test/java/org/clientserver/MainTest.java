package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.apache.commons.codec.binary.Hex;
import org.clientserver.Dao.*;
import org.clientserver.classes.DeEncriptor;
import org.clientserver.entities.*;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest{

//    @Test
//    void checkWhether_InvalidMagicByte() {
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> new Packet(Hex.decodeHex("15"))
//        );
//    }
//
//    @Test
//    void checkWhether_InvalidCrc(){
//        final String input = "1300000000000000000a000000300a8b6c0221f35d79ec1715362980276b7c96a5ec7b0f8e40428fff0f7f54652c00dce9ea";
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> new Packet(Hex.decodeHex(input))
//        );
//    }

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


 //FOR PRODUCTS
    @Test
    void check_insert_and_get_product(){
        Product product = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        DaoProduct daoProduct = new DaoProduct("test.db");

        daoProduct.insertProduct(product);
        Product insertedProduct = daoProduct.getProduct(1);

        assert(product.equals(insertedProduct));

        daoProduct.deleteTable();
    }

    @Test
    void check_update_and_get_product(){
        Product product1 = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        Product product2 = new Product(1,"пшоно",234.5,324,"good","rodyna",1);

        DaoProduct  daoProduct = new DaoProduct("test.db");
        daoProduct.insertProduct(product1);
        daoProduct.updateProduct(product2);

        Product updatedProduct = daoProduct.getProduct(1);

        assert(product2.equals(updatedProduct));

        daoProduct.deleteTable();
    }

    @Test
    void check_get_product(){
        DaoProduct daoProduct = new DaoProduct("test.db");
        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoProduct.getProduct(1)
        );
        daoProduct.deleteTable();
    }

    @Test
    void check_get_All_Products(){
        int expected = 5;

        final DaoProduct daoProduct = new DaoProduct("test.db");
        for(int i = 0; i < expected; i++){
            daoProduct.insertProduct(new Product("гречка"+i , Math.random()*100,Math.random()*100,"very good", "Summer",1));
        }

        List<Product> products = daoProduct.getAll(0,10);

        assert(expected == products.size());

        daoProduct.deleteTable();
    }

    @Test
    void check_filter_for_Product(){
        ProductFilter filter = new ProductFilter();
        filter.setManufacturer("Summer");
        filter.setGroup_id(1);

        int expected = 5;

        final DaoProduct daoProduct = new DaoProduct("test.db");
        for(int i = 0; i < expected; i++){
            daoProduct.insertProduct(new Product("гречка"+i , Math.random()*100,Math.random()*100,"very good", "Summer",1));
        }
        for(int i = 5; i < 10; i++){
            daoProduct.insertProduct(new Product("мінеральна"+i , Math.random()*100,Math.random()*100,"good", "Rodyna",2));
        }

        List<Product> products = daoProduct.getList(0,10, filter);

        assert(expected == products.size());

        daoProduct.deleteTable();
    }

    @Test
    void check_delete_product(){

        Product product1 = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        Product product2 = new Product(2,"пшоно",234.5,324,"good","rodyna",1);

        DaoProduct  daoProduct = new DaoProduct("test");
        daoProduct.insertProduct(product1);
        daoProduct.insertProduct(product2);
        daoProduct.deleteProduct(1);

        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoProduct.getProduct(1)
        );

        daoProduct.deleteTable();
    }

    @Test
    void check_delete_all_products(){

        Product product1 = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        Product product2 = new Product(2,"пшоно",234.5,324,"good","rodyna",1);
        Product product3 = new Product(3,"пшоно2",234.5,324,"good","rodyna",1);

        DaoProduct  daoProduct = new DaoProduct("test");
        daoProduct.insertProduct(product1);
        daoProduct.insertProduct(product2);
        daoProduct.insertProduct(product3);

        daoProduct.deleteAll();

        List<Product> products = daoProduct.getAll(0, 10);

        assert(products.size()==0);

        daoProduct.deleteTable();
    }

    @Test
    void check_delete_all_products_in_group(){

        Product product1 = new Product(1,"гречка",234.5,324,"good","rodyna",1);
        Product product2 = new Product(2,"пшоно",234.5,324,"good","rodyna",1);
        Product product3 = new Product(3,"пшоно2",234.5,324,"good","rodyna",2);

        DaoProduct daoProduct = new DaoProduct("test");
        daoProduct.insertProduct(product1);
        daoProduct.insertProduct(product2);
        daoProduct.insertProduct(product3);

        daoProduct.deleteAllInGroup(1);

        ProductFilter filter = new ProductFilter();
        filter.setGroup_id(1);
        List<Product> products = daoProduct.getList(0, 10, filter);

        assert(products.size()==0 && daoProduct.getAll(0,10).size()==1);

        daoProduct.deleteTable();
    }


//FOR GROUPS
    @Test
    void check_insert_and_get_group(){
        Group group = new Group(100, "крупи", "смачно");
        DaoGroup daoGroup = new DaoGroup("test.db");

        daoGroup.insertGroup(group);
        Group insertedGroup = daoGroup.getGroup(100);

        assert(group.equals(insertedGroup));

        daoGroup.deleteTable();
    }

    @Test
    void check_update_and_get_group(){

        Group group = new Group(100, "крупи", "смачно");
        Group group2 = new Group(100, "мийні засоби", "не смачно");
        DaoGroup daoGroup = new DaoGroup("test.db");

        daoGroup.insertGroup(group);
        daoGroup.updateGroup(group2);

        Group updatedGroup = daoGroup.getGroup(100);

        assert(group2.equals(updatedGroup));

        daoGroup.deleteTable();
    }

    @Test
    void check_get_group(){
        DaoGroup daoGroup = new DaoGroup("test.db");
        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoGroup.getGroup(1)
        );

        daoGroup.deleteTable();
    }

    @Test
    void check_get_All_Groups(){
        int expected = 5;

        final DaoGroup daoGroup = new DaoGroup("test.db");
        for(int i = 0; i < expected; i++){
            daoGroup.insertGroup(new Group(i ,"Фрукти"+i, "смачні"));
        }

        List<Group> groups = daoGroup.getAll();

        assert(expected == groups.size());

        daoGroup.deleteTable();
    }

    @Test
    void check_delete_group(){

        Group group = new Group(100, "крупи", "смачно");
        Group group2 = new Group(10, "мийні засоби", "не смачно");
        DaoGroup daoGroup = new DaoGroup("test.db");

        daoGroup.insertGroup(group);
        daoGroup.insertGroup(group2);
        daoGroup.deleteGroup(100);

        assertThrows(
                java.lang.RuntimeException.class,
                () -> daoGroup.getGroup(100)
        );

        daoGroup.deleteTable();
    }

    @Test
    void check_delete_all_groups(){

        Group group = new Group(100, "крупи", "смачно");
        Group group2 = new Group(10, "мийні засоби", "не смачно");
        Group group3 = new Group(11, "одяг", "не смачно");
        DaoGroup daoGroup = new DaoGroup("test.db");

        daoGroup.insertGroup(group);
        daoGroup.insertGroup(group2);
        daoGroup.insertGroup(group3);

        daoGroup.deleteAll();

        List<Group> groups = daoGroup.getAll();

        assert(groups.size()==0);

        daoGroup.deleteTable();
    }

}