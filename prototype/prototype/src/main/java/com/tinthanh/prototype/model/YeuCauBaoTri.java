package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "YeuCau_BaoTri")
public class YeuCauBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenThietBi;

    @Column(nullable = false)
    private String maThietBi;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String moTaSuCo;

    @Column(length = 50)
    private String mucDo;

    @Column(length = 50)
    private String trangThai;

    private String nguoiYeuCau;

    private LocalDate ngayYeuCau;

    private LocalDateTime ngayTao;

    @Column(columnDefinition = "TEXT")
    private String phuongAnSuaChua;

    @Column(columnDefinition = "TEXT")
    private String lyDoTuChoi;

    private String donViThueNgoai;

    private LocalDate ngaySuaChua;

    @Column(columnDefinition = "TEXT")
    private String ketQuaSuaChua;

    private String nguoiNghiemThu;

    public YeuCauBaoTri() {
        this.trangThai = "Phát hiện";
        this.ngayYeuCau = LocalDate.now();
    }

    @PrePersist
    void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }
    public String getMoTaSuCo() { return moTaSuCo; }
    public void setMoTaSuCo(String moTaSuCo) { this.moTaSuCo = moTaSuCo; }
    public String getMucDo() { return mucDo; }
    public void setMucDo(String mucDo) { this.mucDo = mucDo; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getNguoiYeuCau() { return nguoiYeuCau; }
    public void setNguoiYeuCau(String nguoiYeuCau) { this.nguoiYeuCau = nguoiYeuCau; }
    public LocalDate getNgayYeuCau() { return ngayYeuCau; }
    public void setNgayYeuCau(LocalDate ngayYeuCau) { this.ngayYeuCau = ngayYeuCau; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public String getPhuongAnSuaChua() { return phuongAnSuaChua; }
    public void setPhuongAnSuaChua(String phuongAnSuaChua) { this.phuongAnSuaChua = phuongAnSuaChua; }
    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }
    public String getDonViThueNgoai() { return donViThueNgoai; }
    public void setDonViThueNgoai(String donViThueNgoai) { this.donViThueNgoai = donViThueNgoai; }
    public LocalDate getNgaySuaChua() { return ngaySuaChua; }
    public void setNgaySuaChua(LocalDate ngaySuaChua) { this.ngaySuaChua = ngaySuaChua; }
    public String getKetQuaSuaChua() { return ketQuaSuaChua; }
    public void setKetQuaSuaChua(String ketQuaSuaChua) { this.ketQuaSuaChua = ketQuaSuaChua; }
    public String getNguoiNghiemThu() { return nguoiNghiemThu; }
    public void setNguoiNghiemThu(String nguoiNghiemThu) { this.nguoiNghiemThu = nguoiNghiemThu; }
}
