package org.clientserver.entities;

import org.json.JSONObject;

public class Product {

    private final Integer id;
    private final String name;
    private final double price;
    private final double amount;
    private final String description;
    private final String manufacturer;
    private final Integer group_id;

    public Product(final Integer id, final String name, final double price, final double amount, final String description, String manufacturer, Integer group_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.manufacturer = manufacturer;
        this.group_id = group_id;
    }

    public Product(final String name, final double price, final double amount, final String description, String manufacturer, Integer group_id) {
        this(null, name, price, amount, description, manufacturer, group_id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() { return description; }

    public String getManufacturer() { return manufacturer; }

    public Integer getGroup_id() {
        return group_id;
    }

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
}

