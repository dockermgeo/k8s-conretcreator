package de.mgeo.cose.model;

import java.util.HashMap;

public class StoreSecret {

    private String name;
    private HashMap<String, String> stringData = new HashMap<String,String>();

    public StoreSecret(String name) {
        this.name=name;
    }


    public String getName() {
        return name;
    }

    public HashMap getData() {
        return this.stringData;
    }

    public void addEntry(String name, String value) {
        this.stringData.put(name,value);
    }
}
