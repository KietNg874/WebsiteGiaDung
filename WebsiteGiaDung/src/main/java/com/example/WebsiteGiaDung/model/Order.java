package com.example.WebsiteGiaDung.model;

import com.example.WebsiteGiaDung.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String DiaChi;
    private String phone; // New field added for phone number
    private Long amount;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING; // Default status
    private boolean thutien;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column for user
    private User user; // Added relationship to User

}
