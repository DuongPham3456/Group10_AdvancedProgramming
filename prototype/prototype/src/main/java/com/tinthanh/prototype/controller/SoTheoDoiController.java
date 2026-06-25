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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tinthanh.prototype.model.SoTheoDoi;
import com.tinthanh.prototype.repository.SoTheoDoiRepository;

@RestController
@RequestMapping("/api/sotheodoi")
@CrossOrigin("*")
public class SoTheoDoiController {

    private final SoTheoDoiRepository repository;

    public SoTheoDoiController(SoTheoDoiRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SoTheoDoi> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public SoTheoDoi getById(@PathVariable UUID id) { return find(id); }

    // 5.1.2.5 — "Quản lý Trạm ghi vào Sổ theo dõi bảo dưỡng BM.BT.01.05"
    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PostMapping
    public SoTheoDoi create(@RequestBody SoTheoDoi item) {
        item.setId(null);
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PutMapping("/{id}")
    public SoTheoDoi update(@PathVariable UUID id, @RequestBody SoTheoDoi d) {
        SoTheoDoi item = find(id);
        item.setTenThietBi(d.getTenThietBi());
        item.setKyHieu(d.getKyHieu());
        item.setThongSoKyThuat(d.getThongSoKyThuat());
        item.setNamSanXuat(d.getNamSanXuat());
        item.setNgayKiemTra(d.getNgayKiemTra());
        item.setTinhTrang(d.getTinhTrang());
        item.setGhiChu(d.getGhiChu());
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    private SoTheoDoi find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại"));
    }
}
