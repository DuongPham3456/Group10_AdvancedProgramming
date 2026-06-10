package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "YeuCau_BaoTri")
public class YeuCauBaoTri {

    @Id
    @Column(length = 36)
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

    public YeuCauBaoTri() {
        this.id = UUID.randomUUID();
        this.trangThai = "Chờ duyệt";
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
}
