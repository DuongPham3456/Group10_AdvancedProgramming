package com.tinthanh.prototype.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.ThongSoMay;

public interface ThongSoRepository extends JpaRepository<ThongSoMay, UUID> {

    List<ThongSoMay> findByMaThietBiOrderByThoiGianGhiDesc(String maThietBi);
}
