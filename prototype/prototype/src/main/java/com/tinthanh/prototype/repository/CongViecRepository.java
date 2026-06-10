package com.tinthanh.prototype.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.CongViec;

public interface CongViecRepository extends JpaRepository<CongViec, UUID> {
}
