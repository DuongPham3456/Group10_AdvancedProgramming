package com.tinthanh.prototype.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.DanhMucThietBi;

public interface DanhMucRepository extends JpaRepository<DanhMucThietBi, UUID> {}
