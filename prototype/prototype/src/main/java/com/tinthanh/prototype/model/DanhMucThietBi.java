package com.tinthanh.prototype.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// BM.BT.01.02 — Danh mục thiết bị, máy móc (catalog list, not the full lý lịch)
@Entity
@Table(name = "DanhMuc_ThietBi")
public class DanhMucThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenThietBi;

    private String kyHieu;

    private Integer soLuong;

    private String xuatXu;

    @Column(columnDefinition = "TEXT")
    private String thongSoKyThuat;

    private Integer namSanXuat;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    public DanhMucThietBi() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getKyHieu() { return kyHieu; }
    public void setKyHieu(String kyHieu) { this.kyHieu = kyHieu; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }
    public String getThongSoKyThuat() { return thongSoKyThuat; }
    public void setThongSoKyThuat(String thongSoKyThuat) { this.thongSoKyThuat = thongSoKyThuat; }
    public Integer getNamSanXuat() { return namSanXuat; }
    public void setNamSanXuat(Integer namSanXuat) { this.namSanXuat = namSanXuat; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
