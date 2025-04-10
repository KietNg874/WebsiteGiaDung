package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.Cart;
import com.example.WebsiteGiaDung.model.CartItem;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.repository.CartRepository;
import com.example.WebsiteGiaDung.repository.ProductRepository;
import com.example.WebsiteGiaDung.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    public void addToCart(String username, Long productId, int quantity) {
        // Fetch the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the product
        SanPham product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Fetch or create a cart for the user
        Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));

        // Check if the product already exists in the cart
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getIdSanPham().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // If the product exists, update the quantity
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // If the product doesn't exist, add a new item
            cart.addItem(new CartItem(product, quantity));
        }

        // Save the updated cart
        cartRepository.save(cart);
    }


    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    public List<CartItem> getCartItems(String username) {
        Cart cart = getOrCreateCart(username);
        return cart.getItems() != null ? cart.getItems() : Collections.emptyList();
    }

    public void removeFromCart(Long productId, String username) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().removeIf(item -> item.getProduct().getIdSanPham().equals(productId));
        cartRepository.save(cart); // Save changes to the database
    }

    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void updateQuantity(String username, Long productId, int quantity) {
        Cart cart = getOrCreateCart(username);

        Optional<CartItem> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getIdSanPham().equals(productId))
                .findFirst();

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            cartItem.setQuantity(quantity); // Update the quantity
            cartRepository.save(cart); // Save changes to the database
        }
    }

}
