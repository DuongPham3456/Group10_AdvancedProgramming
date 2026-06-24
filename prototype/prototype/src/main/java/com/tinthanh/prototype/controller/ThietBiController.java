package com.tinthanh.prototype.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.repository.ThietBiRepository;

@RestController
@RequestMapping("/api/thietbi")
@CrossOrigin("*")
public class ThietBiController {

    private final ThietBiRepository repository;

    public ThietBiController(ThietBiRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','CONG_NHAN')")
    @GetMapping
    public List<ThietBiLyLich> getAllThietBi() {
        return repository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','CONG_NHAN')")
    @GetMapping("/{id}")
    public ThietBiLyLich getThietBiById(@PathVariable UUID id) {
        return findThietBiById(id);
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','CONG_NHAN')")
    @GetMapping("/search")
    public List<ThietBiLyLich> searchThietBi(@RequestParam String keyword) {
        return repository.findByKeyword(keyword);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PostMapping
    public ThietBiLyLich createThietBi(@RequestBody ThietBiLyLich thietBi) {
        return repository.save(thietBi);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PutMapping("/{id}")
    public ThietBiLyLich updateThietBi(@PathVariable UUID id, @RequestBody ThietBiLyLich thietBiDetails) {
        ThietBiLyLich thietBi = findThietBiById(id);
        thietBi.setTenThietBi(thietBiDetails.getTenThietBi());
        thietBi.setMaSoQuanLy(thietBiDetails.getMaSoQuanLy());
        thietBi.setSoSerial(thietBiDetails.getSoSerial());
        thietBi.setModelType(thietBiDetails.getModelType());
        thietBi.setNhaSanXuat(thietBiDetails.getNhaSanXuat());
        thietBi.setNamSanXuat(thietBiDetails.getNamSanXuat());
        thietBi.setTrangThaiVanHanh(thietBiDetails.getTrangThaiVanHanh());
        thietBi.setDacDiem(thietBiDetails.getDacDiem());
        thietBi.setNgayTiepNhan(thietBiDetails.getNgayTiepNhan());
        thietBi.setNgayDuaVaoSuDung(thietBiDetails.getNgayDuaVaoSuDung());
        thietBi.setPhuTungKemTheo(thietBiDetails.getPhuTungKemTheo());
        thietBi.setTaiLieuKemTheo(thietBiDetails.getTaiLieuKemTheo());
        thietBi.setTinhTrangTiepNhan(thietBiDetails.getTinhTrangTiepNhan());
        thietBi.setTinhTrangVeSinh(thietBiDetails.getTinhTrangVeSinh());
        thietBi.setNgayVeSinhCuoi(thietBiDetails.getNgayVeSinhCuoi());
        thietBi.setNguoiQuanLyTram(thietBiDetails.getNguoiQuanLyTram());
        thietBi.setGhiChuBaoTri(thietBiDetails.getGhiChuBaoTri());
        return repository.save(thietBi);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteThietBi(@PathVariable UUID id) {
        ThietBiLyLich thietBi = findThietBiById(id);
        repository.delete(thietBi);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @DeleteMapping("/byMa/{ma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByMa(@PathVariable String ma) {
        repository.findByMaSoQuanLyIgnoreCase(ma)
            .ifPresentOrElse(repository::delete,
                () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"); });
    }

    private ThietBiLyLich findThietBiById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"));
    }
}
