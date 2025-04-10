package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.model.CartItem;
import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.OrderDetail;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.repository.OrderDetailRepository;
import com.example.WebsiteGiaDung.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;  // Assuming you have a CartService

    public void saveOrder(String customerName, User user, List<CartItem> cart,String diachi) {
        // Create and save the Order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setUser(user); // Set the User entity
        order.setDiaChi(diachi);
        Order savedOrder = orderRepository.save(order);

        // Save OrderDetails
        for (CartItem cartItem : cart) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetailRepository.save(orderDetail);
        }
    }


    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Fetch all orders
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // Update order status
    public void updateStatus(Long id, OrderStatus orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }
}
