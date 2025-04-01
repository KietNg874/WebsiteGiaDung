package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.repository.OrderRepository;
import com.example.WebsiteGiaDung.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserDetailController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/user/details")
    public String userDetails(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        var pastOrders = orderRepository.findByOrderStatusAndUser(OrderStatus.DELIVERED, user);
        var ongoingOrders = orderRepository.findByOrderStatusNotAndUser(OrderStatus.DELIVERED, user);

        model.addAttribute("user", user);
        model.addAttribute("pastOrders", pastOrders);
        model.addAttribute("ongoingOrders", ongoingOrders);
        return "Users/user-detail";
    }


    @PostMapping("/user/order/confirm")
    public String confirmOrder(@RequestParam("orderId") Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() == OrderStatus.SHIPPED) {
            order.setOrderStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }
        return "redirect:/user/details";
    }
}
