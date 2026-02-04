package com.example.shop;

import java.math.BigDecimal;
import java.util.UUID;

public class Products {
    private final String id;
    private String productName;
    private int stockAmount;
    private int cartAmount = 0;
    private BigDecimal price;

    public Products(String id, String name, int stockAmount,BigDecimal price){
        this.id = id;
        this.productName = name;
        if (stockAmount < 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }else {
            this.stockAmount = stockAmount;
        }
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

    public int getCartAmount(){ return cartAmount; }

    public void addOneAmount(){
        cartAmount++;
    }

    public void removeOneAmount(){
        cartAmount--;
    }

    public int getStockAmount() {
        return stockAmount;
    }

    public void addStockAmount(){
        stockAmount++;
    }

    public void removeStockAmount(){
        stockAmount--;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }
}
