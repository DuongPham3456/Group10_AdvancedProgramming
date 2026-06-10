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
@Table(name = "thietbi_lylich")
public class ThietBiLyLich {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idThietBi;

    private String tenThietBi;

    @Column(unique = true)
    private String maSoQuanLy;

    @Column(unique = true)
    private String soSerial;

    private String modelType;
    private String nhaSanXuat;
    private Integer namSanXuat;
    private String dacDiem;
    private LocalDate ngayTiepNhan;
    private LocalDate ngayDuaVaoSuDung;
    private String phuTungKemTheo;
    private String taiLieuKemTheo;
    private String tinhTrangTiepNhan;
    private String trangThaiVanHanh;

    // Getters and Setters
    public UUID getIdThietBi() {
        return idThietBi;
    }

    public void setIdThietBi(UUID idThietBi) {
        this.idThietBi = idThietBi;
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

    public String getSoSerial() {
        return soSerial;
    }

    public void setSoSerial(String soSerial) {
        this.soSerial = soSerial;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
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

    public String getDacDiem() {
        return dacDiem;
    }

    public void setDacDiem(String dacDiem) {
        this.dacDiem = dacDiem;
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

    public String getPhuTungKemTheo() {
        return phuTungKemTheo;
    }

    public void setPhuTungKemTheo(String phuTungKemTheo) {
        this.phuTungKemTheo = phuTungKemTheo;
    }

    public String getTaiLieuKemTheo() {
        return taiLieuKemTheo;
    }

    public void setTaiLieuKemTheo(String taiLieuKemTheo) {
        this.taiLieuKemTheo = taiLieuKemTheo;
    }

    public String getTinhTrangTiepNhan() {
        return tinhTrangTiepNhan;
    }

    public void setTinhTrangTiepNhan(String tinhTrangTiepNhan) {
        this.tinhTrangTiepNhan = tinhTrangTiepNhan;
    }

    public String getTrangThaiVanHanh() {
        return trangThaiVanHanh;
    }

    public void setTrangThaiVanHanh(String trangThaiVanHanh) {
        this.trangThaiVanHanh = trangThaiVanHanh;
    }
}