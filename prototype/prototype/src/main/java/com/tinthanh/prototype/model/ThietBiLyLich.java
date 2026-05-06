package com.tinthanh.prototype.model;

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

    public String getTrangThaiVanHanh() {
        return trangThaiVanHanh;
    }

    public void setTrangThaiVanHanh(String trangThaiVanHanh) {
        this.trangThaiVanHanh = trangThaiVanHanh;
    }
}