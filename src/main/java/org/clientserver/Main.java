package org.clientserver;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Server();
    }

}
//public class Main {
//
//    public static void main(String[] args) {
//
//        final DaoGroup daoGroup = new DaoGroup("file.db");
//        for(int i = 0; i < 30; i++){
//            daoGroup.insertGroup(new Group( i, "very good"+i, "Rodyna"));
//        }
//
//        daoGroup.getAll()
//                .forEach(System.out::println);
//
//
//        final DaoProduct daoProduct = new DaoProduct("file.db");
//        for(int i = 0; i < 30; i++){
//            daoProduct.insertProduct(new Product("гречка"+i , Math.random()*1000,Math.random()*1000,"very good", "Rodyna", i));
//        }
//
//        daoProduct.getAll(0,30)
//                .forEach(System.out::println);
//
////         daoGroup.deleteTable();
////        daoProduct.deleteTable();
//    }
//}