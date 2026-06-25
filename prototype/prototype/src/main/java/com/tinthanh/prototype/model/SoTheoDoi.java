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
@Table(name = "SoTheoDoi_BaoTri")
public class SoTheoDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenThietBi;

    private String kyHieu;

    @Column(columnDefinition = "TEXT")
    private String thongSoKyThuat;

    private Integer namSanXuat;

    private LocalDate ngayKiemTra;

    @Column(columnDefinition = "TEXT")
    private String tinhTrang;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    public SoTheoDoi() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getKyHieu() { return kyHieu; }
    public void setKyHieu(String kyHieu) { this.kyHieu = kyHieu; }
    public String getThongSoKyThuat() { return thongSoKyThuat; }
    public void setThongSoKyThuat(String thongSoKyThuat) { this.thongSoKyThuat = thongSoKyThuat; }
    public Integer getNamSanXuat() { return namSanXuat; }
    public void setNamSanXuat(Integer namSanXuat) { this.namSanXuat = namSanXuat; }
    public LocalDate getNgayKiemTra() { return ngayKiemTra; }
    public void setNgayKiemTra(LocalDate ngayKiemTra) { this.ngayKiemTra = ngayKiemTra; }
    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
