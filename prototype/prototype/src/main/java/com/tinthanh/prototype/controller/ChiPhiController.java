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

import com.tinthanh.prototype.model.ChiPhi;
import com.tinthanh.prototype.repository.ChiPhiRepository;

@RestController
@RequestMapping("/api/chiphi")
@CrossOrigin("*")
public class ChiPhiController {

    @Autowired
    private ChiPhiRepository repository;

    @GetMapping
    public List<ChiPhi> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ChiPhi getById(@PathVariable UUID id) {
        return findById(id);
    }

    @PostMapping
    public ChiPhi create(@RequestBody ChiPhi item) {
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ChiPhi update(@PathVariable UUID id, @RequestBody ChiPhi details) {
        ChiPhi item = findById(id);
        item.setMaThietBi(details.getMaThietBi());
        item.setTenThietBi(details.getTenThietBi());
        item.setLoaiChiPhi(details.getLoaiChiPhi());
        item.setSoTien(details.getSoTien());
        item.setNgayPhatSinh(details.getNgayPhatSinh());
        item.setMoTa(details.getMoTa());
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.delete(findById(id));
    }

    private ChiPhi findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi phí không tồn tại"));
    }
}
