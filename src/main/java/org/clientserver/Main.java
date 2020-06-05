package org.clientserver;

import org.clientserver.entities.DaoProduct;
import org.clientserver.entities.Product;
import org.clientserver.entities.ProductFilter;

import java.util.Arrays;

public class Main {

    public static final String tableProduct = "products";

    public static void main(String[] args) {
        final DaoProduct daoProduct = new DaoProduct("file.db");
        daoProduct.deleteAll();
        for(int i = 0; i < 30; i++){
            daoProduct.insertProduct(new Product("гречка" + i, Math.random()*1000,Math.random()*1000,"very good", "Rodyna"));
        }

        daoProduct.getList(0, 10, new ProductFilter())
                .forEach(System.out::println);

        System.out.println("~~~~~~~~~~~~~~~");


        daoProduct.getList(0, 10, new ProductFilter())
                .forEach(System.out::println);
        final ProductFilter filter = new ProductFilter();

//        filter.setIds(Arrays.asList(1, 3, 7, 13));
//        filter.setFromPrice(100.0);
//        filter.setToPrice(750.0);
//        daoProduct.getList(0, 20, filter)
//                .forEach(System.out::println);

        Product pshono = new Product(2,"пшоно",250.0,34.6,"its okay","Roshen");

        daoProduct.updateProduct(pshono);

        filter.setIds(Arrays.asList(2));
        daoProduct.getList(0, 20, filter)
                .forEach(System.out::println);

        //System.out.println("PRODUCT "+daoProduct.getProduct(43).toJSON());

        daoProduct.deleteTable();
    }
}