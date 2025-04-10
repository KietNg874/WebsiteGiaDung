package com.example.WebsiteGiaDung.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SanPham product;

    private int quantity;

    // Constructors
    public CartItem() {}

    public CartItem(SanPham product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SanPham getProduct() {
        return product;
    }

    public void setProduct(SanPham product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Equals and HashCode (useful for collections)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }

    public boolean isEmpty() {
        return this.quantity <= 0;
    }
}
