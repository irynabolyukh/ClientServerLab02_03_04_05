package org.clientserver.entities;

import com.google.common.primitives.UnsignedLong;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageGenerator { //GENERATE MESSAGE FROM CLIENT
//        public static byte[] generate(byte srcID, UnsignedLong pktId) {
//                Random random = new Random();
//                int command = random.nextInt(Message.cTypes.values().length);
//                String commandMsg = (Message.cTypes.values()[command]).toString();
//
//                //creates message with random command
//                Message testMessage = new Message(command ,1, commandMsg.getBytes(StandardCharsets.UTF_8));
//                Packet packet = new Packet(srcID, pktId, testMessage);
//
//                byte[] packetToBytes = packet.toPacket();//encodes packet
//                return packetToBytes;
//        }

        public static byte[] generate(byte srcId, UnsignedLong bPktId){

                Product prod = new Product(2, "пшоно", 12, 99, "bkhv", "bkjb", 1);
                Product prod2 = new Product(2, "пшоно2", 12, 99, "bkhv", "Рошен", 1);
                Product prod3 = new Product(2, "пшоно3", 12, 99, "bkhv", "Родина", 1);
                Group group = new Group(100, "крупи", "смачно");
                Group group2 = new Group(8, "мийні засоби", "не смачно");

                List<Integer> list = new ArrayList<Integer>();
                list.add(1);
                list.add(2);
                list.add(3);
                list.add(4);
                list.add(5);
                list.add(6);

                ProductFilter fl = new ProductFilter();
                fl.setIds(list);
                fl.setFromPrice(3.99);
                fl.setToPrice(1000.99);
                fl.setManufacturer("Rodyna");

                JSONObject jsonObj = new JSONObject("{"+"\"page\":"+0+", \"size\":"+10+
                        ", \"productFilter\":"+ fl.toJSON().toString() +"}");

                Message msg1 = new Message(Message.cTypes.DELETE_ALL_IN_GROUP.ordinal() , 1, "2".getBytes(StandardCharsets.UTF_8));
                Message msg2 = new Message(Message.cTypes.DELETE_GROUP.ordinal() , 1, "1".getBytes(StandardCharsets.UTF_8));
                Message msg3 = new Message(Message.cTypes.DELETE_PRODUCT.ordinal() , 1, "21".getBytes(StandardCharsets.UTF_8));
                Message msg4 = new Message(Message.cTypes.UPDATE_PRODUCT.ordinal() , 1, prod2.toJSON().toString().getBytes(StandardCharsets.UTF_8));
                Message msg5 = new Message(Message.cTypes.INSERT_PRODUCT.ordinal() , 1, prod3.toJSON().toString().getBytes(StandardCharsets.UTF_8));
                Message msg6 = new Message(Message.cTypes.GET_PRODUCT.ordinal() , 1, "22".getBytes(StandardCharsets.UTF_8));
                Message msg7 = new Message(Message.cTypes.GET_GROUP.ordinal() , 1, "2".getBytes(StandardCharsets.UTF_8));
                Message msg8 = new Message(Message.cTypes.GET_LIST_GROUPS.ordinal() , 1, "".getBytes(StandardCharsets.UTF_8));
                Message msg9 = new Message(Message.cTypes.INSERT_GROUP.ordinal() , 1, group.toJSON().toString().getBytes(StandardCharsets.UTF_8));
                Message msg10 = new Message(Message.cTypes.UPDATE_GROUP.ordinal() , 1, group2.toJSON().toString().getBytes(StandardCharsets.UTF_8));
                Message msg11 = new Message(Message.cTypes.GET_LIST_PRODUCTS.ordinal() , 1, jsonObj.toString().getBytes(StandardCharsets.UTF_8));

                Message[] msgArray = {msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11};

                Packet packet = new Packet(srcId, bPktId, msg9);

                return packet.toPacket();
        }
}