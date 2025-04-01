package com.example.WebsiteGiaDung.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SanPham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSanPham;

    @Column(name = "TenSanPham", length = 500)
    @NotBlank(message = "Ten san pham is required")
    private String TenSanPham;

    @Column(name = "Gia")
    @Positive(message = "Gia must be greater than 0")
    @NotNull(message = "Gia is required")
    private double Gia;

    @Column(name = "MoTa",length = 5000)
    @NotBlank(message = "Mo ta is required")
    private String MoTa;

    @ManyToOne
    @JoinColumn(name = "loai_san_pham")
    private LoaiSanPham loaiSanPham;

    // Fields to store file paths of the images instead of MultipartFile objects
    @Column(name = "Img1")
    private String img1Path;

    @Column(name = "Img2")
    private String img2Path;

    @Column(name = "Img3")
    private String img3Path;

    @Column(name = "Img4")
    private String img4Path;

    @Column(name = "Img5")
    private String img5Path;

    // Getters and Setters for image path fields
    public String getImg1Path() {
        return img1Path;
    }

    public void setImg1Path(String img1Path) {
        this.img1Path = img1Path;
    }

    public String getImg2Path() {
        return img2Path;
    }

    public void setImg2Path(String img2Path) {
        this.img2Path = img2Path;
    }

    public String getImg3Path() {
        return img3Path;
    }

    public void setImg3Path(String img3Path) {
        this.img3Path = img3Path;
    }

    public String getImg4Path() {
        return img4Path;
    }

    public void setImg4Path(String img4Path) {
        this.img4Path = img4Path;
    }

    public String getImg5Path() {
        return img5Path;
    }

    public void setImg5Path(String img5Path) {
        this.img5Path = img5Path;
    }
    @Column(name = "Quality")
    @NotNull(message = "Quality is required")
    @Min(value = 0, message = "Quality cannot be negative")
    private Long quality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // Getter and Setter
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    @Override
    public String toString() {
        return "SanPham{" +
                "idSanPham=" + idSanPham +
                ", TenSanPham='" + TenSanPham + '\'' +
                ", Gia=" + Gia +
                ", MoTa='" + MoTa + '\'' +
                ", img1Path='" + img1Path + '\'' +
                '}';
    }

    public SanPham(Long idSanPham) {
        this.idSanPham = idSanPham;
    }

}
