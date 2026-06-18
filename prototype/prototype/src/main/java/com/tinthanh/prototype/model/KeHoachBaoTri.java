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
@Table(name = "KeHoach_BaoTri")
public class KeHoachBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenKeHoach;

    @Column(nullable = false)
    private String maThietBi;

    private String tenThietBi;

    @Column(length = 50)
    private String loaiBaoTri;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private Integer chuKyNgay;

    @Column(length = 50)
    private String trangThai;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @Column(columnDefinition = "TEXT")
    private String lyDoTuChoi;

    private String donViThueNgoai;

    private String nguoiThucHien;

    private LocalDate ngayThucHienThucTe;

    @Column(columnDefinition = "TEXT")
    private String ketQuaBaoDuong;

    public KeHoachBaoTri() {
        this.trangThai = "Chờ duyệt";
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenKeHoach() { return tenKeHoach; }
    public void setTenKeHoach(String tenKeHoach) { this.tenKeHoach = tenKeHoach; }
    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getLoaiBaoTri() { return loaiBaoTri; }
    public void setLoaiBaoTri(String loaiBaoTri) { this.loaiBaoTri = loaiBaoTri; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public Integer getChuKyNgay() { return chuKyNgay; }
    public void setChuKyNgay(Integer chuKyNgay) { this.chuKyNgay = chuKyNgay; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }
    public String getDonViThueNgoai() { return donViThueNgoai; }
    public void setDonViThueNgoai(String donViThueNgoai) { this.donViThueNgoai = donViThueNgoai; }
    public String getNguoiThucHien() { return nguoiThucHien; }
    public void setNguoiThucHien(String nguoiThucHien) { this.nguoiThucHien = nguoiThucHien; }
    public LocalDate getNgayThucHienThucTe() { return ngayThucHienThucTe; }
    public void setNgayThucHienThucTe(LocalDate ngayThucHienThucTe) { this.ngayThucHienThucTe = ngayThucHienThucTe; }
    public String getKetQuaBaoDuong() { return ketQuaBaoDuong; }
    public void setKetQuaBaoDuong(String ketQuaBaoDuong) { this.ketQuaBaoDuong = ketQuaBaoDuong; }
}
