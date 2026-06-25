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

import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.repository.KeHoachRepository;

@RestController
@RequestMapping("/api/kehoach")
@CrossOrigin("*")
public class KeHoachController {

    @Autowired
    private KeHoachRepository repository;

    @Autowired
    private com.tinthanh.prototype.repository.ThietBiRepository thietBiRepository;

    @GetMapping
    public List<KeHoachBaoTri> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public KeHoachBaoTri getById(@PathVariable UUID id) { return find(id); }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PostMapping
    public KeHoachBaoTri create(@RequestBody KeHoachBaoTri item) {
        if ((item.getTenThietBi() == null || item.getTenThietBi().isBlank()) && item.getMaThietBi() != null) {
            thietBiRepository.findAll().stream()
                .filter(t -> item.getMaThietBi().equalsIgnoreCase(t.getMaSoQuanLy()))
                .findFirst().ifPresent(t -> item.setTenThietBi(t.getTenThietBi()));
        }
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PutMapping("/{id}")
    public KeHoachBaoTri update(@PathVariable UUID id, @RequestBody KeHoachBaoTri d) {
        KeHoachBaoTri item = find(id);
        item.setTenKeHoach(d.getTenKeHoach());
        item.setMaThietBi(d.getMaThietBi());
        item.setTenThietBi(d.getTenThietBi());
        item.setLoaiBaoTri(d.getLoaiBaoTri());
        item.setNgayBatDau(d.getNgayBatDau());
        item.setNgayKetThuc(d.getNgayKetThuc());
        item.setChuKyNgay(d.getChuKyNgay());
        item.setMoChuongTrinh(d.getMoChuongTrinh());
        item.setNoiDung(d.getNoiDung());
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('GIAM_DOC')")
    @PutMapping("/{id}/duyet")
    public KeHoachBaoTri approve(@PathVariable UUID id, java.security.Principal principal) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Đã duyệt");
        // set approver info (username stored, frontend can display full name via /api/me)
        if (principal != null) {
            item.setNguoiKyDuyet(principal.getName());
            item.setNgayKyDuyet(java.time.LocalDate.now());
        }
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('GIAM_DOC')")
    @PutMapping("/{id}/tuchoi")
    public KeHoachBaoTri reject(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Từ chối");
        item.setLyDoTuChoi(body.get("lyDoTuChoi"));
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PutMapping("/{id}/thuchien")
    public KeHoachBaoTri startExecution(@PathVariable UUID id, @RequestBody KeHoachBaoTri body) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Đang thực hiện");
        item.setDonViThueNgoai(body.getDonViThueNgoai());
        item.setNguoiThucHien(body.getNguoiThucHien());
        item.setNgayThucHienThucTe(body.getNgayThucHienThucTe());
        return repository.save(item);
    }

    // 5.1.2.5 Bàn giao — Công nhân vận hành / Quản lý Vùng
    @PreAuthorize("hasAnyAuthority('CONG_NHAN','QUAN_LY_VUNG')")
    @PutMapping("/{id}/hoanthanh")
    public KeHoachBaoTri complete(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Hoàn thành");
        item.setKetQuaBaoDuong((String) body.get("ketQuaBaoDuong"));
        item.setPhuTungThayThe((String) body.get("phuTungThayThe"));
        item.setNhanXetQualityControl((String) body.get("nhanXetQualityControl"));
        if (body.get("ngayNhanXet") != null) {
            item.setNgayNhanXet(java.time.LocalDate.parse((String) body.get("ngayNhanXet")));
        }
        // Save and keep the completed plan (do not delete)
        return repository.save(item);
    }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','GIAM_DOC')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','QUAN_LY_TRAM')")
    @DeleteMapping("/byMa/{ma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByMa(@PathVariable String ma) {
        repository.deleteAllByMaThietBiIgnoreCase(ma);
    }

    private KeHoachBaoTri find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kế hoạch không tồn tại"));
    }
}
