package com.example.shop;

import java.math.BigDecimal;
import java.util.UUID;

public class Products {
    private final String id;
    private String productName;
    private BigDecimal price;

    public Products(String id, String name, BigDecimal price){
        this.id = id;
        this.productName = name;
        this.price = price;
    }

    public String getID(){
        return this.id;
    }

    public void setProductName(String name){
        this.productName = name;
    }

    public String getProductName(){
        return productName;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }
}
