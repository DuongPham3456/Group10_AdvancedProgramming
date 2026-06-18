package com.tinthanh.prototype.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.tinthanh.prototype.model.YeuCauBaoTri;
import com.tinthanh.prototype.repository.YeuCauRepository;

@RestController
@RequestMapping("/api/yeucau")
@CrossOrigin("*")
public class YeuCauController {

    @Autowired
    private YeuCauRepository repository;

    @GetMapping
    public List<YeuCauBaoTri> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public YeuCauBaoTri getById(@PathVariable UUID id) { return find(id); }

    @PreAuthorize("hasAuthority('CONG_NHAN')")
    @PostMapping
    public YeuCauBaoTri create(@RequestBody YeuCauBaoTri item) { return repository.save(item); }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','QUAN_LY_TRAM')")
    @PutMapping("/{id}/lapkehoach")
    public YeuCauBaoTri submitPlan(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Chờ duyệt");
        item.setPhuongAnSuaChua(body.get("phuongAnSuaChua"));
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('GIAM_DOC')")
    @PutMapping("/{id}/duyet")
    public YeuCauBaoTri approve(@PathVariable UUID id) {
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Đang sửa chữa");
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('GIAM_DOC')")
    @PutMapping("/{id}/tuchoi")
    public YeuCauBaoTri reject(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Từ chối");
        item.setLyDoTuChoi(body.get("lyDoTuChoi"));
        return repository.save(item);
    }

    @PreAuthorize("hasAnyAuthority('CONG_NHAN','QUAN_LY_TRAM')")
    @PutMapping("/{id}/nghiemthu")
    public YeuCauBaoTri accept(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Hoàn thành");
        item.setKetQuaSuaChua(body.get("ketQuaSuaChua"));
        item.setNguoiNghiemThu(body.get("nguoiNghiemThu"));
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('CONG_NHAN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    private YeuCauBaoTri find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sự cố không tồn tại"));
    }
}
