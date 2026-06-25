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

import com.tinthanh.prototype.model.DanhMucThietBi;
import com.tinthanh.prototype.repository.DanhMucRepository;

@RestController
@RequestMapping("/api/danhmuc")
@CrossOrigin("*")
public class DanhMucController {

    private final DanhMucRepository repository;

    public DanhMucController(DanhMucRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DanhMucThietBi> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public DanhMucThietBi getById(@PathVariable UUID id) { return find(id); }

    // Quản lý Trạm + Giám đốc lập danh mục thiết bị
    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','GIAM_DOC')")
    @PostMapping
    public DanhMucThietBi create(@RequestBody DanhMucThietBi item) {
        item.setId(null);
        return repository.save(item);
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','GIAM_DOC')")
    @PutMapping("/{id}")
    public DanhMucThietBi update(@PathVariable UUID id, @RequestBody DanhMucThietBi d) {
        DanhMucThietBi item = find(id);
        item.setTenThietBi(d.getTenThietBi());
        item.setKyHieu(d.getKyHieu());
        item.setSoLuong(d.getSoLuong());
        item.setXuatXu(d.getXuatXu());
        item.setThongSoKyThuat(d.getThongSoKyThuat());
        item.setNamSanXuat(d.getNamSanXuat());
        item.setGhiChu(d.getGhiChu());
        return repository.save(item);
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','GIAM_DOC')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    private DanhMucThietBi find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại"));
    }
}
