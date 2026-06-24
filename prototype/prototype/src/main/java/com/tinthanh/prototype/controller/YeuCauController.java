package com.tinthanh.prototype.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    private final YeuCauRepository repository;
    private final com.tinthanh.prototype.repository.ThietBiRepository thietBiRepository;
    private static final Logger logger = LoggerFactory.getLogger(YeuCauController.class);

    public YeuCauController(YeuCauRepository repository, com.tinthanh.prototype.repository.ThietBiRepository thietBiRepository) {
        this.repository = repository;
        this.thietBiRepository = thietBiRepository;
    }

    @GetMapping
    public List<YeuCauBaoTri> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public YeuCauBaoTri getById(@PathVariable UUID id) { return find(id); }

    @PreAuthorize("hasAnyAuthority('CONG_NHAN','BP_QLTB')")
    @PostMapping
    public YeuCauBaoTri create(@RequestBody YeuCauBaoTri item) {
        if ((item.getTenThietBi() == null || item.getTenThietBi().isBlank()) && item.getMaThietBi() != null) {
            // try to fill device name from catalog
            thietBiRepository.findAll().stream()
                .filter(t -> item.getMaThietBi().equalsIgnoreCase(t.getMaSoQuanLy()))
                .findFirst().ifPresent(t -> item.setTenThietBi(t.getTenThietBi()));
        }
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PutMapping("/{id}/lapkehoach")
    public YeuCauBaoTri submitPlan(@PathVariable UUID id, @RequestBody Map<String, String> body, Authentication auth) {
        logger.info("submitPlan called for id={} by user={}", id, auth != null ? auth.getName() : "anonymous");
        logger.debug("submitPlan payload: {}", body);
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Chờ duyệt");
        item.setPhuongAnSuaChua(body.get("phuongAnSuaChua"));
        YeuCauBaoTri saved = repository.save(item);
        logger.info("submitPlan saved id={} status={}", saved.getId(), saved.getTrangThai());
        return saved;
    }

    // Some clients or proxies may send POST instead of PUT; accept POST too and delegate
    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PostMapping("/{id}/lapkehoach")
    public YeuCauBaoTri submitPlanPost(@PathVariable UUID id, @RequestBody Map<String, String> body, Authentication auth) {
        logger.warn("submitPlanPost (POST) called for id={} by user={}", id, auth != null ? auth.getName() : "anonymous");
        return submitPlan(id, body, auth);
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

    @PreAuthorize("hasAnyAuthority('CONG_NHAN','BP_QLTB')")
    @PutMapping("/{id}/nghiemthu")
    public YeuCauBaoTri accept(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        YeuCauBaoTri item = find(id);
        item.setTrangThai("Hoàn thành");
        item.setKetQuaSuaChua((String) body.get("ketQuaSuaChua"));
        item.setNguoiNghiemThu((String) body.get("nguoiNghiemThu"));
        item.setBangKiemTraKyThuat((String) body.get("bangKiemTraKyThuat"));
        if (body.get("ngayNghiemThu") != null) {
            item.setNgayNghiemThu(java.time.LocalDate.parse((String) body.get("ngayNghiemThu")));
        }
        item.setKichThicTinhHoatDong((String) body.get("kichThicTinhHoatDong"));
        if (body.get("ngayBanGiao") != null) {
            item.setNgayBanGiao(java.time.LocalDate.parse((String) body.get("ngayBanGiao")));
        }
        item.setNguoiBanGiao((String) body.get("nguoiBanGiao"));
        item.setNguoiTiepNhan((String) body.get("nguoiTiepNhan"));
        item.setGhiChuThemBaoTri((String) body.get("ghiChuThemBaoTri"));
        YeuCauBaoTri saved = repository.save(item);
        return saved;
    }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','GIAM_DOC')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','QUAN_LY_TRAM')")
    @DeleteMapping("/byMa/{ma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByMa(@PathVariable String ma) { repository.deleteAllByMaThietBiIgnoreCase(ma); }

    private YeuCauBaoTri find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sự cố không tồn tại"));
    }
}
