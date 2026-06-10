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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tinthanh.prototype.model.ThongSoMay;
import com.tinthanh.prototype.repository.ThongSoRepository;

@RestController
@RequestMapping("/api/thongso")
@CrossOrigin("*")
public class ThongSoController {

    @Autowired
    private ThongSoRepository repository;

    @GetMapping
    public List<ThongSoMay> getAll() {
        return repository.findAll();
    }

    @GetMapping("/search")
    public List<ThongSoMay> searchByMaThietBi(@RequestParam String maThietBi) {
        return repository.findByMaThietBiOrderByThoiGianGhiDesc(maThietBi);
    }

    @PostMapping
    public ThongSoMay create(@RequestBody ThongSoMay item) {
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.delete(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
}
