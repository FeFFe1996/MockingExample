package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {
    private Customer customer = new Customer("Testson");

    @InjectMocks
    ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        shoppingCart = new ShoppingCart(customer.getCustommerID());
    }

    @Test
    void cannotCreateShoppingCartWithNullValues(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new ShoppingCart(null);
        });

        assertThat(e.getMessage()).isEqualTo("customer id cannot be null");
    }

    @Test
    void cannotCreateShoppingCartWithEmptyValues(){
        Customer customerTest = new Customer("", "Testson");
        String customerId = customerTest.getCustommerID();
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new ShoppingCart(customerId);
        });

        assertThat(e.getMessage()).isEqualTo("customer id cannot be empty");
    }

    @Test
    void productsCannotBeNull(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Products errorProduct = null;
            shoppingCart.addToCart(errorProduct);
        });

        assertThat(exception.getMessage()).isEqualTo("product cannot be null");
    }

    @Test
    void productShouldThrowErrorWhenProductAmountIsZeroOrLess(){
        BigDecimal price = BigDecimal.valueOf(20.0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Products("1", "Ball", -1, price);
        });

        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero");
    }

    @Test
    void addToCart(){
        Products product = new Products("1", "Ball",10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);
        assertThat(shoppingCart.getCart().size()).isEqualTo(1);
    }


    @Test
    void addOneMoreOfProduct(){
        Products product = new Products("1", "Ball",10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);
        shoppingCart.addToCart(product);

        assertThat(shoppingCart.getProductCartAmount(product.getID())).isEqualTo(2);
        System.out.println(shoppingCart.getProductCartAmount(product.getID()));
    }

    @Test
    void addProductReducesStock(){
        Products product = new Products("1", "Ball",1, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product);

        assertThat(shoppingCart.getProductStockAmount(product.getID())).isZero();
    }

    @Test
    void removeProductFromCart(){
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
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product1);
        shoppingCart.removeOneFromCart(product1.getID());

        assertThat(shoppingCart.getCart().getFirst().getCartAmount()).isEqualTo(1);
        assertThat(shoppingCart.getCart().getFirst().getStockAmount()).isEqualTo(9);
    }

    @Test
    void removeOneWillRemoveProductIfCartAmountIsZero(){
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        Products product2 = new Products("2","Disc", 5, BigDecimal.valueOf(15.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        shoppingCart.removeOneFromCart("1");
        assertThat(shoppingCart.getCart().size()).isEqualTo(1);
    }

    @Test
    void removeAllProductsFromCart(){
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        Products product2 = new Products("2", "Pipe", 10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        shoppingCart.removeAllFromCart();

        assertThat(shoppingCart.getCart().isEmpty());
    }

    @Test
    void calculateTotalPrice(){
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        Products product2 = new Products("2", "Pipe", 10, BigDecimal.valueOf(10.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        BigDecimal totalPrice = shoppingCart.calculateTotalPrice();

        assertThat(totalPrice).isEqualTo(BigDecimal.valueOf(30.0));
    }

    @Test
    void priceDiscountCannotBeLessThanZero() {
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product1);
        String productId = product1.getID();
        BigDecimal discount = BigDecimal.valueOf(-10.0);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.addDiscountToProduct(productId, discount);
        });

        assertThat(e.getMessage()).isEqualTo("percentage must be between 0 and 100");
    }

    @Test
    void priceDiscountCannotBeGreaterThanHundred() {
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        shoppingCart.addToCart(product1);
        String productId = product1.getID();
        BigDecimal discount = BigDecimal.valueOf(110.0);
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.addDiscountToProduct(productId, discount);
        });
        assertThat(e.getMessage()).isEqualTo("percentage must be between 0 and 100");
    }

    @Test
    void addDiscountShouldReturnVoidIfProductIsNull(){
        BigDecimal discountAmount = BigDecimal.valueOf(10);
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.addDiscountToProduct(null, discountAmount);
        });
        assertThat(e.getMessage()).isEqualTo("product id cannot be null");
    }

    @Test
    void calculatePriceWithDiscount(){
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20));
        shoppingCart.addToCart(product1);
        shoppingCart.addDiscountToProduct(product1.getID(), BigDecimal.valueOf(25));

        assertThat(shoppingCart.calculateTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
    }

    @Test
    void priceDiscountOnWholeCartTotalPrice() {
        Products product1 = new Products("1", "Ball", 10, BigDecimal.valueOf(20.0));
        Products product2 = new Products("2", "Pipe", 10, BigDecimal.valueOf(10.0));
        shoppingCart.addToCart(product1);
        shoppingCart.addToCart(product2);

        assertThat(shoppingCart.calculateTotalPriceWithDiscount(BigDecimal.valueOf(25))).isEqualByComparingTo(BigDecimal.valueOf(22.5));
    }

}