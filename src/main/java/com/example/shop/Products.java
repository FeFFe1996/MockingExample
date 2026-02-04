package com.example.shop;

import java.math.BigDecimal;

public class Products {
    private String productName;
    private BigDecimal price;

    public Products(String name, BigDecimal price){
        this.productName = name;
        this.price = price;
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
