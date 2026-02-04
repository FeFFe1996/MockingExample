package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Products> cart;
    private Discounts discounts;

    public ShoppingCart(Discounts discounts, List<Products> cart){
        this.cart = cart;
        this.discounts = discounts;
    }

    public void addToCart(Products products){
        cart.add(products);
    }

    public List<Products> getCart() {
        return cart.stream().toList();
    }
}
