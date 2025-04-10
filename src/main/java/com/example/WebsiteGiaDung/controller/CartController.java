package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.CartItem;
import com.example.WebsiteGiaDung.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String showCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        List<CartItem> cartItems = cartService.getCartItems(username);

        if (cartItems == null) {
            cartItems = Collections.emptyList();
        }
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getGia() * item.getQuantity())
                .sum();
        DecimalFormat formatter= new DecimalFormat("#,###");
        String formattedTotalPrice = totalPrice % 1 == 0 ? String.format("%.0f", totalPrice) : String.format("%.2f", totalPrice);
        model.addAttribute("totalPrice", formattedTotalPrice);
        model.addAttribute("cartItems", cartItems);
        return "Cart/cart"; // Ensure this matches the actual template path
    }

    @PostMapping("/product")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            Principal principal) {
        // Check if the user is authenticated
        if (principal == null) {
            // Redirect to the login page if the user is not authenticated
            return "redirect:/login";
        }

        // If authenticated, proceed to add the product to the cart
        String username = principal.getName();
        cartService.addToCart(username, productId, quantity);
        return "redirect:/cart"; // Redirect to the cart page after adding
    }
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, Principal principal) {
        String username = principal.getName();
        cartService.removeFromCart(productId, username);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(Principal principal) {
        String username = principal.getName();
        cartService.clearCart(username);
        return "redirect:/cart";
    }

    @PostMapping("/quantity")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") int quantity,
                                 Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        if (quantity < 1) {
            return "redirect:/cart"; // Ensure quantity is valid
        }

        String username = principal.getName();
        cartService.updateQuantity(username, productId, quantity);

        return "redirect:/cart"; // Redirect back to the cart page after updating
    }
}