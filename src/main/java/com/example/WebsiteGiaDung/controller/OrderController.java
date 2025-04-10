package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.model.*;
import com.example.WebsiteGiaDung.repository.CartRepository;
import com.example.WebsiteGiaDung.repository.OrderDetailRepository;
import com.example.WebsiteGiaDung.repository.OrderRepository;
import com.example.WebsiteGiaDung.service.CartService;
import com.example.WebsiteGiaDung.service.OrderDetailService;
import com.example.WebsiteGiaDung.service.OrderService;
import com.example.WebsiteGiaDung.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartRepository cartRepository;



    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam String customerName,
            @RequestParam String fullAddress,
            @RequestParam String phone,
            @RequestParam Long totalAmount,
            @AuthenticationPrincipal User user) {

        // Fetch the cart for the user
        Optional<Cart> optionalCart = cartRepository.findByUser(user);

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();

            // Create and save the order
            Order order = new Order();
            order.setCustomerName(customerName);
            order.setDiaChi(fullAddress); // Set the address
            order.setPhone(phone);   // Set the phone number
            order.setOrderStatus(OrderStatus.PENDING); // Set default status
            order.setAmount(totalAmount);
            order.setUser(user); // Set the user
            order.setThutien(true); // phải thu tiền khi giao hàng
            orderRepository.save(order);

            // Save each CartItem as an OrderDetail
            for (CartItem cartItem : cart.getItems()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartItem.getProduct());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetailRepository.save(orderDetail);

                // Reduce product quantity
                SanPham product = cartItem.getProduct();
                if (product.getQuality() != null && product.getQuality() >= cartItem.getQuantity()) {
                    product.setQuality(product.getQuality() - cartItem.getQuantity());
                    productService.updateProduct(product); // Save the updated product
                } else {
                    throw new IllegalStateException("Not enough quantity for product: " + product.getTenSanPham());
                }
            }

            // Clear the cart
            cart.getItems().clear();
            cartRepository.save(cart); // Persist changes to the database
        } else {
            throw new IllegalStateException("Cart not found for user!");
        }

        return "redirect:/order/success";
    }


    @GetMapping("/checkoutpayment")
    public String showCheckoutPagePayment(@RequestParam("amount") double amount, Model model, @AuthenticationPrincipal User user) {
        // Truyền amount vào model
        double totalPrice=amount;
        String formattedTotalPrice = totalPrice % 1 == 0 ? String.format("%.0f", totalPrice) : String.format("%.2f", totalPrice);
        model.addAttribute("amount", formattedTotalPrice);

        // Retrieve the cart for the logged-in user
        Optional<Cart> cart = cartRepository.findByUser(user);
        if (cart.isEmpty() || cart.get().getItems().isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            model.addAttribute("cartItems", new ArrayList<CartItem>());
        } else {
            model.addAttribute("cartItems", cart.get().getItems());
        }

        // Add user information to the model
        model.addAttribute("userPhone", user.getPhone());
        return "Cart/checkoutpayment"; // Đảm bảo đường dẫn template đúng
    }


    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, @AuthenticationPrincipal User user) {
        // Retrieve the cart for the logged-in user
        Optional<Cart> cart = cartRepository.findByUser(user);
        List<CartItem> cartItems = cartService.getCartItems(user.getUsername());

        if (cart.isEmpty() || cart.get().getItems().isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            model.addAttribute("cartItems", new ArrayList<CartItem>());
        } else {
            model.addAttribute("cartItems", cart.get().getItems());
        }
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getGia() * item.getQuantity())
                .sum();
        DecimalFormat formatter= new DecimalFormat("#,###");
        String formattedTotalPrice = totalAmount % 1 == 0 ? String.format("%.0f", totalAmount) : String.format("%.2f", totalAmount)+"VNĐ";
        model.addAttribute("totalAmount", formattedTotalPrice);
        // Add user information to the model
        model.addAttribute("userPhone", user.getPhone()); // Pass user's phone
        return "Cart/checkout"; // Ensure the path matches your template location
    }

    @GetMapping("/success")
    public String orderSuccess(Model model) {
        model.addAttribute("message", "Your order has been successfully placed!");
        return "Cart/success"; // Name of the Thymeleaf template for the success page
    }

    @PostMapping("/checkout/single")
    public String checkoutSingleProduct(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String customerName,
            @RequestParam String fullAddress,
            @RequestParam String phone,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {

        // Fetch the product directly
        Optional<SanPham> productOptional = productService.getProductById(productId);

        if (productOptional.isPresent()) {
            SanPham product = productOptional.get();
            // Validate product stock
            if (product.getQuality() == null || product.getQuality() < quantity) {
                redirectAttributes.addFlashAttribute("error", "Requested quantity exceeds available stock!");
                return "redirect:/order/checkout";
            }
            // Create a new Order
            Order order = new Order();
            order.setCustomerName(customerName); // Set customer name from form
            order.setDiaChi(fullAddress);        // Set address from form
            order.setPhone(phone);               // Set phone number from form
            order.setOrderStatus(OrderStatus.PENDING); // Default status
            order.setUser(user);                 // Link order to logged-in user
            orderRepository.save(order);

            // Create and save OrderDetail for the single product
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetailRepository.save(orderDetail);

            // Reduce product quantity
            product.setQuality(product.getQuality() - quantity);
            productService.updateProduct(product);

            // Remove the product from the user's cart
            Optional<Cart> userCart = cartRepository.findByUser(user);
            if (userCart.isPresent()) {
                Cart cart = userCart.get();
                List<CartItem> cartItems = cart.getItems();
                cartItems.removeIf(item -> item.getProduct().getIdSanPham().equals(productId));
                cartRepository.save(cart); // Persist cart changes
            }

            redirectAttributes.addFlashAttribute("success", "Checkout successful for " + product.getTenSanPham());
            return "redirect:/order/success";
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/order/checkout";
        }
    }


    @GetMapping("/checkout/single")
    public String showSingleCheckout(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Model model,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        // Fetch the product by productId
        Optional<SanPham> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            SanPham product = productOptional.get();
            // Add product and quantity to the model for display on the checkout page
            model.addAttribute("product", product);
            model.addAttribute("quantity", quantity);
            // Pre-fill phone number if available
            model.addAttribute("userPhone", user.getPhone());
            return "Cart/single-checkout"; // Ensure this matches your Thymeleaf template location
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/order/checkout"; // Redirect back to the cart or main checkout page
        }
    }
    @GetMapping("/details/{id}")
    public String viewDetails(@PathVariable Long id, Model model) {
        List<OrderDetail> details = orderDetailService.findByOrderId(id);
        model.addAttribute("orderDetails", details);
        return "Admin/order-detail-list";
    }
}