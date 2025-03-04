package com.example.group1_petfood.models;

public class CartItem {
    private Cart cart;
    private Product product;

    public CartItem(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Tính tổng giá của một mục trong giỏ hàng (đã bao gồm giảm giá)
     * @return Tổng giá của một mục
     */
    public double getTotalPrice() {
        return product.getPrice() * 0.8 * cart.getQuantity();
    }
}
