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

import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.repository.KeHoachRepository;

@RestController
@RequestMapping("/api/kehoach")
@CrossOrigin("*")
public class KeHoachController {

    @Autowired
    private KeHoachRepository repository;

    @GetMapping
    public List<KeHoachBaoTri> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public KeHoachBaoTri getById(@PathVariable UUID id) {
        return findById(id);
    }

    @PostMapping
    public KeHoachBaoTri create(@RequestBody KeHoachBaoTri item) {
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public KeHoachBaoTri update(@PathVariable UUID id, @RequestBody KeHoachBaoTri details) {
        KeHoachBaoTri item = findById(id);
        item.setTenKeHoach(details.getTenKeHoach());
        item.setMaThietBi(details.getMaThietBi());
        item.setTenThietBi(details.getTenThietBi());
        item.setLoaiBaoTri(details.getLoaiBaoTri());
        item.setNgayBatDau(details.getNgayBatDau());
        item.setNgayKetThuc(details.getNgayKetThuc());
        item.setChuKyNgay(details.getChuKyNgay());
        item.setTrangThai(details.getTrangThai());
        item.setNoiDung(details.getNoiDung());
        return repository.save(item);
    }

    @PutMapping("/{id}/duyet")
    public KeHoachBaoTri approve(@PathVariable UUID id) {
        KeHoachBaoTri item = findById(id);
        item.setTrangThai("Đã duyệt");
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.delete(findById(id));
    }

    private KeHoachBaoTri findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kế hoạch không tồn tại"));
    }
}
