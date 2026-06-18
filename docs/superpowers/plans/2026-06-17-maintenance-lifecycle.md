# Maintenance Lifecycle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild the app to match exactly the two business processes in BT.01 — periodic maintenance (5.1) and incident response (5.2) — with Spring Security role-based authorization.

**Architecture:** Full lifecycle on `KeHoachBaoTri` (5.1) and `YeuCauBaoTri` (5.2) as state machines. Spring Security form login with 5 roles enforced via `@PreAuthorize` on controller methods. Frontend shows/hides buttons based on `GET /api/me`.

**Tech Stack:** Spring Boot 4.0.6, Java 25, Spring Security 7, Spring Data JPA, H2, Vanilla JS

## Global Constraints

- Package: `com.tinthanh.prototype`
- All static files: `prototype/prototype/src/main/resources/static/`
- All Java files: `prototype/prototype/src/main/java/com/tinthanh/prototype/`
- Table name for User entity: `app_users` (avoids reserved keyword)
- Use `hasAuthority('ROLE_NAME')` not `hasRole()` throughout — roles stored without `ROLE_` prefix
- All Vietnamese strings use UTF-8; file must be saved UTF-8
- Run commands from `prototype/prototype/` directory

---

## File Map

**Create:**
- `model/User.java`
- `repository/UserRepository.java`
- `service/UserDetailsServiceImpl.java`
- `config/SecurityConfig.java`
- `controller/UserController.java`
- `static/login.html`

**Modify:**
- `pom.xml` — add spring-boot-starter-security
- `config/DataInitializer.java` — remove unused seeds, add user seeds
- `controller/PdfExportController.java` — replace CongViec/ThongSoMay with YeuCauBaoTri/KeHoachBaoTri
- `controller/ThietBiController.java` — add @PreAuthorize
- `model/KeHoachBaoTri.java` — add 5 lifecycle fields
- `controller/KeHoachController.java` — add tuchoi/thuchien/hoanthanh endpoints + @PreAuthorize
- `model/YeuCauBaoTri.java` — add 6 lifecycle fields, change default status
- `controller/YeuCauController.java` — add lapkehoach/nghiemthu, update duyet/tuchoi + @PreAuthorize
- `static/js/common.js` — add getCurrentUser, doLogout, role helpers, 401 handling
- `static/index.html` — 3 cards, user navbar
- `static/pages/thiet-bi.html` — role-based buttons, user navbar
- `static/pages/ke-hoach.html` — full lifecycle UI with modals
- `static/pages/su-co.html` — create new (replaces yeu-cau.html concept)

**Delete:**
- `model/ChiPhi.java`, `model/CongViec.java`, `model/ThongSoMay.java`
- `repository/ChiPhiRepository.java`, `repository/CongViecRepository.java`, `repository/ThongSoRepository.java`
- `controller/ChiPhiController.java`, `controller/CongViecController.java`, `controller/ThongSoController.java`, `controller/BaoCaoController.java`
- `static/pages/chi-phi.html`, `static/pages/cong-viec.html`, `static/pages/giam-sat.html`, `static/pages/lich-su.html`, `static/pages/bao-cao.html`, `static/pages/yeu-cau.html`

---

## Task 1: Remove unused backend code

**Files:**
- Delete: 4 controllers, 3 models, 3 repositories (listed above)
- Modify: `controller/PdfExportController.java`
- Modify: `config/DataInitializer.java`

- [ ] **Step 1: Delete unused Java files**

```
del prototype\prototype\src\main\java\com\tinthanh\prototype\model\ChiPhi.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\model\CongViec.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\model\ThongSoMay.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\repository\ChiPhiRepository.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\repository\CongViecRepository.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\repository\ThongSoRepository.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\controller\ChiPhiController.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\controller\CongViecController.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\controller\ThongSoController.java
del prototype\prototype\src\main\java\com\tinthanh\prototype\controller\BaoCaoController.java
```

- [ ] **Step 2: Rewrite DataInitializer.java** (remove 3 unused repos, remove CongViec/ChiPhi/ThongSoMay seeds)

Replace the entire file:

```java
package com.tinthanh.prototype.config;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.model.YeuCauBaoTri;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.YeuCauRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            ThietBiRepository thietBiRepo,
            YeuCauRepository yeuCauRepo,
            KeHoachRepository keHoachRepo) {
        return args -> {
            if (thietBiRepo.count() > 0) return;

            ThietBiLyLich tb1 = new ThietBiLyLich();
            tb1.setTenThietBi("Máy CNC Haas VF-2");
            tb1.setMaSoQuanLy("TB-001");
            tb1.setSoSerial("SN-HAAS-2021-001");
            tb1.setModelType("VF-2");
            tb1.setNhaSanXuat("Haas Automation");
            tb1.setNamSanXuat(2021);
            tb1.setTrangThaiVanHanh("Đang hoạt động");
            thietBiRepo.save(tb1);

            ThietBiLyLich tb2 = new ThietBiLyLich();
            tb2.setTenThietBi("Máy phay Bridgeport");
            tb2.setMaSoQuanLy("TB-002");
            tb2.setSoSerial("SN-BP-2019-045");
            tb2.setModelType("Series I");
            tb2.setNhaSanXuat("Bridgeport");
            tb2.setNamSanXuat(2019);
            tb2.setTrangThaiVanHanh("Bảo dưỡng");
            thietBiRepo.save(tb2);

            ThietBiLyLich tb3 = new ThietBiLyLich();
            tb3.setTenThietBi("Máy tiện Okuma LB3000");
            tb3.setMaSoQuanLy("TB-003");
            tb3.setSoSerial("SN-OKU-2020-112");
            tb3.setModelType("LB3000");
            tb3.setNhaSanXuat("Okuma");
            tb3.setNamSanXuat(2020);
            tb3.setTrangThaiVanHanh("Sự cố");
            thietBiRepo.save(tb3);

            YeuCauBaoTri yc = new YeuCauBaoTri();
            yc.setTenThietBi("Máy tiện Okuma LB3000");
            yc.setMaThietBi("TB-003");
            yc.setMoTaSuCo("Tiếng kêu bất thường từ trục chính, rung mạnh khi gia công");
            yc.setMucDo("Cao");
            yc.setNguoiYeuCau("Phạm Công Nhân");
            yc.setTrangThai("Phát hiện");
            yeuCauRepo.save(yc);

            KeHoachBaoTri kh = new KeHoachBaoTri();
            kh.setTenKeHoach("Bảo dưỡng định kỳ CNC Q2/2026");
            kh.setMaThietBi("TB-001");
            kh.setTenThietBi("Máy CNC Haas VF-2");
            kh.setLoaiBaoTri("Định kỳ");
            kh.setNgayBatDau(LocalDate.of(2026, 6, 1));
            kh.setNgayKetThuc(LocalDate.of(2026, 6, 15));
            kh.setChuKyNgay(90);
            kh.setNoiDung("Thay dầu bôi trơn, kiểm tra băng tải phoi, hiệu chuẩn trục");
            keHoachRepo.save(kh);
        };
    }
}
```

- [ ] **Step 3: Rewrite PdfExportController.java** (remove CongViec/ThongSoMay, use YeuCauBaoTri for BM.BT.01.04 and KeHoachBaoTri for BM.BT.01.05)

Replace the entire file:

```java
package com.tinthanh.prototype.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.model.YeuCauBaoTri;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.YeuCauRepository;
import com.tinthanh.prototype.service.PdfService;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin("*")
public class PdfExportController {

    @Autowired private PdfService pdfService;
    @Autowired private ThietBiRepository thietBiRepo;
    @Autowired private KeHoachRepository keHoachRepo;
    @Autowired private YeuCauRepository yeuCauRepo;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping(value = "/thietbi/lylich/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportThietBiLyLich(@PathVariable UUID id) throws IOException {
        ThietBiLyLich tb = thietBiRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return pdf(buildLyLichHtml(tb), "BM.BT.01.01_LyLichThietBi.pdf");
    }

    @GetMapping(value = "/thietbi/danhmuc", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportDanhMucThietBi() throws IOException {
        return pdf(buildDanhMucHtml(thietBiRepo.findAll()), "BM.BT.01.02_DanhMucThietBi.pdf");
    }

    @GetMapping(value = "/kehoach", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportKeHoach() throws IOException {
        return pdf(buildKeHoachHtml(keHoachRepo.findAll()), "BM.BT.01.03_KeHoachBaoDuong.pdf");
    }

    @GetMapping(value = "/nghiemthu", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportBienBanNghiemThu() throws IOException {
        List<YeuCauBaoTri> done = yeuCauRepo.findAll().stream()
                .filter(y -> "Hoàn thành".equals(y.getTrangThai()))
                .collect(Collectors.toList());
        return pdf(buildBienBanHtml(done), "BM.BT.01.04_BienBanNghiemThu.pdf");
    }

    @GetMapping(value = "/so-theo-doi", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSoTheoDoi() throws IOException {
        List<KeHoachBaoTri> done = keHoachRepo.findAll().stream()
                .filter(k -> "Hoàn thành".equals(k.getTrangThai()))
                .collect(Collectors.toList());
        return pdf(buildSoTheoDoiHtml(done), "BM.BT.01.05_SoTheoDoiBaoDuong.pdf");
    }

    private ResponseEntity<byte[]> pdf(String html, String filename) throws IOException {
        String base = PdfExportController.class.getResource("/static/") != null
                ? PdfExportController.class.getResource("/static/").toString() : "";
        byte[] bytes = pdfService.renderPdf(html, base);
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_PDF);
        h.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(bytes, h, HttpStatus.OK);
    }

    private String buildLyLichHtml(ThietBiLyLich tb) {
        return "<html><head><meta charset='utf-8'/><style>"
                + "body{font-family:'Noto Sans',Arial,sans-serif;}.page{padding:24px;}"
                + ".form-code{float:right;font-weight:bold;}h1{text-align:center;margin:0 0 16px;}"
                + "table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + "th,td{border:1px solid #333;padding:10px;vertical-align:top;}"
                + "th{background:#f2f2f2;text-align:left;width:35%;}"
                + ".footer-table{width:100%;border:none;margin-top:32px;}"
                + ".footer-table td{border:none;padding:12px 8px;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.01</div>"
                + "<h1>LÝ LỊCH MÁY MÓC THIẾT BỊ</h1><table>"
                + row("Tên máy móc, thiết bị", tb.getTenThietBi())
                + row("Mã số quản lý", tb.getMaSoQuanLy())
                + row("Model/Type", tb.getModelType())
                + row("Số serial", tb.getSoSerial())
                + row("Nhà sản xuất", tb.getNhaSanXuat())
                + row("Năm sản xuất", tb.getNamSanXuat())
                + row("Đặc điểm máy móc, thiết bị", safe(tb.getDacDiem()))
                + row("Ngày tiếp nhận", tb.getNgayTiepNhan() == null ? "" : tb.getNgayTiepNhan().format(DATE_FORMAT))
                + row("Ngày đưa vào sử dụng", tb.getNgayDuaVaoSuDung() == null ? "" : tb.getNgayDuaVaoSuDung().format(DATE_FORMAT))
                + row("Phụ tùng kèm theo", safe(tb.getPhuTungKemTheo()))
                + row("Tài liệu kèm theo", safe(tb.getTaiLieuKemTheo()))
                + row("Tình trạng lúc tiếp nhận", safe(tb.getTinhTrangTiepNhan()))
                + "</table>"
                + "<table class='footer-table'><tr><td>Người lập</td><td>Phê duyệt</td></tr></table>"
                + "</div></body></html>";
    }

    private String buildDanhMucHtml(List<ThietBiLyLich> items) {
        StringBuilder rows = new StringBuilder();
        int i = 1;
        for (ThietBiLyLich tb : items) {
            rows.append("<tr><td>").append(i++).append("</td>")
                .append("<td>").append(safe(tb.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(tb.getMaSoQuanLy())).append("</td>")
                .append("<td>1</td>")
                .append("<td>").append(safe(tb.getNhaSanXuat())).append("</td>")
                .append("<td>").append(safe(tb.getModelType())).append("</td>")
                .append("<td>").append(tb.getNamSanXuat() == null ? "" : tb.getNamSanXuat()).append("</td>")
                .append("<td>").append(safe(tb.getTrangThaiVanHanh())).append("</td></tr>");
        }
        if (items.isEmpty()) rows.append("<tr><td colspan='8' style='text-align:center'>Không có dữ liệu</td></tr>");
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans',Arial,sans-serif;}"
                + ".form-code{float:right;font-weight:bold;}h1{text-align:center;}"
                + "table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;}</style></head><body>"
                + "<div class='form-code'>BM.BT.01.02</div><h1>DANH MỤC THIẾT BỊ, MÁY MÓC</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu</th><th>Số lượng</th>"
                + "<th>Xuất xứ</th><th>Thông số kỹ thuật</th><th>Năm sản xuất</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></body></html>";
    }

    private String buildKeHoachHtml(List<KeHoachBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int i = 1;
        for (KeHoachBaoTri k : items) {
            List<Integer> months = List.of();
            if (k.getNgayBatDau() != null && k.getNgayKetThuc() != null) {
                months = k.getNgayBatDau().datesUntil(k.getNgayKetThuc().plusDays(1))
                        .map(d -> d.getMonthValue()).distinct().sorted().collect(Collectors.toList());
            }
            StringBuilder monthCells = new StringBuilder();
            for (int m = 1; m <= 12; m++)
                monthCells.append("<td>").append(months.contains(m) ? "X" : "").append("</td>");
            rows.append("<tr><td>").append(i++).append("</td>")
                .append("<td>").append(safe(k.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(k.getMaThietBi())).append("</td>")
                .append("<td>").append(safe(k.getLoaiBaoTri())).append("</td>")
                .append(monthCells)
                .append("<td>").append(safe(k.getTrangThai())).append("</td></tr>");
        }
        if (items.isEmpty()) rows.append("<tr><td colspan='16' style='text-align:center'>Không có dữ liệu</td></tr>");
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans',Arial,sans-serif;}"
                + ".page{padding:24px;}.form-code{float:right;font-weight:bold;}h1{text-align:center;margin:0 0 8px;}"
                + ".subtitle{text-align:center;margin:0 0 18px;}table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + "th,td{border:1px solid #333;padding:8px;text-align:center;}th{background:#f2f2f2;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.03</div>"
                + "<h1>KẾ HOẠCH BẢO DƯỠNG, HIỆU CHUẨN, KIỂM ĐỊNH</h1>"
                + "<p class='subtitle'>Năm " + java.time.Year.now().getValue() + "</p>"
                + "<table><thead><tr><th>Stt</th><th>Tên máy móc, thiết bị</th><th>Mã số</th><th>Bộ phận sử dụng</th>"
                + "<th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th>"
                + "<th>7</th><th>8</th><th>9</th><th>10</th><th>11</th><th>12</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></div></body></html>";
    }

    private String buildBienBanHtml(List<YeuCauBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int i = 1;
        for (YeuCauBaoTri y : items) {
            rows.append("<tr><td>").append(i++).append("</td>")
                .append("<td>").append(safe(y.getMaThietBi())).append(" - ").append(safe(y.getTenThietBi())).append("</td>")
                .append("<td>1</td>")
                .append("<td>").append(safe(y.getKetQuaSuaChua())).append("</td>")
                .append("<td>").append(safe(y.getNguoiNghiemThu())).append("</td></tr>");
        }
        if (items.isEmpty()) rows.append("<tr><td colspan='5' style='text-align:center'>Chưa có nghiệm thu hoàn thành</td></tr>");
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans',Arial,sans-serif;}"
                + ".form-code{float:right;font-weight:bold;}h1{text-align:center;}"
                + "table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;}"
                + "</style></head><body>"
                + "<div class='form-code'>BM.BT.01.04</div><h1>BIÊN BẢN NGHIỆM THU</h1>"
                + "<p>Căn cứ biên bản kiểm tra kỹ thuật đã ký, kết quả nghiệm thu như sau:</p>"
                + "<table><thead><tr><th>TT</th><th>Tên TB sửa chữa – thay thế</th><th>Số lượng</th>"
                + "<th>Tình hình hoạt động</th><th>Người nghiệm thu</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "<table style='width:100%;margin-top:32px;'><tr>"
                + "<td style='border:none;text-align:center;'>ĐẠI DIỆN BÊN A</td>"
                + "<td style='border:none;text-align:center;'>ĐẠI DIỆN BÊN B</td></tr></table>"
                + "</body></html>";
    }

    private String buildSoTheoDoiHtml(List<KeHoachBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int i = 1;
        for (KeHoachBaoTri k : items) {
            rows.append("<tr><td>").append(i++).append("</td>")
                .append("<td>").append(safe(k.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(k.getMaThietBi())).append("</td>")
                .append("<td>").append(safe(k.getLoaiBaoTri())).append("</td>")
                .append("<td>").append(k.getNgayThucHienThucTe() == null ? "" : k.getNgayThucHienThucTe().format(DATE_FORMAT)).append("</td>")
                .append("<td>").append(safe(k.getNguoiThucHien())).append("</td>")
                .append("<td>").append(safe(k.getDonViThueNgoai())).append("</td>")
                .append("<td>").append(safe(k.getKetQuaBaoDuong())).append("</td></tr>");
        }
        if (items.isEmpty()) rows.append("<tr><td colspan='8' style='text-align:center'>Chưa có bảo dưỡng hoàn thành</td></tr>");
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans',Arial,sans-serif;}"
                + ".form-code{float:right;font-weight:bold;}h1{text-align:center;}"
                + "table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;}"
                + "</style></head><body>"
                + "<div class='form-code'>BM.BT.01.05</div><h1>SỔ THEO DÕI BẢO DƯỠNG MÁY MÓC, THIẾT BỊ</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu</th><th>Loại bảo trì</th>"
                + "<th>Ngày thực hiện</th><th>Người thực hiện</th><th>Đơn vị thuê ngoài</th><th>Kết quả</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></body></html>";
    }

    private String safe(Object v) { return v == null ? "" : v.toString(); }
    private String row(String label, Object value) {
        return "<tr><th>" + label + "</th><td>" + safe(value) + "</td></tr>";
    }
}
```

- [ ] **Step 4: Verify project compiles**

```
cd prototype/prototype && mvn compile -q
```
Expected: BUILD SUCCESS (no references to deleted classes)

- [ ] **Step 5: Delete unused frontend pages**

```
del prototype\prototype\src\main\resources\static\pages\chi-phi.html
del prototype\prototype\src\main\resources\static\pages\cong-viec.html
del prototype\prototype\src\main\resources\static\pages\giam-sat.html
del prototype\prototype\src\main\resources\static\pages\lich-su.html
del prototype\prototype\src\main\resources\static\pages\bao-cao.html
del prototype\prototype\src\main\resources\static\pages\yeu-cau.html
```

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "chore: remove unused entities, controllers, and frontend pages"
```

---

## Task 2: Spring Security + User entity

**Files:**
- Modify: `pom.xml`
- Create: `model/User.java`
- Create: `repository/UserRepository.java`
- Create: `service/UserDetailsServiceImpl.java`
- Create: `config/SecurityConfig.java`
- Create: `controller/UserController.java`
- Create: `static/login.html`
- Modify: `config/DataInitializer.java`

- [ ] **Step 1: Add Spring Security dependency to pom.xml**

Add inside `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

- [ ] **Step 2: Create model/User.java**

```java
package com.tinthanh.prototype.model;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String hoTen;

    @Column(nullable = false, length = 30)
    private String role;

    public User() { this.id = UUID.randomUUID(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```

- [ ] **Step 3: Create repository/UserRepository.java**

```java
package com.tinthanh.prototype.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tinthanh.prototype.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
```

- [ ] **Step 4: Create service/UserDetailsServiceImpl.java**

```java
package com.tinthanh.prototype.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tinthanh.prototype.model.User;
import com.tinthanh.prototype.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
```

- [ ] **Step 5: Create config/SecurityConfig.java**

```java
package com.tinthanh.prototype.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.tinthanh.prototype.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login.html", "/css/**", "/js/**", "/fonts/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login.html?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }
}
```

- [ ] **Step 6: Create controller/UserController.java**

```java
package com.tinthanh.prototype.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.tinthanh.prototype.model.User;
import com.tinthanh.prototype.repository.UserRepository;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/me")
    public Map<String, String> me(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return Map.of("username", user.getUsername(), "hoTen", user.getHoTen(), "role", user.getRole());
    }
}
```

- [ ] **Step 7: Update DataInitializer.java** — add User seeding (add to the existing file from Task 1)

Add imports:
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.tinthanh.prototype.model.User;
import com.tinthanh.prototype.repository.UserRepository;
```

Update the `@Bean` method signature to also accept `UserRepository userRepo`, then add at the end of the lambda (before the closing `}`):

```java
            // Seed users
            if (userRepo.count() == 0) {
                BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
                String pw = enc.encode("123456");
                userRepo.save(user("admin.tram", pw, "Nguyễn Quản Lý Trạm", "QUAN_LY_TRAM"));
                userRepo.save(user("bp.qltb",    pw, "Trần BP. QLTB",        "BP_QLTB"));
                userRepo.save(user("giamdoc",    pw, "Lê Giám Đốc",          "GIAM_DOC"));
                userRepo.save(user("congnhan",   pw, "Phạm Công Nhân",       "CONG_NHAN"));
                userRepo.save(user("ql.vung",    pw, "Hoàng Quản Lý Vùng",  "QUAN_LY_VUNG"));
            }
```

Add a private helper at the bottom of the class (outside the `@Bean` method):

```java
    private User user(String username, String password, String hoTen, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setHoTen(hoTen);
        u.setRole(role);
        return u;
    }
```

- [ ] **Step 8: Create static/login.html**

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        body { background: var(--bg, #f5f5f5); display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
        .login-box { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,.1); width: 100%; max-width: 400px; }
        .login-box h1 { text-align: center; color: #8b3a8f; margin: 0 0 4px; font-size: 1.4rem; }
        .login-box p  { text-align: center; color: #888; margin: 0 0 28px; font-size: .9rem; }
        .msg { padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-size: .9rem; display: none; }
        .msg.error  { background: #fee; color: #c33; }
        .msg.success{ background: #efe; color: #363; }
    </style>
</head>
<body>
    <div class="login-box">
        <h1>Quản lý Bảo trì Thiết bị</h1>
        <p>Tín Thành Environment Technology</p>
        <div id="msg" class="msg"></div>
        <form method="post" action="/login">
            <div class="form-group">
                <label>Tên đăng nhập</label>
                <input type="text" name="username" required placeholder="Nhập tên đăng nhập">
            </div>
            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" required placeholder="Nhập mật khẩu">
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;margin-top:8px;">Đăng nhập</button>
        </form>
        <p style="margin-top:16px;font-size:.8rem;color:#999;text-align:center">
            Demo: giamdoc / bp.qltb / congnhan / admin.tram / ql.vung — mật khẩu: 123456
        </p>
    </div>
    <script>
        const p = new URLSearchParams(location.search);
        const el = document.getElementById('msg');
        if (p.has('error'))  { el.textContent = 'Tên đăng nhập hoặc mật khẩu không đúng.'; el.className = 'msg error';   el.style.display = 'block'; }
        if (p.has('logout')) { el.textContent = 'Đã đăng xuất thành công.';                 el.className = 'msg success'; el.style.display = 'block'; }
    </script>
</body>
</html>
```

- [ ] **Step 9: Start app and verify login works**

```
cd prototype/prototype && mvn spring-boot:run
```

Open `http://localhost:8080` — should redirect to `/login.html`.
Login as `giamdoc / 123456` — should redirect to `/`.
Click any nav link — should work.
Open a new browser tab, go to `/api/me` — should return `{"username":"giamdoc","hoTen":"Lê Giám Đốc","role":"GIAM_DOC"}`.

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "feat: add Spring Security with form login and 5 user roles"
```

---

## Task 3: Update common.js

**Files:**
- Modify: `static/js/common.js`

- [ ] **Step 1: Replace common.js entirely**

```javascript
const API = {
    thietBi: '/api/thietbi',
    suCo:    '/api/yeucau',
    keHoach: '/api/kehoach'
};

let currentUser = null;

async function getCurrentUser() {
    if (currentUser) return currentUser;
    try {
        const res = await fetch('/api/me');
        if (res.status === 401) { window.location.href = '/login.html'; return null; }
        currentUser = await res.json();
    } catch (e) {
        window.location.href = '/login.html';
        return null;
    }
    return currentUser;
}

function hasRole(...roles) {
    return currentUser != null && roles.includes(currentUser.role);
}

async function doLogout() {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login.html?logout';
}

async function initNav(activePage) {
    const user = await getCurrentUser();
    if (!user) return;
    const el = document.getElementById('navUser');
    if (el) el.textContent = user.hoTen;
    document.querySelectorAll('.navbar-links a[data-page]').forEach(a => {
        a.classList.toggle('active', a.dataset.page === activePage);
    });
}

async function apiGet(url) {
    const res = await fetch(url);
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPost(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPut(url, body) {
    const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(url, { method: 'DELETE' });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return; }
    if (!res.ok) throw new Error('API error: ' + res.status);
}

function badgeClass(trangThai) {
    const map = {
        'Đang hoạt động':  'badge-success',
        'Hoàn thành':      'badge-success',
        'Đã duyệt':        'badge-info',
        'Chờ duyệt':       'badge-warning',
        'Phát hiện':       'badge-warning',
        'Đang thực hiện':  'badge-info',
        'Đang sửa chữa':   'badge-info',
        'Từ chối':         'badge-danger',
        'Sự cố':           'badge-danger',
        'Bảo dưỡng':       'badge-warning'
    };
    return map[trangThai] || 'badge-neutral';
}

function badgeHtml(trangThai) {
    return `<span class="badge ${badgeClass(trangThai)}">${trangThai || ''}</span>`;
}

function formatDate(d) { return d ? d.split('T')[0] : ''; }

function getField(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
}

function getNumberField(id) {
    const v = parseInt(document.getElementById(id)?.value);
    return isNaN(v) ? null : v;
}

function clearFields(ids) {
    ids.forEach(id => { const el = document.getElementById(id); if (el) el.value = ''; });
}

function openPdf(url) { window.open(url, '_blank'); }

function showModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }
```

- [ ] **Step 2: Commit**

```bash
git add static/js/common.js
git commit -m "feat: add role-aware helpers and 401 handling to common.js"
```

---

## Task 4: KeHoachBaoTri lifecycle — backend

**Files:**
- Modify: `model/KeHoachBaoTri.java`
- Modify: `controller/KeHoachController.java`
- Modify: `controller/ThietBiController.java`

- [ ] **Step 1: Add 5 new fields to KeHoachBaoTri.java**

Add after the `noiDung` field declaration and before the constructor:

```java
    @Column(columnDefinition = "TEXT")
    private String lyDoTuChoi;

    private String donViThueNgoai;
    private String nguoiThucHien;
    private LocalDate ngayThucHienThucTe;

    @Column(columnDefinition = "TEXT")
    private String ketQuaBaoDuong;
```

Add getters/setters before the closing `}`:

```java
    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }
    public String getDonViThueNgoai() { return donViThueNgoai; }
    public void setDonViThueNgoai(String donViThueNgoai) { this.donViThueNgoai = donViThueNgoai; }
    public String getNguoiThucHien() { return nguoiThucHien; }
    public void setNguoiThucHien(String nguoiThucHien) { this.nguoiThucHien = nguoiThucHien; }
    public LocalDate getNgayThucHienThucTe() { return ngayThucHienThucTe; }
    public void setNgayThucHienThucTe(LocalDate ngayThucHienThucTe) { this.ngayThucHienThucTe = ngayThucHienThucTe; }
    public String getKetQuaBaoDuong() { return ketQuaBaoDuong; }
    public void setKetQuaBaoDuong(String ketQuaBaoDuong) { this.ketQuaBaoDuong = ketQuaBaoDuong; }
```

- [ ] **Step 2: Replace KeHoachController.java entirely**

```java
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

    @GetMapping
    public List<KeHoachBaoTri> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public KeHoachBaoTri getById(@PathVariable UUID id) { return find(id); }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @PostMapping
    public KeHoachBaoTri create(@RequestBody KeHoachBaoTri item) { return repository.save(item); }

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
        item.setNoiDung(d.getNoiDung());
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('GIAM_DOC')")
    @PutMapping("/{id}/duyet")
    public KeHoachBaoTri approve(@PathVariable UUID id) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Đã duyệt");
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

    @PreAuthorize("hasAnyAuthority('BP_QLTB','CONG_NHAN','QUAN_LY_VUNG')")
    @PutMapping("/{id}/hoanthanh")
    public KeHoachBaoTri complete(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        KeHoachBaoTri item = find(id);
        item.setTrangThai("Hoàn thành");
        item.setKetQuaBaoDuong(body.get("ketQuaBaoDuong"));
        return repository.save(item);
    }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.delete(find(id)); }

    private KeHoachBaoTri find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kế hoạch không tồn tại"));
    }
}
```

- [ ] **Step 3: Add @PreAuthorize to ThietBiController.java**

Add import at top of ThietBiController:
```java
import org.springframework.security.access.prepost.PreAuthorize;
```

Add `@PreAuthorize("hasAuthority('QUAN_LY_TRAM')")` on the `create`, `update`, and `delete` methods. The `getAll`, `getById`, and search methods stay public (any authenticated user).

- [ ] **Step 4: Compile check**

```
mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: add KeHoachBaoTri lifecycle endpoints with role-based authorization"
```

---

## Task 5: YeuCauBaoTri lifecycle — backend

**Files:**
- Modify: `model/YeuCauBaoTri.java`
- Modify: `controller/YeuCauController.java`

- [ ] **Step 1: Add 6 new fields to YeuCauBaoTri.java**

Change the constructor default status from `"Chờ duyệt"` to `"Phát hiện"`:
```java
    public YeuCauBaoTri() {
        this.id = UUID.randomUUID();
        this.trangThai = "Phát hiện";
        this.ngayYeuCau = LocalDate.now();
    }
```

Add after `ngayTao` field:
```java
    @Column(columnDefinition = "TEXT")
    private String phuongAnSuaChua;

    @Column(columnDefinition = "TEXT")
    private String lyDoTuChoi;

    private String donViThueNgoai;
    private LocalDate ngaySuaChua;

    @Column(columnDefinition = "TEXT")
    private String ketQuaSuaChua;

    private String nguoiNghiemThu;
```

Add getters/setters before the closing `}`:
```java
    public String getPhuongAnSuaChua() { return phuongAnSuaChua; }
    public void setPhuongAnSuaChua(String phuongAnSuaChua) { this.phuongAnSuaChua = phuongAnSuaChua; }
    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }
    public String getDonViThueNgoai() { return donViThueNgoai; }
    public void setDonViThueNgoai(String donViThueNgoai) { this.donViThueNgoai = donViThueNgoai; }
    public LocalDate getNgaySuaChua() { return ngaySuaChua; }
    public void setNgaySuaChua(LocalDate ngaySuaChua) { this.ngaySuaChua = ngaySuaChua; }
    public String getKetQuaSuaChua() { return ketQuaSuaChua; }
    public void setKetQuaSuaChua(String ketQuaSuaChua) { this.ketQuaSuaChua = ketQuaSuaChua; }
    public String getNguoiNghiemThu() { return nguoiNghiemThu; }
    public void setNguoiNghiemThu(String nguoiNghiemThu) { this.nguoiNghiemThu = nguoiNghiemThu; }
```

- [ ] **Step 2: Replace YeuCauController.java entirely**

```java
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
```

- [ ] **Step 3: Compile and restart**

```
mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: add YeuCauBaoTri incident lifecycle endpoints with role-based authorization"
```

---

## Task 6: ke-hoach.html — periodic maintenance UI

**Files:**
- Replace: `static/pages/ke-hoach.html`

- [ ] **Step 1: Replace ke-hoach.html entirely**

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảo dưỡng định kỳ</title>
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .modal-overlay{position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,.5);display:none;align-items:center;justify-content:center;z-index:1000;}
        .modal-box{background:#fff;border-radius:12px;padding:32px;max-width:480px;width:90%;}
        .modal-box h3{margin:0 0 20px;color:#8b3a8f;}
        .text-danger{color:#dc2626;}
        .small-note{font-size:.8rem;color:#888;}
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-inner">
            <a href="/" class="navbar-brand">
                <svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="4" y="8" width="24" height="18" rx="2"/><path d="M10 8V6a2 2 0 012-2h8a2 2 0 012 2v2"/><circle cx="16" cy="17" r="4"/></svg>
                Quản lý Bảo trì thiết bị
            </a>
            <div class="navbar-links">
                <a href="/pages/thiet-bi.html" data-page="thiet-bi">Thiết bị</a>
                <a href="/pages/ke-hoach.html" data-page="ke-hoach">Bảo dưỡng định kỳ</a>
                <a href="/pages/su-co.html" data-page="su-co">Khắc phục sự cố</a>
            </div>
            <div style="display:flex;align-items:center;gap:12px;">
                <span id="navUser" class="small-note"></span>
                <button class="btn btn-secondary btn-sm" onclick="doLogout()">Đăng xuất</button>
            </div>
        </div>
    </nav>

    <div class="page-container">
        <div class="page-header">
            <div class="breadcrumb"><a href="/">Trang chủ</a> / Bảo dưỡng định kỳ</div>
            <h1>Quy trình bảo dưỡng định kỳ (5.1)</h1>
            <p>Lập kế hoạch, phê duyệt, thực hiện và bàn giao bảo dưỡng thiết bị</p>
        </div>

        <!-- Create form — only visible to BP_QLTB -->
        <div class="card" id="formCard" style="display:none">
            <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;">
                <h2>Lập kế hoạch bảo dưỡng mới</h2>
                <div style="display:flex;align-items:center;gap:8px;">
                    <button class="btn btn-secondary" onclick="openPdf('/api/pdf/kehoach')">Xuất PDF BM.BT.01.03</button>
                </div>
            </div>
            <div class="form-grid cols-3">
                <div class="form-group"><label>Tên kế hoạch</label><input type="text" id="tenKeHoach"></div>
                <div class="form-group"><label>Mã thiết bị</label><input type="text" id="maThietBi"></div>
                <div class="form-group"><label>Tên thiết bị</label><input type="text" id="tenThietBi"></div>
                <div class="form-group"><label>Loại bảo trì</label>
                    <select id="loaiBaoTri"><option>Định kỳ</option><option>Sửa chữa</option><option>Kiểm tra</option></select>
                </div>
                <div class="form-group"><label>Ngày bắt đầu</label><input type="date" id="ngayBatDau"></div>
                <div class="form-group"><label>Ngày kết thúc</label><input type="date" id="ngayKetThuc"></div>
                <div class="form-group"><label>Chu kỳ (ngày)</label><input type="number" id="chuKyNgay"></div>
                <div class="form-group" style="grid-column:span 2"><label>Nội dung</label><textarea id="noiDung"></textarea></div>
            </div>
            <div class="form-actions"><button class="btn btn-primary" onclick="addKeHoach()">Lưu kế hoạch</button></div>
        </div>

        <div class="card">
            <h2>Danh sách kế hoạch bảo dưỡng</h2>
            <div class="table-wrap">
                <table>
                    <thead><tr>
                        <th>Tên kế hoạch</th><th>Thiết bị</th><th>Loại</th>
                        <th>Bắt đầu</th><th>Kết thúc</th><th>Chu kỳ</th>
                        <th>Trạng thái</th><th>Hành động</th>
                    </tr></thead>
                    <tbody id="tableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal: Từ chối -->
    <div id="modalTuChoi" class="modal-overlay">
        <div class="modal-box">
            <h3>Từ chối kế hoạch</h3>
            <div class="form-group"><label>Lý do từ chối <span class="text-danger">*</span></label>
                <textarea id="lyDoTuChoi" rows="4" placeholder="Nhập lý do từ chối để gửi lại BP.QLTB..."></textarea>
            </div>
            <div class="form-actions">
                <button class="btn btn-danger" onclick="submitTuChoi()">Xác nhận từ chối</button>
                <button class="btn btn-secondary" onclick="closeModal('modalTuChoi')">Hủy</button>
            </div>
        </div>
    </div>

    <!-- Modal: Bắt đầu thực hiện -->
    <div id="modalThucHien" class="modal-overlay">
        <div class="modal-box">
            <h3>Bắt đầu thực hiện bảo dưỡng</h3>
            <div class="form-group"><label>Người thực hiện (BP.QLTB)</label><input type="text" id="nguoiThucHien"></div>
            <div class="form-group"><label>Đơn vị thuê ngoài (nếu cần)</label><input type="text" id="donViThueNgoai" placeholder="Để trống nếu không thuê ngoài"></div>
            <div class="form-group"><label>Ngày thực hiện thực tế</label><input type="date" id="ngayThucHienThucTe"></div>
            <div class="form-actions">
                <button class="btn btn-primary" onclick="submitThucHien()">Xác nhận thực hiện</button>
                <button class="btn btn-secondary" onclick="closeModal('modalThucHien')">Hủy</button>
            </div>
        </div>
    </div>

    <!-- Modal: Hoàn thành / Bàn giao -->
    <div id="modalHoanThanh" class="modal-overlay">
        <div class="modal-box">
            <h3>Hoàn thành &amp; Bàn giao</h3>
            <div class="form-group"><label>Kết quả bảo dưỡng <span class="text-danger">*</span></label>
                <textarea id="ketQuaBaoDuong" rows="4" placeholder="Mô tả kết quả, phụ tùng thay thế, chất lượng máy sau bảo dưỡng..."></textarea>
            </div>
            <div class="form-actions">
                <button class="btn btn-success" onclick="submitHoanThanh()">Xác nhận hoàn thành</button>
                <button class="btn btn-secondary" onclick="closeModal('modalHoanThanh')">Hủy</button>
            </div>
        </div>
    </div>

    <script src="/js/common.js"></script>
    <script>
        let actionId = null;

        async function init() {
            await initNav('ke-hoach');
            if (hasRole('BP_QLTB')) {
                document.getElementById('formCard').style.display = 'block';
            }
            load();
        }

        async function load() {
            const data = await apiGet(API.keHoach);
            if (!data) return;
            document.getElementById('tableBody').innerHTML = data.map(renderRow).join('');
        }

        function renderRow(k) {
            const isBp    = hasRole('BP_QLTB');
            const isGd    = hasRole('GIAM_DOC');
            const canDone = hasRole('BP_QLTB', 'CONG_NHAN', 'QUAN_LY_VUNG');

            let actions = '';
            if (k.trangThai === 'Chờ duyệt') {
                if (isGd) {
                    actions += `<button class="btn btn-success btn-sm" onclick="duyet('${k.id}')">Duyệt</button>`;
                    actions += `<button class="btn btn-danger btn-sm" onclick="openTuChoi('${k.id}')">Từ chối</button>`;
                }
                if (isBp) actions += `<button class="btn btn-danger btn-sm" onclick="xoa('${k.id}')">Xóa</button>`;
            } else if (k.trangThai === 'Đã duyệt' && isBp) {
                actions += `<button class="btn btn-primary btn-sm" onclick="openThucHien('${k.id}')">Bắt đầu thực hiện</button>`;
            } else if (k.trangThai === 'Đang thực hiện' && canDone) {
                actions += `<button class="btn btn-success btn-sm" onclick="openHoanThanh('${k.id}')">Hoàn thành</button>`;
            }

            const note = k.trangThai === 'Từ chối' && k.lyDoTuChoi
                ? `<br><small class="text-danger">Lý do: ${k.lyDoTuChoi}</small>` : '';
            const exec = k.nguoiThucHien
                ? `<br><small class="small-note">Người TH: ${k.nguoiThucHien}${k.donViThueNgoai ? ' / ' + k.donViThueNgoai : ''}</small>` : '';

            return `<tr>
                <td>${k.tenKeHoach}</td>
                <td>${k.tenThietBi || k.maThietBi}</td>
                <td>${k.loaiBaoTri || ''}</td>
                <td>${formatDate(k.ngayBatDau)}</td>
                <td>${formatDate(k.ngayKetThuc)}</td>
                <td>${k.chuKyNgay || ''} ngày</td>
                <td>${badgeHtml(k.trangThai)}${note}${exec}</td>
                <td class="actions">${actions}</td>
            </tr>`;
        }

        async function addKeHoach() {
            const r = await apiPost(API.keHoach, {
                tenKeHoach: getField('tenKeHoach'), maThietBi: getField('maThietBi'),
                tenThietBi: getField('tenThietBi'), loaiBaoTri: document.getElementById('loaiBaoTri').value,
                ngayBatDau: getField('ngayBatDau') || null, ngayKetThuc: getField('ngayKetThuc') || null,
                chuKyNgay: getNumberField('chuKyNgay'), noiDung: getField('noiDung')
            });
            if (!r) return;
            clearFields(['tenKeHoach','maThietBi','tenThietBi','ngayBatDau','ngayKetThuc','chuKyNgay','noiDung']);
            load();
        }

        async function duyet(id) {
            if (!confirm('Phê duyệt kế hoạch này?')) return;
            const r = await apiPut(`${API.keHoach}/${id}/duyet`, {});
            if (r) load();
        }

        function openTuChoi(id) {
            actionId = id;
            document.getElementById('lyDoTuChoi').value = '';
            showModal('modalTuChoi');
        }
        async function submitTuChoi() {
            const ly = getField('lyDoTuChoi');
            if (!ly) { alert('Vui lòng nhập lý do từ chối.'); return; }
            const r = await apiPut(`${API.keHoach}/${actionId}/tuchoi`, { lyDoTuChoi: ly });
            if (r) { closeModal('modalTuChoi'); load(); }
        }

        function openThucHien(id) {
            actionId = id;
            clearFields(['nguoiThucHien','donViThueNgoai','ngayThucHienThucTe']);
            showModal('modalThucHien');
        }
        async function submitThucHien() {
            const r = await apiPut(`${API.keHoach}/${actionId}/thuchien`, {
                nguoiThucHien: getField('nguoiThucHien'),
                donViThueNgoai: getField('donViThueNgoai'),
                ngayThucHienThucTe: getField('ngayThucHienThucTe') || null
            });
            if (r) { closeModal('modalThucHien'); load(); }
        }

        function openHoanThanh(id) {
            actionId = id;
            document.getElementById('ketQuaBaoDuong').value = '';
            showModal('modalHoanThanh');
        }
        async function submitHoanThanh() {
            const kq = getField('ketQuaBaoDuong');
            if (!kq) { alert('Vui lòng nhập kết quả bảo dưỡng.'); return; }
            const r = await apiPut(`${API.keHoach}/${actionId}/hoanthanh`, { ketQuaBaoDuong: kq });
            if (r) { closeModal('modalHoanThanh'); load(); }
        }

        async function xoa(id) {
            if (!confirm('Xóa kế hoạch này?')) return;
            await apiDelete(`${API.keHoach}/${id}`);
            load();
        }

        init();
    </script>
</body>
</html>
```

- [ ] **Step 2: Open browser, login as bp.qltb, go to /pages/ke-hoach.html**

Verify: create form is visible, table loads.

- [ ] **Step 3: Test full lifecycle manually**

Login as `bp.qltb` → create a plan → status `Chờ duyệt`.
Switch to `giamdoc` → Từ chối button visible → click → enter reason → status `Từ chối`, reason shown.
Switch to `bp.qltb` → create new plan (Từ chối plan is read-only) → status `Chờ duyệt`.
Switch to `giamdoc` → click Duyệt → status `Đã duyệt`.
Switch to `bp.qltb` → Bắt đầu thực hiện → fill modal → status `Đang thực hiện`.
Switch to `congnhan` → Hoàn thành → fill modal → status `Hoàn thành`.

- [ ] **Step 4: Commit**

```bash
git add static/pages/ke-hoach.html
git commit -m "feat: ke-hoach.html full lifecycle UI with role-based modals"
```

---

## Task 7: su-co.html — incident response UI

**Files:**
- Create: `static/pages/su-co.html`

- [ ] **Step 1: Create static/pages/su-co.html**

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khắc phục sự cố</title>
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .modal-overlay{position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,.5);display:none;align-items:center;justify-content:center;z-index:1000;}
        .modal-box{background:#fff;border-radius:12px;padding:32px;max-width:480px;width:90%;}
        .modal-box h3{margin:0 0 20px;color:#8b3a8f;}
        .text-danger{color:#dc2626;}
        .small-note{font-size:.8rem;color:#888;}
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-inner">
            <a href="/" class="navbar-brand">
                <svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="4" y="8" width="24" height="18" rx="2"/><path d="M10 8V6a2 2 0 012-2h8a2 2 0 012 2v2"/><circle cx="16" cy="17" r="4"/></svg>
                Quản lý Bảo trì thiết bị
            </a>
            <div class="navbar-links">
                <a href="/pages/thiet-bi.html" data-page="thiet-bi">Thiết bị</a>
                <a href="/pages/ke-hoach.html" data-page="ke-hoach">Bảo dưỡng định kỳ</a>
                <a href="/pages/su-co.html" data-page="su-co">Khắc phục sự cố</a>
            </div>
            <div style="display:flex;align-items:center;gap:12px;">
                <span id="navUser" class="small-note"></span>
                <button class="btn btn-secondary btn-sm" onclick="doLogout()">Đăng xuất</button>
            </div>
        </div>
    </nav>

    <div class="page-container">
        <div class="page-header">
            <div class="breadcrumb"><a href="/">Trang chủ</a> / Khắc phục sự cố</div>
            <h1>Quy trình khắc phục sự cố (5.2)</h1>
            <p>Phát hiện sự cố, lập phương án, phê duyệt, sửa chữa và nghiệm thu</p>
        </div>

        <!-- Report incident form — CONG_NHAN only -->
        <div class="card" id="formCard" style="display:none">
            <h2>Báo cáo sự cố mới</h2>
            <div class="form-grid cols-3">
                <div class="form-group"><label>Mã thiết bị</label><input type="text" id="maThietBi"></div>
                <div class="form-group"><label>Tên thiết bị</label><input type="text" id="tenThietBi"></div>
                <div class="form-group"><label>Mức độ</label>
                    <select id="mucDo"><option>Cao</option><option>Trung bình</option><option>Thấp</option></select>
                </div>
                <div class="form-group" style="grid-column:span 3">
                    <label>Mô tả sự cố</label>
                    <textarea id="moTaSuCo" placeholder="Mô tả chi tiết tình trạng sự cố..."></textarea>
                </div>
            </div>
            <div class="form-actions"><button class="btn btn-primary" onclick="addSuCo()">Báo cáo sự cố</button></div>
        </div>

        <div class="card">
            <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;">
                <h2>Danh sách sự cố</h2>
                <button class="btn btn-secondary" onclick="openPdf('/api/pdf/nghiemthu')">Xuất PDF BM.BT.01.04</button>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr>
                        <th>Thiết bị</th><th>Mô tả sự cố</th><th>Mức độ</th>
                        <th>Người báo cáo</th><th>Ngày</th><th>Trạng thái</th><th>Hành động</th>
                    </tr></thead>
                    <tbody id="tableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal: Lập phương án sửa chữa -->
    <div id="modalLapKeHoach" class="modal-overlay">
        <div class="modal-box">
            <h3>Lập phương án sửa chữa</h3>
            <div class="form-group"><label>Phương án sửa chữa <span class="text-danger">*</span></label>
                <textarea id="phuongAnSuaChua" rows="5" placeholder="Mô tả chi tiết phương án sửa chữa, phụ tùng cần chuẩn bị..."></textarea>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" onclick="submitLapKeHoach()">Gửi phê duyệt</button>
                <button class="btn btn-secondary" onclick="closeModal('modalLapKeHoach')">Hủy</button>
            </div>
        </div>
    </div>

    <!-- Modal: Từ chối phương án -->
    <div id="modalTuChoi" class="modal-overlay">
        <div class="modal-box">
            <h3>Từ chối phương án sửa chữa</h3>
            <div class="form-group"><label>Lý do từ chối <span class="text-danger">*</span></label>
                <textarea id="lyDoTuChoi" rows="4" placeholder="Nhập lý do từ chối..."></textarea>
            </div>
            <div class="form-actions">
                <button class="btn btn-danger" onclick="submitTuChoi()">Xác nhận từ chối</button>
                <button class="btn btn-secondary" onclick="closeModal('modalTuChoi')">Hủy</button>
            </div>
        </div>
    </div>

    <!-- Modal: Nghiệm thu -->
    <div id="modalNghiemThu" class="modal-overlay">
        <div class="modal-box">
            <h3>Nghiệm thu sửa chữa</h3>
            <div class="form-group"><label>Kết quả sửa chữa <span class="text-danger">*</span></label>
                <textarea id="ketQuaSuaChua" rows="4" placeholder="Mô tả tình trạng hoạt động sau sửa chữa..."></textarea>
            </div>
            <div class="form-group"><label>Người nghiệm thu</label>
                <input type="text" id="nguoiNghiemThu">
            </div>
            <div class="form-actions">
                <button class="btn btn-success" onclick="submitNghiemThu()">Xác nhận nghiệm thu</button>
                <button class="btn btn-secondary" onclick="closeModal('modalNghiemThu')">Hủy</button>
            </div>
        </div>
    </div>

    <script src="/js/common.js"></script>
    <script>
        let actionId = null;

        async function init() {
            await initNav('su-co');
            if (hasRole('CONG_NHAN')) {
                document.getElementById('formCard').style.display = 'block';
            }
            load();
        }

        async function load() {
            const data = await apiGet(API.suCo);
            if (!data) return;
            document.getElementById('tableBody').innerHTML = data.map(renderRow).join('');
        }

        function renderRow(y) {
            const isBp   = hasRole('BP_QLTB', 'QUAN_LY_TRAM');
            const isGd   = hasRole('GIAM_DOC');
            const canAcc = hasRole('CONG_NHAN', 'QUAN_LY_TRAM');
            const isCn   = hasRole('CONG_NHAN');

            let actions = '';
            if (y.trangThai === 'Phát hiện') {
                if (isBp) actions += `<button class="btn btn-primary btn-sm" onclick="openLapKeHoach('${y.id}')">Lập phương án</button>`;
                if (isCn) actions += `<button class="btn btn-danger btn-sm" onclick="xoa('${y.id}')">Xóa</button>`;
            } else if (y.trangThai === 'Chờ duyệt') {
                if (isGd) {
                    actions += `<button class="btn btn-success btn-sm" onclick="duyet('${y.id}')">Duyệt</button>`;
                    actions += `<button class="btn btn-danger btn-sm" onclick="openTuChoi('${y.id}')">Từ chối</button>`;
                }
            } else if (y.trangThai === 'Đang sửa chữa') {
                if (canAcc) actions += `<button class="btn btn-success btn-sm" onclick="openNghiemThu('${y.id}')">Nghiệm thu</button>`;
            }

            const plan = y.phuongAnSuaChua
                ? `<br><small class="small-note">Phương án: ${y.phuongAnSuaChua.substring(0, 60)}${y.phuongAnSuaChua.length > 60 ? '...' : ''}</small>` : '';
            const note = y.trangThai === 'Từ chối' && y.lyDoTuChoi
                ? `<br><small class="text-danger">Lý do: ${y.lyDoTuChoi}</small>` : '';

            return `<tr>
                <td>${y.tenThietBi || y.maThietBi}</td>
                <td>${y.moTaSuCo || ''}</td>
                <td>${y.mucDo || ''}</td>
                <td>${y.nguoiYeuCau || ''}</td>
                <td>${formatDate(y.ngayYeuCau)}</td>
                <td>${badgeHtml(y.trangThai)}${note}${plan}</td>
                <td class="actions">${actions}</td>
            </tr>`;
        }

        async function addSuCo() {
            const r = await apiPost(API.suCo, {
                maThietBi: getField('maThietBi'),
                tenThietBi: getField('tenThietBi'),
                mucDo: document.getElementById('mucDo').value,
                moTaSuCo: getField('moTaSuCo'),
                nguoiYeuCau: currentUser.hoTen
            });
            if (!r) return;
            clearFields(['maThietBi','tenThietBi','moTaSuCo']);
            load();
        }

        function openLapKeHoach(id) {
            actionId = id;
            document.getElementById('phuongAnSuaChua').value = '';
            showModal('modalLapKeHoach');
        }
        async function submitLapKeHoach() {
            const pa = getField('phuongAnSuaChua');
            if (!pa) { alert('Vui lòng nhập phương án sửa chữa.'); return; }
            const r = await apiPut(`${API.suCo}/${actionId}/lapkehoach`, { phuongAnSuaChua: pa });
            if (r) { closeModal('modalLapKeHoach'); load(); }
        }

        async function duyet(id) {
            if (!confirm('Duyệt phương án sửa chữa này?')) return;
            const r = await apiPut(`${API.suCo}/${id}/duyet`, {});
            if (r) load();
        }

        function openTuChoi(id) {
            actionId = id;
            document.getElementById('lyDoTuChoi').value = '';
            showModal('modalTuChoi');
        }
        async function submitTuChoi() {
            const ly = getField('lyDoTuChoi');
            if (!ly) { alert('Vui lòng nhập lý do từ chối.'); return; }
            const r = await apiPut(`${API.suCo}/${actionId}/tuchoi`, { lyDoTuChoi: ly });
            if (r) { closeModal('modalTuChoi'); load(); }
        }

        function openNghiemThu(id) {
            actionId = id;
            clearFields(['ketQuaSuaChua','nguoiNghiemThu']);
            showModal('modalNghiemThu');
        }
        async function submitNghiemThu() {
            const kq = getField('ketQuaSuaChua');
            if (!kq) { alert('Vui lòng nhập kết quả sửa chữa.'); return; }
            const r = await apiPut(`${API.suCo}/${actionId}/nghiemthu`, {
                ketQuaSuaChua: kq,
                nguoiNghiemThu: getField('nguoiNghiemThu')
            });
            if (r) { closeModal('modalNghiemThu'); load(); }
        }

        async function xoa(id) {
            if (!confirm('Xóa báo cáo sự cố này?')) return;
            await apiDelete(`${API.suCo}/${id}`);
            load();
        }

        init();
    </script>
</body>
</html>
```

- [ ] **Step 2: Test incident lifecycle manually**

Login as `congnhan` → go to `/pages/su-co.html` → report an incident → status `Phát hiện`.
Switch to `bp.qltb` → Lập phương án → fill in → status `Chờ duyệt`.
Switch to `giamdoc` → Duyệt → status `Đang sửa chữa`.
Switch to `congnhan` → Nghiệm thu → fill in → status `Hoàn thành`.

- [ ] **Step 3: Commit**

```bash
git add static/pages/su-co.html
git commit -m "feat: su-co.html incident response lifecycle UI"
```

---

## Task 8: Update index.html and thiet-bi.html

**Files:**
- Modify: `static/index.html`
- Modify: `static/pages/thiet-bi.html`

- [ ] **Step 1: Replace index.html**

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Bảo trì thiết bị</title>
    <link rel="stylesheet" href="/css/styles.css">
    <style>.small-note{font-size:.8rem;color:#888;}</style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-inner">
            <a href="/" class="navbar-brand">
                <svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="4" y="8" width="24" height="18" rx="2"/><path d="M10 8V6a2 2 0 012-2h8a2 2 0 012 2v2"/><circle cx="16" cy="17" r="4"/></svg>
                Quản lý Bảo trì thiết bị
            </a>
            <div class="navbar-links">
                <a href="/pages/thiet-bi.html" data-page="thiet-bi">Thiết bị</a>
                <a href="/pages/ke-hoach.html" data-page="ke-hoach">Bảo dưỡng định kỳ</a>
                <a href="/pages/su-co.html" data-page="su-co">Khắc phục sự cố</a>
            </div>
            <div style="display:flex;align-items:center;gap:12px;">
                <span id="navUser" class="small-note"></span>
                <button class="btn btn-secondary btn-sm" onclick="doLogout()">Đăng xuất</button>
            </div>
        </div>
    </nav>

    <section class="hero">
        <h1>Quản lý Bảo trì Thiết bị</h1>
        <p>Hệ thống quản lý theo quy trình BT.01 — Tín Thành Environment Technology</p>
    </section>

    <section class="features-section">
        <div class="features-grid" style="grid-template-columns:repeat(3,1fr)">
            <a href="/pages/thiet-bi.html" class="feature-card">
                <svg class="feature-icon" viewBox="0 0 64 64" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M14 12h8v8h-8zM26 12h8v8h-8zM38 12h8v8h-8zM14 24h8v8h-8zM26 24h8v8h-8zM38 24h8v8h-8zM14 36h8v8h-8zM26 36h8v8h-8zM38 36h8v8h-8z"/>
                </svg>
                <h3>Danh mục thiết bị</h3>
                <p>BM.BT.01.01 &amp; BM.BT.01.02 — Lý lịch và danh mục máy móc thiết bị</p>
            </a>
            <a href="/pages/ke-hoach.html" class="feature-card">
                <svg class="feature-icon" viewBox="0 0 64 64" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="10" y="6" width="36" height="48" rx="2"/>
                    <path d="M18 18h20M18 26h20M18 34h12"/>
                    <path d="M38 42l14 8v-16l-14 8z"/>
                </svg>
                <h3>Bảo dưỡng định kỳ</h3>
                <p>BM.BT.01.03 — Lập kế hoạch, duyệt, thực hiện và bàn giao bảo dưỡng</p>
            </a>
            <a href="/pages/su-co.html" class="feature-card">
                <svg class="feature-icon" viewBox="0 0 64 64" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="32" cy="32" r="22"/>
                    <path d="M32 20v14M32 42v2"/>
                </svg>
                <h3>Khắc phục sự cố</h3>
                <p>BM.BT.01.04 — Phát hiện sự cố, lập phương án, duyệt, sửa chữa và nghiệm thu</p>
            </a>
        </div>
    </section>

    <script src="/js/common.js"></script>
    <script>
        initNav('');
    </script>
</body>
</html>
```

- [ ] **Step 2: Update thiet-bi.html** — add user navbar, hide create/edit/delete for non-QUAN_LY_TRAM

Open `static/pages/thiet-bi.html`. Make these targeted changes:

**a) Replace the `<div class="navbar-links">` section** with the 3-item navbar matching the other pages (same as ke-hoach.html navbar).

**b) Add user display span + logout button** to the navbar (same pattern as ke-hoach.html).

**c) Wrap the create-form card** in a `<div id="formCard" style="display:none">` and reveal it in JS only if role is QUAN_LY_TRAM.

**d) In the `load()` function**, make the edit/delete action buttons conditional:

```javascript
// At the top of the script block, add:
async function init() {
    await initNav('thiet-bi');
    if (hasRole('QUAN_LY_TRAM')) {
        document.getElementById('formCard').style.display = 'block';
    }
    load();
}

// In the table row rendering (wherever action buttons are built), wrap them:
const canManage = hasRole('QUAN_LY_TRAM');
// then in the actions cell:
`${canManage ? `<button class="btn btn-warning btn-sm" onclick="edit('${tb.id}')">Sửa</button>` : ''}
 ${canManage ? `<button class="btn btn-danger btn-sm" onclick="xoa('${tb.id}')">Xóa</button>` : ''}`
```

**e) Change the initial call** from `load()` to `init()` at the bottom of the script.

- [ ] **Step 3: Final end-to-end test**

Start app. Visit `http://localhost:8080` → redirected to login.
Login as each of the 5 users and verify the homepage shows 3 cards.
Verify thiet-bi.html: `congnhan` cannot see create form or edit/delete buttons; `admin.tram` can.
Verify `/pages/giam-sat.html` returns 404.

- [ ] **Step 4: Final commit**

```bash
git add -A
git commit -m "feat: update index.html and thiet-bi.html for 3-module navigation with role-based UI"
```
