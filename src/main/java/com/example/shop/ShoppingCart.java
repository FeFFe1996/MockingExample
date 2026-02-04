package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Products> cart;
    private Discounts discounts;

    public ShoppingCart(List<Products> cart){
        if (cart == null)
            throw new IllegalArgumentException("Discounts or cart cannot be null");
        this.cart = cart;
    }

    public void addToCart(Products products){
        cart.add(products);
    }

    public List<Products> getCart() {
        return cart.stream().toList();
    }

    public void removeFromCart(String id){
        cart.removeIf(products -> products.getID().equals(id));
    }
}
