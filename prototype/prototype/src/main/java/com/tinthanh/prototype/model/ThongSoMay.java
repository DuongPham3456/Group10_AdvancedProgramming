package com.tinthanh.prototype.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ThongSo_May")
public class ThongSoMay {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false)
    private String maThietBi;

    private String tenThietBi;

    @Column(nullable = false)
    private String tenThongSo;

    private Double giaTri;

    private String donVi;

    private Integer gioMay;

    private LocalDateTime thoiGianGhi;

    public ThongSoMay() {
        this.id = UUID.randomUUID();
    }

    @PrePersist
    void onCreate() {
        if (thoiGianGhi == null) thoiGianGhi = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getTenThongSo() { return tenThongSo; }
    public void setTenThongSo(String tenThongSo) { this.tenThongSo = tenThongSo; }
    public Double getGiaTri() { return giaTri; }
    public void setGiaTri(Double giaTri) { this.giaTri = giaTri; }
    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
    public Integer getGioMay() { return gioMay; }
    public void setGioMay(Integer gioMay) { this.gioMay = gioMay; }
    public LocalDateTime getThoiGianGhi() { return thoiGianGhi; }
    public void setThoiGianGhi(LocalDateTime thoiGianGhi) { this.thoiGianGhi = thoiGianGhi; }
}
