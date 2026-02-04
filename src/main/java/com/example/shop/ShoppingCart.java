package com.example.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingCart {
    private String cartId;
    private String customerID;
    private List<Products> cart;
    private Discounts discounts;


    public ShoppingCart(String customerID){
        if (customerID == null)
            throw new IllegalArgumentException("customer id cannot be null");
        this.customerID = customerID;
        this.cartId = UUID.randomUUID().toString();
        this.cart = new ArrayList<>();
    }

    public void addToCart(Products products){
        if (products == null) {
            throw new IllegalArgumentException("product cannot be null");
        }

        if (cart.stream().filter(c -> c.getID().equals(products.getID())).findAny().isEmpty()) {
            cart.add(products);
            products.addOneAmount();
            System.out.println("Added product " + products.getID() + " with name: " + products.getProductName());
        }
        products.addOneAmount();
    }

    public int getProductAmount(String productID){
        return 0;
    }

    public List<Products> getCart() {
        return cart.stream().toList();
    }

    public void removeFromCart(String id){
        cart.removeIf(products -> products.getID().equals(id));
    }


}
