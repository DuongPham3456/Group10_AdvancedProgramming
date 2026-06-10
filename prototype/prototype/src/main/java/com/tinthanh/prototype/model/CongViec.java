package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cong_Viec")
public class CongViec {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false)
    private String tenCongViec;

    private String maThietBi;

    private String nguoiPhuTrach;

    private LocalDate hanHoanThanh;

    @Column(length = 50)
    private String trangThai;

    @Column(length = 20)
    private String doUuTien;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    public CongViec() {
        this.id = UUID.randomUUID();
        this.trangThai = "Mới";
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenCongViec() { return tenCongViec; }
    public void setTenCongViec(String tenCongViec) { this.tenCongViec = tenCongViec; }
    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }
    public String getNguoiPhuTrach() { return nguoiPhuTrach; }
    public void setNguoiPhuTrach(String nguoiPhuTrach) { this.nguoiPhuTrach = nguoiPhuTrach; }
    public LocalDate getHanHoanThanh() { return hanHoanThanh; }
    public void setHanHoanThanh(LocalDate hanHoanThanh) { this.hanHoanThanh = hanHoanThanh; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getDoUuTien() { return doUuTien; }
    public void setDoUuTien(String doUuTien) { this.doUuTien = doUuTien; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
