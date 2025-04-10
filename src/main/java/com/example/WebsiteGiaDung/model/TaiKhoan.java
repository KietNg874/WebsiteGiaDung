package com.example.WebsiteGiaDung.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccount;

    @Column(name = "username", length = 50)
    @NotBlank(message = "Username is required")
    private String username;
    @Column(name = "password", length = 250)
    @NotBlank(message = "Password is required")
    private String password;
}
