package com.example.WebsiteGiaDung.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LoaiSanPham")
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLoaiSanPham;

    @OneToMany(mappedBy = "loaiSanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent recursive serialization
    private List<SanPham> sanPhams;

    @Column(name = "TenLoaiSanPham", length = 50)
    @NotBlank(message = "Ten loai san pham is required")
    private String TenLoaiSanPham;

    // Getters and setters
    public Long getIdLoaiSanPham() {
        return idLoaiSanPham;
    }

    public void setIdLoaiSanPham(Long idLoaiSanPham) {
        this.idLoaiSanPham = idLoaiSanPham;
    }

    public String getTenLoaiSanPham() {
        return TenLoaiSanPham;
    }

    public void setTenLoaiSanPham(String TenLoaiSanPham) {
        this.TenLoaiSanPham = TenLoaiSanPham;
    }

}
