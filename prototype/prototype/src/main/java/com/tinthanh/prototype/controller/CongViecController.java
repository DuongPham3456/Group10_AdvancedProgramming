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

import com.tinthanh.prototype.model.CongViec;
import com.tinthanh.prototype.repository.CongViecRepository;

@RestController
@RequestMapping("/api/congviec")
@CrossOrigin("*")
public class CongViecController {

    @Autowired
    private CongViecRepository repository;

    @GetMapping
    public List<CongViec> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public CongViec getById(@PathVariable UUID id) {
        return findById(id);
    }

    @PostMapping
    public CongViec create(@RequestBody CongViec item) {
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public CongViec update(@PathVariable UUID id, @RequestBody CongViec details) {
        CongViec item = findById(id);
        item.setTenCongViec(details.getTenCongViec());
        item.setMaThietBi(details.getMaThietBi());
        item.setNguoiPhuTrach(details.getNguoiPhuTrach());
        item.setHanHoanThanh(details.getHanHoanThanh());
        item.setTrangThai(details.getTrangThai());
        item.setDoUuTien(details.getDoUuTien());
        item.setMoTa(details.getMoTa());
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.delete(findById(id));
    }

    private CongViec findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Công việc không tồn tại"));
    }
}
