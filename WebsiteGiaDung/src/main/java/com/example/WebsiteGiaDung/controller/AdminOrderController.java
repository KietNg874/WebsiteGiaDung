package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.OrderDetail;
import com.example.WebsiteGiaDung.service.OrderDetailService;
import com.example.WebsiteGiaDung.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "Admin/order-admin-list";
    }

    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam Long id, @RequestParam OrderStatus orderStatus) {
        orderService.updateStatus(id, orderStatus);
        return "redirect:/admin/orders";
    }
}