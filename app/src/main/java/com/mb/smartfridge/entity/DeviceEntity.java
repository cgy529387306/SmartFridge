package com.mb.smartfridge.entity;

/**
 * @author chenqm on 2017/7/14.
 */

public class DeviceEntity {

    private String name ;
    private String model;

    public DeviceEntity(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

}
