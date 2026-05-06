package com.tinthanh.prototype.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ThietBi_LyLich")
public class ThietBiLyLich {

    @Id
    @Column(name = "ID_ThietBi", length = 36)
    private UUID id;

    @Column(name = "TenThietBi", nullable = false)
    private String tenThietBi;

    @Column(name = "MaSoQuanLy", nullable = false, unique = true)
    private String maSoQuanLy;

    @Column(name = "ModelType")
    private String modelType;

    @Column(name = "SoSerial", nullable = false, unique = true)
    private String soSerial;

    @Column(name = "NhaSanXuat")
    private String nhaSanXuat;

    @Column(name = "NamSanXuat")
    private Integer namSanXuat;

    // Đã đổi sang LocalDate và xóa bỏ @Temporal
    @Column(name = "NgayTiepNhan")
    private LocalDate ngayTiepNhan;

    // Đã đổi sang LocalDate và xóa bỏ @Temporal
    @Column(name = "NgayDuaVaoSuDung")
    private LocalDate ngayDuaVaoSuDung;

    @Column(name = "DacDiemKyThuat", columnDefinition = "TEXT")
    private String dacDiemKyThuat;

    @Column(name = "TrangThaiVanHanh", length = 50)
    private String trangThaiVanHanh;

    public ThietBiLyLich() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    public String getMaSoQuanLy() {
        return maSoQuanLy;
    }

    public void setMaSoQuanLy(String maSoQuanLy) {
        this.maSoQuanLy = maSoQuanLy;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getSoSerial() {
        return soSerial;
    }

    public void setSoSerial(String soSerial) {
        this.soSerial = soSerial;
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = nhaSanXuat;
    }

    public Integer getNamSanXuat() {
        return namSanXuat;
    }

    public void setNamSanXuat(Integer namSanXuat) {
        this.namSanXuat = namSanXuat;
    }

    public LocalDate getNgayTiepNhan() {
        return ngayTiepNhan;
    }

    public void setNgayTiepNhan(LocalDate ngayTiepNhan) {
        this.ngayTiepNhan = ngayTiepNhan;
    }

    public LocalDate getNgayDuaVaoSuDung() {
        return ngayDuaVaoSuDung;
    }

    public void setNgayDuaVaoSuDung(LocalDate ngayDuaVaoSuDung) {
        this.ngayDuaVaoSuDung = ngayDuaVaoSuDung;
    }

    public String getDacDiemKyThuat() {
        return dacDiemKyThuat;
    }

    public void setDacDiemKyThuat(String dacDiemKyThuat) {
        this.dacDiemKyThuat = dacDiemKyThuat;
    }

    public String getTrangThaiVanHanh() {
        return trangThaiVanHanh;
    }

    public void setTrangThaiVanHanh(String trangThaiVanHanh) {
        this.trangThaiVanHanh = trangThaiVanHanh;
    }
}