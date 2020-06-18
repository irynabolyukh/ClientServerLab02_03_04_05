package org.clientserver.Dao;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Product {

    private Integer id;
    private String name;
    private double price;
    private double amount;
    private String description;
    private String manufacturer;
    private Integer group_id;

    public Product(){

    }

    public Product(final Integer id, final String name, final double price, final double amount, final String description, String manufacturer, Integer group_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.manufacturer = manufacturer;
        this.group_id = group_id;
    }

//    public Product(final String name, final double price, final double amount, final String description, String manufacturer, Integer group_id) {
//        this(null, name, price, amount, description, manufacturer, group_id);
//    }

    public JSONObject toJSON(){

        JSONObject json = new JSONObject("{"+"\"id\":"+id+", \"name\":\""+name+
                "\", \"price\":"+ price+", \"amount\":"+amount+
                ", \"description\":\""+description+"\", \"manufacturer\":\""+manufacturer+
                "\", \"group_id\":"+group_id+"}");

        return json;
    }

    @Override
    public String toString(){
        return "Product("+"id="+id+", name="+name+", price="+price+
                ", amount="+ amount + ", description="+description +
                ", manufacturer="+manufacturer + ", group_id="+group_id+')';
    }

    public boolean equals(Product p){
        if(this.id.equals(p.getId()) && this.name.equals(p.getName()) && this.amount == p.getAmount()
                && this.price == p.getPrice() && this.description.equals(p.getDescription()) && this.manufacturer.equals(p.getManufacturer())){
            return true;
        }
        return false;
    }
}

