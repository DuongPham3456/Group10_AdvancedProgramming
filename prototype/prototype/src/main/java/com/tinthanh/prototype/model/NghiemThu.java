package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NghiemThu_BaoTri")
public class NghiemThu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenThietBi;

    private Integer soLuong;

    @Column(columnDefinition = "TEXT")
    private String tinhHinhHoatDong;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    private String donViDeNghi;

    private String nguoiNghiemThu;

    private LocalDate ngayNghiemThu;

    public NghiemThu() {
        this.soLuong = 1;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public String getTinhHinhHoatDong() { return tinhHinhHoatDong; }
    public void setTinhHinhHoatDong(String tinhHinhHoatDong) { this.tinhHinhHoatDong = tinhHinhHoatDong; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getDonViDeNghi() { return donViDeNghi; }
    public void setDonViDeNghi(String donViDeNghi) { this.donViDeNghi = donViDeNghi; }
    public String getNguoiNghiemThu() { return nguoiNghiemThu; }
    public void setNguoiNghiemThu(String nguoiNghiemThu) { this.nguoiNghiemThu = nguoiNghiemThu; }
    public LocalDate getNgayNghiemThu() { return ngayNghiemThu; }
    public void setNgayNghiemThu(LocalDate ngayNghiemThu) { this.ngayNghiemThu = ngayNghiemThu; }
}
