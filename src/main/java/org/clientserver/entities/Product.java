package org.clientserver.entities;

import org.json.JSONObject;

public class Product {

    private final Integer id;
    private final String name;
    private final double price;

    public double getAmount() {
        return amount;
    }

    private final double amount;
    private final String description;
    private final String manufacturer;

    public Product(final Integer id, final String name, final double price, final double amount, final String description, String manufacturer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.manufacturer = manufacturer;
    }

    public Product(final String name, final double price, final double amount, final String description, String manufacturer) {
        this(null, name, price, amount, description, manufacturer);
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

    public String getDescription() { return description; }

    public String getManufacturer() { return manufacturer; }

    @Override
    public String toString(){
        return "{"+"\"id\":\""+id+"\", \"name\":\""+name+"\", \"price\":\""+
                price+"\", \"description\":\""+description+"\", \"manufacturer\":\""+manufacturer+"\"}";
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject("{"+"\"id\":\""+id+"\", \"name\":\""+name+"\", \"price\":\""+
                price+"\", \"description\":\""+description+"\", \"manufacturer\":\""+manufacturer+"\"}");
        return json;

    }
}
