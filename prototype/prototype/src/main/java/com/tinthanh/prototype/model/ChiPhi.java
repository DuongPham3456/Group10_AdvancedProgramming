package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Chi_Phi")
public class ChiPhi {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false)
    private String maThietBi;

    private String tenThietBi;

    @Column(length = 50)
    private String loaiChiPhi;

    private Double soTien;

    private LocalDate ngayPhatSinh;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    public ChiPhi() {
        this.id = UUID.randomUUID();
        this.ngayPhatSinh = LocalDate.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getLoaiChiPhi() { return loaiChiPhi; }
    public void setLoaiChiPhi(String loaiChiPhi) { this.loaiChiPhi = loaiChiPhi; }
    public Double getSoTien() { return soTien; }
    public void setSoTien(Double soTien) { this.soTien = soTien; }
    public LocalDate getNgayPhatSinh() { return ngayPhatSinh; }
    public void setNgayPhatSinh(LocalDate ngayPhatSinh) { this.ngayPhatSinh = ngayPhatSinh; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
