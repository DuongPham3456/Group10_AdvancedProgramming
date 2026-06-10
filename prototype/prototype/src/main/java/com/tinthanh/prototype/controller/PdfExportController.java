package com.tinthanh.prototype.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.tinthanh.prototype.model.CongViec;
import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.model.ThongSoMay;
import com.tinthanh.prototype.repository.CongViecRepository;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.ThongSoRepository;
import com.tinthanh.prototype.service.PdfService;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin("*")
public class PdfExportController {

    @Autowired private PdfService pdfService;
    @Autowired private ThietBiRepository thietBiRepo;
    @Autowired private KeHoachRepository keHoachRepo;
    @Autowired private CongViecRepository congViecRepo;
    @Autowired private ThongSoRepository thongSoRepo;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping(value = "/thietbi/lylich/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportThietBiLyLich(@PathVariable UUID id) throws IOException {
        ThietBiLyLich thietBi = thietBiRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"));

        String html = buildLyLichHtml(thietBi);
        return createPdfResponse(html, "BM.BT.01.01_LyLichThietBi.pdf");
    }

    @GetMapping(value = "/thietbi/danhmuc", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportDanhMucThietBi() throws IOException {
        List<ThietBiLyLich> items = thietBiRepo.findAll();
        String html = buildDanhMucHtml(items);
        return createPdfResponse(html, "BM.BT.01.02_DanhMucThietBi.pdf");
    }

    @GetMapping(value = "/kehoach", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportKeHoach() throws IOException {
        List<KeHoachBaoTri> items = keHoachRepo.findAll();
        String html = buildKeHoachHtml(items);
        return createPdfResponse(html, "BM.BT.01.03_KeHoachBaoDuong.pdf");
    }

    @GetMapping(value = "/nghiemthu", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportBienBanNghiemThu() throws IOException {
        List<CongViec> tasks = congViecRepo.findAll();
        String html = buildBienBanHtml(tasks);
        return createPdfResponse(html, "BM.BT.01.04_BienBanNghiemThu.pdf");
    }

    @GetMapping(value = "/so-theo-doi", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSoTheoDoi() throws IOException {
        List<ThietBiLyLich> devices = thietBiRepo.findAll();
        List<ThongSoMay> logs = thongSoRepo.findAll();
        String html = buildSoTheoDoiHtml(devices, logs);
        return createPdfResponse(html, "BM.BT.01.05_SoTheoDoiBaoDuong.pdf");
    }

    private ResponseEntity<byte[]> createPdfResponse(String html, String filename) throws IOException {
        String baseUri = PdfExportController.class.getResource("/static/") != null ? PdfExportController.class.getResource("/static/").toString() : "";
        byte[] bytes = pdfService.renderPdf(html, baseUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private String buildLyLichHtml(ThietBiLyLich tb) {
        return "<html><head><meta charset='utf-8'/><style>"
                + "body{font-family:'Noto Sans', Arial, sans-serif;margin:0;padding:0;}"
                + " .page{padding:24px;}"
                + " .form-code{float:right;font-weight:bold;margin-bottom:8px;}"
                + " h1{text-align:center;margin:0 0 16px;}"
                + " table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + " th,td{border:1px solid #333;padding:10px;vertical-align:top;}"
                + " th{background:#f2f2f2;text-align:left;width:35%;}"
                + " .footer-table{width:100%;border:none;margin-top:32px;}"
                + " .footer-table td{border:none;padding:12px 8px;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.01</div>"
                + "<h1>LÝ LỊCH MÁY MÓC THIẾT BỊ</h1>"
                + "<table>"
                + row("Tên máy móc, thiết bị", tb.getTenThietBi())
                + row("Mã số quản lý", tb.getMaSoQuanLy())
                + row("Model/Type", tb.getModelType())
                + row("Số serial", tb.getSoSerial())
                + row("Nhà sản xuất", tb.getNhaSanXuat())
                + row("Năm sản xuất", tb.getNamSanXuat() == null ? "" : String.valueOf(tb.getNamSanXuat()))
                + row("Đặc điểm máy móc, thiết bị", safe(tb.getDacDiem()))
                + row("Ngày tiếp nhận", tb.getNgayTiepNhan() == null ? "" : tb.getNgayTiepNhan().format(DATE_FORMAT))
                + row("Ngày đưa vào sử dụng", tb.getNgayDuaVaoSuDung() == null ? "" : tb.getNgayDuaVaoSuDung().format(DATE_FORMAT))
                + row("Phụ tùng kèm theo (nếu có)", safe(tb.getPhuTungKemTheo()))
                + row("Tài liệu kèm theo (nếu có)", safe(tb.getTaiLieuKemTheo()))
                + row("Tình trạng máy móc thiết bị lúc tiếp nhận (đánh giá theo % nếu có thể)", safe(tb.getTinhTrangTiepNhan()))
                + "</table>"
                + "<table class='footer-table'>"
                + "<tr><td>Người lập</td><td>Phê duyệt</td></tr>"
                + "</table>"
                + "</div></body></html>";
    }

    private String buildDanhMucHtml(List<ThietBiLyLich> items) {
        String rows = "";
        int stt = 1;
        for (ThietBiLyLich tb : items) {
            rows += "<tr>"
                    + "<td>" + stt++ + "</td>"
                    + "<td>" + safe(tb.getTenThietBi()) + "</td>"
                    + "<td>" + safe(tb.getMaSoQuanLy()) + "</td>"
                    + "<td>1</td>"
                    + "<td>" + safe(tb.getNhaSanXuat()) + "</td>"
                    + "<td>" + safe(tb.getModelType()) + "</td>"
                    + "<td>" + (tb.getNamSanXuat() == null ? "" : tb.getNamSanXuat()) + "</td>"
                    + "<td>" + safe(tb.getTrangThaiVanHanh()) + "</td>"
                    + "</tr>";
        }
        if (items.isEmpty()) {
            rows = "<tr><td colspan='8' style='text-align:center;color:#555;'>Không có dữ liệu</td></tr>";
        }
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans', Arial, sans-serif;} .form-code{float:right;font-weight:bold;} h1{text-align:center;}table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;text-align:left;}</style></head><body>"
                + "<div class='form-code'>BM.BT.01.02</div>"
                + "<h1>DANH MỤC THIẾT BỊ, MÁY MÓC</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu</th><th>Số lượng</th><th>Xuất xứ</th><th>Thông số kỹ thuật</th><th>Năm sản xuất</th><th>Ghi chú</th></tr></thead><tbody>"
                + rows
                + "</tbody></table></body></html>";
    }

    private String buildKeHoachHtml(List<KeHoachBaoTri> items) {
        String rows = "";
        int stt = 1;
        for (KeHoachBaoTri k : items) {
            List<Integer> months = List.of();
            if (k.getNgayBatDau() != null && k.getNgayKetThuc() != null) {
                months = k.getNgayBatDau().datesUntil(k.getNgayKetThuc().plusDays(1))
                        .map(d -> d.getMonthValue())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
            }
            String monthCells = "";
            for (int m = 1; m <= 12; m++) {
                monthCells += "<td>" + (months.contains(m) ? "X" : "") + "</td>";
            }
            rows += "<tr>"
                    + "<td>" + stt++ + "</td>"
                    + "<td>" + safe(k.getTenThietBi()) + "</td>"
                    + "<td>" + safe(k.getMaThietBi()) + "</td>"
                    + "<td>" + safe(k.getLoaiBaoTri()) + "</td>"
                    + monthCells
                    + "<td>" + safe(k.getTrangThai()) + "</td>"
                    + "</tr>";
        }
        if (items.isEmpty()) {
            rows = "<tr><td colspan='16' style='text-align:center;color:#555;'>Không có dữ liệu</td></tr>";
        }
        return "<html><head><meta charset='utf-8'/><style>"
                + "body{font-family:'Noto Sans', Arial, sans-serif;margin:0;padding:0;}"
                + " .page{padding:24px;}"
                + " .form-code{float:right;font-weight:bold;margin-bottom:8px;}"
                + " h1{text-align:center;margin:0 0 8px;}"
                + " .subtitle{text-align:center;margin:0 0 18px;font-size:14px;}"
                + " table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + " th,td{border:1px solid #333;padding:8px;text-align:center;}"
                + " th{background:#f2f2f2;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.03</div>"
                + "<h1>KẾ HOẠCH BẢO DƯỠNG, HIỆU CHUẨN, KIỂM ĐỊNH</h1>"
                + "<p class='subtitle'>Năm " + java.time.Year.now().getValue() + "</p>"
                + "<table><thead><tr><th>Stt</th><th>Tên máy móc, thiết bị</th><th>Mã số</th><th>Bộ phận sử dụng</th>"
                + "<th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th><th>7</th><th>8</th><th>9</th><th>10</th><th>11</th><th>12</th><th>Ghi chú</th></tr></thead><tbody>"
                + rows
                + "</tbody></table>"
                + "</div></body></html>";
    }

    private String buildBienBanHtml(List<com.tinthanh.prototype.model.CongViec> tasks) {
        String rows = "";
        int stt = 1;
        for (com.tinthanh.prototype.model.CongViec task : tasks) {
            rows += "<tr>"
                    + "<td>" + stt++ + "</td>"
                    + "<td>" + safe(task.getMaThietBi()) + " - " + safe(task.getTenCongViec()) + "</td>"
                    + "<td>1</td>"
                    + "<td>" + safe(task.getTrangThai()) + "</td>"
                    + "<td>" + safe(task.getMoTa()) + "</td>"
                    + "</tr>";
        }
        if (tasks.isEmpty()) {
            rows = "<tr><td colspan='5' style='text-align:center;color:#555;'>Không có công việc nghiệm thu</td></tr>";
        }
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans', Arial, sans-serif;} .form-code{float:right;font-weight:bold;} h1{text-align:center;}table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;text-align:left;}</style></head><body>"
                + "<div class='form-code'>BM.BT.01.04</div>"
                + "<h1>BIÊN BẢN NGHIỆM THU</h1>"
                + "<p>Căn cứ đơn đề nghị và biên bản kiểm tra kỹ thuật đã ký, chúng tôi tiến hành kiểm tra, đánh giá tình hình hoạt động và thống nhất với kết quả nghiệm thu như sau:</p>"
                + "<table><thead><tr><th>TT</th><th>Tên TB sửa chữa - thay thế, xuất xứ</th><th>Số lượng</th><th>Tình hình hoạt động</th><th>Ghi chú</th></tr></thead><tbody>"
                + rows
                + "</tbody></table>"
                + "<p style='margin-top:24px;'>Đại diện hai bên ký xác nhận:</p>"
                + "<table style='width:100%;margin-top:24px;'><tr><td style='border:none;'>ĐẠI DIỆN BÊN A</td><td style='border:none;'>ĐẠI DIỆN BÊN B</td></tr></table>"
                + "</body></html>";
    }

    private String buildSoTheoDoiHtml(List<ThietBiLyLich> devices, List<ThongSoMay> logs) {
        Map<String, ThongSoMay> latestByMa = new HashMap<>();
        for (ThongSoMay log : logs) {
            String key = log.getMaThietBi() == null ? "" : log.getMaThietBi();
            latestByMa.compute(key, (k, current) -> {
                if (current == null) return log;
                return log.getThoiGianGhi() != null && current.getThoiGianGhi() != null
                        ? (log.getThoiGianGhi().isAfter(current.getThoiGianGhi()) ? log : current)
                        : (current.getThoiGianGhi() == null ? log : current);
            });
        }
        String rows = "";
        int stt = 1;
        for (ThietBiLyLich tb : devices.stream().sorted(Comparator.comparing(ThietBiLyLich::getTenThietBi, Comparator.nullsFirst(String::compareTo))).collect(Collectors.toList())) {
            ThongSoMay latest = latestByMa.get(tb.getMaSoQuanLy());
            String date = latest != null && latest.getThoiGianGhi() != null ? latest.getThoiGianGhi().format(DATE_FORMAT) : "";
            String note = latest != null ? safe(latest.getTenThongSo()) + ": " + safe(latest.getGiaTri()) + " " + safe(latest.getDonVi()) : "";
            rows += "<tr>"
                    + "<td>" + stt++ + "</td>"
                    + "<td>" + safe(tb.getTenThietBi()) + "</td>"
                    + "<td>" + safe(tb.getModelType()) + "</td>"
                    + "<td>" + safe(tb.getModelType()) + "</td>"
                    + "<td>" + (tb.getNamSanXuat() == null ? "" : tb.getNamSanXuat()) + "</td>"
                    + "<td>" + date + "</td>"
                    + "<td>" + safe(tb.getTrangThaiVanHanh()) + "</td>"
                    + "<td>" + note + "</td>"
                    + "</tr>";
        }
        if (devices.isEmpty()) {
            rows = "<tr><td colspan='8' style='text-align:center;color:#555;'>Không có dữ liệu</td></tr>";
        }
        return "<html><head><meta charset='utf-8'/><style>body{font-family:'Noto Sans', Arial, sans-serif;} .form-code{float:right;font-weight:bold;} h1{text-align:center;}table{width:100%;border-collapse:collapse;margin-top:16px;}th,td{border:1px solid #333;padding:8px;}th{background:#f2f2f2;text-align:left;}</style></head><body>"
                + "<div class='form-code'>BM.BT.01.05</div>"
                + "<h1>SỔ THEO DÕI BẢO DƯỠNG MÁY MÓC, THIẾT BỊ</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu</th><th>Thông số kỹ thuật</th><th>Năm sản xuất</th><th>Ngày kiểm tra</th><th>Tình trạng</th><th>Ghi chú</th></tr></thead><tbody>"
                + rows
                + "</tbody></table></body></html>";
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private String row(String label, Object value) {
        return "<tr><th style='width:30%;text-align:left;padding:10px;'>" + label + "</th><td>" + safe(value) + "</td></tr>";
    }
}
