package com.tinthanh.prototype.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.YeuCauBaoTri;

public interface YeuCauRepository extends JpaRepository<YeuCauBaoTri, UUID> {}
