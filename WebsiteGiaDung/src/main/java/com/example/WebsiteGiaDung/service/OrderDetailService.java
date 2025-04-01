package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.OrderDetail;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    // Fetch order details by order ID
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

}