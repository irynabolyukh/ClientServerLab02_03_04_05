package org.clientserver.classes;
import org.clientserver.Dao.*;
import org.clientserver.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Processor{
    public static byte[] process(byte[] packetFromUser) {//processes packet from USER, gets encoded packet
        Packet packet = new Packet(packetFromUser);//decoding packet from USER

        Message.cTypes [] val = Message.cTypes.values();

        int command = packet.getBMsq().getcType();

        Message.cTypes command_type = val[command];

        String message = new String(packet.getBMsq().getMessage(), StandardCharsets.UTF_8);

        DaoProduct daoProduct;
        DaoGroup daoGroup;
        JSONObject information;
        String reply;
        int success;

        switch(command_type){

            case INSERT_PRODUCT:
                information = new JSONObject(message);

                Product product = new Product( information.getInt("id"),information.getString("name"),
                        information.getDouble("price"),information.getDouble("amount"),information.getString("description"),
                        information.getString("manufacturer"),information.getInt("group_id"));

                daoProduct = new DaoProduct("file.db");
                success = daoProduct.insertProduct(product);

                if(success == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successfully inserted product";
                }
                break;

            case UPDATE_PRODUCT:
                information = new JSONObject(message);

                Product product2 = new Product( information.getInt("id"),information.getString("name"),
                        information.getDouble("price"),information.getDouble("amount"),information.getString("description"),
                        information.getString("manufacturer"),information.getInt("group_id"));

                daoProduct = new DaoProduct("file.db");
                success = daoProduct.updateProduct(product2);

                if(success == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successfully updated product";
                }
                break;

            case DELETE_PRODUCT:
                int id = Integer.parseInt(message);
                daoProduct = new DaoProduct("file.db");
                success = daoProduct.deleteProduct(id);
                if(success == -1){
                    reply = "Invalid name of product";
                }
                else{
                    reply = "Successfully deleted product with id " + success;
                }
                break;

            case GET_PRODUCT:
                int id4 = Integer.parseInt(message);
                daoProduct = new DaoProduct("file.db");
                Product product4 = daoProduct.getProduct(id4);

                reply = product4.toJSON().toString();
                break;

            case GET_LIST_PRODUCTS:
                information = new JSONObject(message);

                int page = information.getInt("page");
                int size = information.getInt("size");

                JSONObject filtr = information.getJSONObject("productFilter");
                ProductFilter filter = new ProductFilter();
                JSONArray array = filtr.getJSONArray("ids");

                if(!filtr.isNull("ids")){
                    List<Integer> arrayList = new ArrayList<>();
                    for(int i = 0; i < array.length(); i++){
                        arrayList.add((Integer)(array.get(i)));
                    }
                    filter.setIds(arrayList);
                }
                if(!filtr.isNull("group_id")){
                    filter.setGroup_id(filtr.getInt("group_id"));
                }
                if(!filtr.isNull("manufacturer")){
                    filter.setManufacturer(filtr.getString("manufacturer"));
                }
                if(!filtr.isNull("toPrice")){
                    filter.setToPrice(filtr.getDouble("toPrice"));
                }
                if(!filtr.isNull("fromPrice")){
                    filter.setFromPrice(filtr.getDouble("fromPrice"));
                }
                if(!filtr.isNull("query")){
                    filter.setQuery("query");
                }
                daoProduct = new DaoProduct("file.db");

                reply = daoProduct.toJSONObject(daoProduct.getList(page, size, filter)).toString();
                break;

            case DELETE_ALL_IN_GROUP:
                int id6 = Integer.parseInt(message);
                daoProduct = new DaoProduct("file.db");
                daoProduct.deleteAllInGroup(id6);

                reply = "Products in group " + id6 + " were deleted";
                break;

            case INSERT_GROUP:
                information = new JSONObject(message);
                Group group = new Group( information.getInt("id"),information.getString("name"),
                        information.getString("description"));
                daoGroup = new DaoGroup("file.db");
                success = daoGroup.insertGroup(group);
                if(success == -1){
                    reply = "Invalid name of group";
                }
                else{
                    reply = "Successfully inserted group";
                }
                break;

            case DELETE_GROUP:
                int group_id = Integer.parseInt(message);
                daoGroup = new DaoGroup("file.db");
                success = daoGroup.deleteGroup(group_id);
                if(success == -1){
                    reply = "Can't delete group";
                }
                else{
                    reply = "Successfully deleted group";
                }
                break;

            case UPDATE_GROUP:
                information = new JSONObject(message);
                Group group1 = new Group( information.getInt("id"),
                        information.getString("name"), information.getString("description"));

                daoGroup = new DaoGroup("file.db");
                success = daoGroup.updateGroup(group1);

                if(success == -1){
                    reply = "Invalid name of group";
                }
                else{
                    reply = "Successfully updated group";
                }
                break;

            case GET_GROUP:
                int group_id1 = Integer.parseInt(message);
                daoGroup = new DaoGroup("file.db");
                Group resGroup = daoGroup.getGroup(group_id1);

                reply = resGroup.toJSON().toString();
                break;

            case GET_LIST_GROUPS:
                daoGroup = new DaoGroup("file.db");
                reply = daoGroup.toJSONObject(daoGroup.getAll()).toString();
                break;

            default:
                 reply = "INVALID COMMAND";
        }

        System.out.println("Message from client: " + new String(packet.getBMsq().getMessage(), StandardCharsets.UTF_8)
                + "\t\t\t with user ID: " + packet.getSrcId()
                + "\t\t\t and with packet ID: " + packet.getbPktId());


        Message answerMessage = new Message(packet.getBMsq().getcType(), packet.getBMsq().getbUserId(), reply.getBytes(StandardCharsets.UTF_8));
        Packet answerPacket = new Packet(packet.getSrcId(), packet.getbPktId(), answerMessage);

        return answerPacket.toPacket(); //returns encoded response for USER
    }
}
