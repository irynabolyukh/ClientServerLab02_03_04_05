package org.clientserver;

import com.google.common.primitives.UnsignedLong;
import org.clientserver.classes.Processor;
import org.clientserver.entities.*;
import org.json.JSONObject;

import java.util.Arrays;

public class Main {

    public static final String tableProduct = "products";

    public static void main(String[] args) {

//        MessageGenerator.generate((byte)1, UnsignedLong.ONE);

//        DaoGroup daoGroup = new DaoGroup("file.db");
//        for(int i = 0; i < 10; i++){
//            daoGroup.insertGroup(new Group( i, "very good"+i, "Rodyna"));
//        }
//
//        daoGroup.updateGroup(new Group(2,"okay","hello"));
//
//        daoGroup.getAll()
//                .forEach(System.out::println);
//
//        System.out.println(daoGroup.toJSONObject(daoGroup.getAll()));

//
        final DaoProduct daoProduct = new DaoProduct("file.db");
//        daoProduct.deleteAll();
        for(int i = 0; i < 30; i++){
            daoProduct.insertProduct(new Product("гречка" , Math.random()*1000,Math.random()*1000,"very good", "Rodyna",i));
        }

        daoProduct.getList(0, 10, new ProductFilter())
                .forEach(System.out::println);
         Processor.process( MessageGenerator.generate((byte)1, UnsignedLong.ONE));
//
//        System.out.println("~~~~~~~~~~~~~~~");
//
//
//        daoProduct.getList(0, 10, new ProductFilter())
//                .forEach(System.out::println);
//        final ProductFilter filter = new ProductFilter();
//
////        filter.setIds(Arrays.asList(1, 3, 7, 13));
////        filter.setFromPrice(100.0);
////        filter.setToPrice(750.0);
////        daoProduct.getList(0, 20, filter)
////                .forEach(System.out::println);
//
//        Product pshono = new Product(2,"пшоно",250.0,34.6,"its okay","Roshen",12);
//
//        daoProduct.updateProduct(pshono);
//
//        filter.setIds(Arrays.asList(2));
//        daoProduct.getList(0, 20, filter)
//                .forEach(System.out::println);
//
//        //System.out.println("PRODUCT "+daoProduct.getProduct(43).toJSON());
//
//
//
//        System.out.println("----------");
//
//        ProductFilter filter2 = new ProductFilter();
//        filter2.setManufacturer("Rodyna");
//        daoProduct.getList(0, 20, filter2)
//                .forEach(System.out::println);
//
       // daoGroup.deleteTable();
        daoProduct.deleteTable();

    }
}