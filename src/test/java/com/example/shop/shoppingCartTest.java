package com.example.shop;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class shoppingCartTest {
    @Mock
    private Discounts discount;

    @InjectMocks
    ShoppingCart shoppingCart;

    @Test
    void addToCart(){
        Products product = new Products("Ball", 20.0);

        shoppingCart.addToCart(product);

        assertThat(shoppingCart.getCart().size).isEqualTo(1);
    }

}