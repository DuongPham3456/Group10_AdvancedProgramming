package com.tinthanh.prototype.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.SoTheoDoi;

public interface SoTheoDoiRepository extends JpaRepository<SoTheoDoi, UUID> {}
