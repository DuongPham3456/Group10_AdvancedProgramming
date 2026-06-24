package com.tinthanh.prototype.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinthanh.prototype.model.KeHoachBaoTri;

public interface KeHoachRepository extends JpaRepository<KeHoachBaoTri, UUID> {
	void deleteAllByMaThietBiIgnoreCase(String ma);
}
