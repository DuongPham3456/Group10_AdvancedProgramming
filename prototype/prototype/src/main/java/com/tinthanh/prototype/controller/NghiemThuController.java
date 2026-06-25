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

import com.tinthanh.prototype.model.NghiemThu;
import com.tinthanh.prototype.repository.NghiemThuRepository;

@RestController
@RequestMapping("/api/nghiemthu")
@CrossOrigin("*")
public class NghiemThuController {

    private final NghiemThuRepository repository;

    public NghiemThuController(NghiemThuRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NghiemThu> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public NghiemThu getById(@PathVariable UUID id) { return find(id); }

    // 5.1.2.4 — "Quản lý lập biên bản nghiệm thu theo BM.BT.01.04"
    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PostMapping
    public NghiemThu create(@RequestBody NghiemThu item) {
        item.setId(null);
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @PutMapping("/{id}")
    public NghiemThu update(@PathVariable UUID id, @RequestBody NghiemThu d) {
        NghiemThu item = find(id);
        item.setTenThietBi(d.getTenThietBi());
        item.setSoLuong(d.getSoLuong());
        item.setTinhHinhHoatDong(d.getTinhHinhHoatDong());
        item.setGhiChu(d.getGhiChu());
        item.setDonViDeNghi(d.getDonViDeNghi());
        item.setNguoiNghiemThu(d.getNguoiNghiemThu());
        item.setNgayNghiemThu(d.getNgayNghiemThu());
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    private NghiemThu find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Biên bản không tồn tại"));
    }
}
