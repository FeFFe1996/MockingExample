package com.example.shop;

public class Customer {
    private String custommerID;
    private String name;

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
