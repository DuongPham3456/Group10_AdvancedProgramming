package com.tinthanh.prototype.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tinthanh.prototype.repository.ChiPhiRepository;
import com.tinthanh.prototype.repository.CongViecRepository;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.YeuCauRepository;

@RestController
@RequestMapping("/api/baocao")
@CrossOrigin("*")
public class BaoCaoController {

    @Autowired private ThietBiRepository thietBiRepo;
    @Autowired private YeuCauRepository yeuCauRepo;
    @Autowired private KeHoachRepository keHoachRepo;
    @Autowired private CongViecRepository congViecRepo;
    @Autowired private ChiPhiRepository chiPhiRepo;

    @GetMapping("/tonghop")
    public Map<String, Object> getTongHop() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("tongThietBi", thietBiRepo.count());
        stats.put("tongYeuCau", yeuCauRepo.count());
        stats.put("tongKeHoach", keHoachRepo.count());
        stats.put("tongCongViec", congViecRepo.count());
        stats.put("tongChiPhi", chiPhiRepo.findAll().stream()
                .mapToDouble(c -> c.getSoTien() != null ? c.getSoTien() : 0).sum());
        stats.put("thietBiDangHoatDong", thietBiRepo.findAll().stream()
                .filter(t -> "Đang hoạt động".equals(t.getTrangThaiVanHanh())).count());
        stats.put("thietBiBaoDuong", thietBiRepo.findAll().stream()
                .filter(t -> "Bảo dưỡng".equals(t.getTrangThaiVanHanh())).count());
        stats.put("thietBiSuCo", thietBiRepo.findAll().stream()
                .filter(t -> "Sự cố".equals(t.getTrangThaiVanHanh())).count());
        stats.put("yeuCauChoDuyet", yeuCauRepo.findAll().stream()
                .filter(y -> "Chờ duyệt".equals(y.getTrangThai())).count());
        return stats;
    }
}
