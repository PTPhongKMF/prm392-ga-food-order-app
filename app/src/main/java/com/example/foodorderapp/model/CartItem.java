package com.example.foodorderapp.model;

public class CartItem {
    private int cartId;
    private int userId;
    private int productId;
    private String productTitle;
    private String productPrice;

    public CartItem(int cartId, int userId, int productId, String productTitle, String productPrice) {
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
