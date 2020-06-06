package org.clientserver.entities;

import org.json.JSONObject;

public class Group {

    private final Integer id;
    private final String name;
    private final String description;

    public Group(final Integer id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() { return description; }


    @Override
    public String toString(){
        return "{"+"\"id\":\""+id+"\", \"name\":\""+name+"\", \"description\":\""+description+"\"}";
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject("{"+"\"id\":"+id+", \"name\":\""+name+"\", \"description\":\""+description+"\"}");
        return json;

    }

       public boolean equals(Group p){
        if(this.id.equals(p.getId()) && this.name.equals(p.getName())
                && this.description.equals(p.getDescription())){
            return true;
        }
        return false;
    }

}
