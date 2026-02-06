package com.example.shop;

import java.util.UUID;

public class Customer {
    private String custommerID;
    private String name;

    public Customer( String name){
        this.custommerID = String.valueOf(UUID.randomUUID());
        this.name = name;
    }

    public Customer(String id, String name){
        this.custommerID = id;
        this.name = name;
    }


    public String getCustommerID() {
        return custommerID;
    }

    public String getName() {
        return name;
    }
}
