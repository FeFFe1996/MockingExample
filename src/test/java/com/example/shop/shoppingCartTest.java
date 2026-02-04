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

    @Mock
    private List<Products> products = new ArrayList<>();

    private Customer customer = new Customer("1", "Testson");

    @InjectMocks
    ShoppingCart shoppingCart;

    @Test
    void cannotCreateShoppingCartWithNullValues(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            ShoppingCart errorCart = new ShoppingCart(null);
        });

        assertThat(e.getMessage()).isEqualTo("customer id cannot be null");
    }

    @Test
    void productShouldThrowErrorWhenProductAmountIsZeroOrLess(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Products products1 = new Products("1", "Ball", -1, BigDecimal.valueOf(20.0));
        });

        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero");
    }

    @Test
    void addToCart(){
        shoppingCart = new ShoppingCart(customer.getCustommerID());
        Products product = new Products("1", "Ball",10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);
        assertThat(shoppingCart.getCart().size()).isEqualTo(1);
    }


    @Test
    void addOneMoreOfProduct(){
        shoppingCart = new ShoppingCart(customer.getCustommerID());
        Products product = new Products("1", "Ball",10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);
        shoppingCart.addToCart(product);

        assertThat(shoppingCart.getProductCartAmount(product.getID())).isEqualTo(2);
        System.out.println(shoppingCart.getProductCartAmount(product.getID()));
    }

    @Test
    void addProductReducesStock(){
        shoppingCart = new ShoppingCart(customer.getCustommerID());
        Products product = new Products("1", "Ball",1, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);

        assertThat(shoppingCart.getProductStockAmount(product.getID())).isEqualTo(0);
    }

    @Test
    void removeProductFromCart(){
        shoppingCart = new ShoppingCart(customer.getCustommerID());
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        Products product2 = new Products("2","Disc", 5, BigDecimal.valueOf(15.0));
        List<Products> testCart = new ArrayList<>();
        testCart.add(product2);
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        shoppingCart.removeFromCart("1");

        assertThat(shoppingCart.getCart().getFirst().getProductName()).isEqualTo(testCart.getFirst().getProductName());
    }

    @Test
    void removeOneAmountFromProduct(){
        shoppingCart = new ShoppingCart(customer.getCustommerID());
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product1);
        shoppingCart.removeOneFromCart(product1.getID());

        assertThat(shoppingCart.getCart().getFirst().getCartAmount()).isEqualTo(1);
        assertThat(shoppingCart.getCart().getFirst().getStockAmount()).isEqualTo(9);
    }

}