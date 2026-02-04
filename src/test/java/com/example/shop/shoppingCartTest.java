package com.example.shop;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class shoppingCartTest {
    @Mock
    private Discounts discount;

    private List<Products> products = new ArrayList<>();

    @InjectMocks
    ShoppingCart shoppingCart = new ShoppingCart(discount, products);

    @Test
    void addToCart(){

        Products product = new Products("1", "Ball", BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);
        assertThat(shoppingCart.getCart().size()).isEqualTo(1);
    }

    @Test
    void removeProductFromCart(){
        Products product1 = new Products("1", "Ball", BigDecimal.valueOf(20.0));
        Products product2 = new Products("2","Disc", BigDecimal.valueOf(15.0));
        List<Products> testCart = new ArrayList<>();
        testCart.add(product2);
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        shoppingCart.removeFromCart("1");

        assertThat(shoppingCart.getCart().getFirst().getProductName()).isEqualTo(testCart.getFirst().getProductName());
    }

}