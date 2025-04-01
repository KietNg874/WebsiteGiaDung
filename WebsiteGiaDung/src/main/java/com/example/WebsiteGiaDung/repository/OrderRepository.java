package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    // Fetch orders by status and associated user
    List<Order> findByOrderStatusAndUser(OrderStatus status, User user);
    List<Order> findByUserId(Long userId);
    // Fetch orders by associated user excluding a specific status
    List<Order> findByOrderStatusNotAndUser(OrderStatus status, User user);
}
