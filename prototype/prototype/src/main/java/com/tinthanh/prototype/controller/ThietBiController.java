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

    @Autowired
    private ThietBiRepository repository;

    @GetMapping
    public List<ThietBiLyLich> getAllThietBi() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ThietBiLyLich getThietBiById(@PathVariable UUID id) {
        return findThietBiById(id);
    }

    @GetMapping("/search")
    public List<ThietBiLyLich> searchThietBi(@RequestParam String keyword) {
        return repository.findByKeyword(keyword);
    }

    @PostMapping
    public ThietBiLyLich createThietBi(@RequestBody ThietBiLyLich thietBi) {
        return repository.save(thietBi);
    }

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
        return repository.save(thietBi);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteThietBi(@PathVariable UUID id) {
        ThietBiLyLich thietBi = findThietBiById(id);
        repository.delete(thietBi);
    }

    private ThietBiLyLich findThietBiById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"));
    }
}
