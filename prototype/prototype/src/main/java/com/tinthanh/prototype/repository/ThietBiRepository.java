package com.tinthanh.prototype.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tinthanh.prototype.model.ThietBiLyLich;

public interface ThietBiRepository extends JpaRepository<ThietBiLyLich, UUID> {

    @Query("""
           SELECT t FROM ThietBiLyLich t
           WHERE LOWER(t.tenThietBi) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.maSoQuanLy) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.soSerial) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.modelType) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.nhaSanXuat) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    List<ThietBiLyLich> findByKeyword(@Param("keyword") String keyword);

    java.util.Optional<ThietBiLyLich> findByMaSoQuanLyIgnoreCase(String ma);
}