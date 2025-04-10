package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.OrderDetail;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT o.product FROM OrderDetail o " +
            "GROUP BY o.product " +
            "HAVING SUM(o.quantity) > 3 " +
            "ORDER BY SUM(o.quantity) DESC")
    List<SanPham> findPopularProducts();
}
