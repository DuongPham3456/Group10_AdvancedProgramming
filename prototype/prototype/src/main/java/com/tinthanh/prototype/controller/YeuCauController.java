package com.tinthanh.prototype.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<YeuCauBaoTri> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public YeuCauBaoTri getById(@PathVariable UUID id) {
        return findById(id);
    }

    @PostMapping
    public YeuCauBaoTri create(@RequestBody YeuCauBaoTri item) {
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public YeuCauBaoTri update(@PathVariable UUID id, @RequestBody YeuCauBaoTri details) {
        YeuCauBaoTri item = findById(id);
        item.setTenThietBi(details.getTenThietBi());
        item.setMaThietBi(details.getMaThietBi());
        item.setMoTaSuCo(details.getMoTaSuCo());
        item.setMucDo(details.getMucDo());
        item.setTrangThai(details.getTrangThai());
        item.setNguoiYeuCau(details.getNguoiYeuCau());
        item.setNgayYeuCau(details.getNgayYeuCau());
        return repository.save(item);
    }

    @PutMapping("/{id}/duyet")
    public YeuCauBaoTri approve(@PathVariable UUID id) {
        YeuCauBaoTri item = findById(id);
        item.setTrangThai("Đã duyệt");
        return repository.save(item);
    }

    @PutMapping("/{id}/tuchoi")
    public YeuCauBaoTri reject(@PathVariable UUID id) {
        YeuCauBaoTri item = findById(id);
        item.setTrangThai("Từ chối");
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.delete(findById(id));
    }

    private YeuCauBaoTri findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yêu cầu không tồn tại"));
    }
}
