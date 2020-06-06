package org.clientserver.entities;

import org.json.JSONObject;

import java.util.List;

public class ProductFilter {

    private List<Integer> ids;
    private String query;
    private Double fromPrice;
    private Double toPrice;
    private String manufacturer;
    private Integer group_id;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public void setQuery(String query) {
            this.query = query;
    }

    public Double getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(Double fromPrice) {
        this.fromPrice = fromPrice;
    }

    public Double getToPrice() {
        return toPrice;
    }

    public void setToPrice(Double toPrice) {
        this.toPrice = toPrice;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public JSONObject toJSON(){

        Object[] objArr = ids.toArray();

        String arr = "[";

        for (int i=0; i<objArr.length-1; i++) {
            //intArr[i] = ((Integer) objArr[i]);
            arr += (Integer) objArr[i] + ", ";
        }
        arr += (Integer) objArr[objArr.length-1] + "]";

        JSONObject json = new JSONObject("{"+"\"ids\":"+arr+", \"query\":\""+query+
                "\", \"fromPrice\":"+ fromPrice+", \"toPrice\":"+toPrice+
                ", \"manufacturer\":\""+ manufacturer+"\", \"group_id\":"+ group_id+"}");

        return json;
    }

}
