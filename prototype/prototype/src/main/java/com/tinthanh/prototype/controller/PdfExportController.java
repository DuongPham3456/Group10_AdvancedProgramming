package com.tinthanh.prototype.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final PdfService pdfService;
    private final ThietBiRepository thietBiRepo;
    private final KeHoachRepository keHoachRepo;
    private final YeuCauRepository yeuCauRepo;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PdfExportController(PdfService pdfService,
                               ThietBiRepository thietBiRepo,
                               KeHoachRepository keHoachRepo,
                               YeuCauRepository yeuCauRepo) {
        this.pdfService = pdfService;
        this.thietBiRepo = thietBiRepo;
        this.keHoachRepo = keHoachRepo;
        this.yeuCauRepo = yeuCauRepo;
    }

    @PreAuthorize("hasAnyAuthority('QUAN_LY_TRAM','CONG_NHAN')")
    @GetMapping(value = "/thietbi/lylich/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportThietBiLyLich(@PathVariable UUID id) throws IOException {
        ThietBiLyLich tb = thietBiRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"));
        return pdf(buildLyLichHtml(tb), "BM.BT.01.01_LyLichThietBi.pdf");
    }

    @PreAuthorize("hasAuthority('QUAN_LY_TRAM')")
    @GetMapping(value = "/thietbi/danhmuc", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportDanhMucThietBi() throws IOException {
        return pdf(buildDanhMucHtml(thietBiRepo.findAll()), "BM.BT.01.02_DanhMucThietBi.pdf");
    }

    @PreAuthorize("hasAuthority('BP_QLTB')")
    @GetMapping(value = "/kehoach", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportKeHoach() throws IOException {
        return pdf(buildKeHoachHtml(keHoachRepo.findAll()), "BM.BT.01.03_KeHoachBaoDuong.pdf");
    }

    @PreAuthorize("hasAnyAuthority('BP_QLTB','CONG_NHAN')")
    @GetMapping(value = "/nghiemthu", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportBienBanNghiemThu() throws IOException {
        List<YeuCauBaoTri> done = yeuCauRepo.findAll().stream()
                .filter(y -> "Hoàn thành".equals(y.getTrangThai()))
                .collect(Collectors.toList());
        return pdf(buildBienBanHtml(done), "BM.BT.01.04_BienBanNghiemThu.pdf");
    }

    @PreAuthorize("hasAuthority('CONG_NHAN')")
    @GetMapping(value = "/so-theo-doi", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSoTheoDoi() throws IOException {
        List<KeHoachBaoTri> done = keHoachRepo.findAll().stream()
                .filter(k -> "Hoàn thành".equals(k.getTrangThai()))
                .collect(Collectors.toList());
        return pdf(buildSoTheoDoiHtml(done), "BM.BT.01.05_SoTheoDoiBaoDuong.pdf");
    }

    private ResponseEntity<byte[]> pdf(String html, String filename) throws IOException {
        String baseUri = PdfExportController.class.getResource("/static/") != null
                ? PdfExportController.class.getResource("/static/").toString() : "";
        byte[] bytes = pdfService.renderPdf(html, baseUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private String buildLyLichHtml(ThietBiLyLich tb) {
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:11pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;margin-bottom:8px;font-size:10pt;}"
                + "h1{text-align:center;margin:0 0 12px;font-size:14pt;font-weight:bold;}"
                + "table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + "th,td{border:1px solid #000;padding:8px;vertical-align:top;}"
                + "th{background:#e8e8e8;text-align:left;width:40%;font-weight:bold;}"
                + ".footer-table{width:100%;border:none;margin-top:24px;}"
                + ".footer-table td{border:none;padding:20px 8px;text-align:center;}"
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
                + row("Trạng thái vận hành", safe(tb.getTrangThaiVanHanh()))
                + row("Đặc điểm máy móc, thiết bị", safe(tb.getDacDiem()))
                + row("Ngày tiếp nhận", tb.getNgayTiepNhan() == null ? "" : tb.getNgayTiepNhan().format(DATE_FORMAT))
                + row("Ngày đưa vào sử dụng", tb.getNgayDuaVaoSuDung() == null ? "" : tb.getNgayDuaVaoSuDung().format(DATE_FORMAT))
                + row("Phụ tùng kèm theo (nếu có)", safe(tb.getPhuTungKemTheo()))
                + row("Tài liệu kèm theo (nếu có)", safe(tb.getTaiLieuKemTheo()))
                + row("Tình trạng lúc tiếp nhận", safe(tb.getTinhTrangTiepNhan()))
                + row("Tình trạng vệ sinh", safe(tb.getTinhTrangVeSinh()))
                + row("Ngày vệ sinh cuối", tb.getNgayVeSinhCuoi() == null ? "" : tb.getNgayVeSinhCuoi().format(DATE_FORMAT))
                + row("Người quản lý trạm", safe(tb.getNguoiQuanLyTram()))
                + row("Ghi chú bảo trì", safe(tb.getGhiChuBaoTri()))
                + "</table>"
                + "<table class='footer-table'><tr>"
                + "<td>Người lập<br/><br/>..............................</td>"
                + "<td>Phê duyệt<br/><br/>..............................</td>"
                + "</tr></table>"
                + "</div></body></html>";
    }

    private String buildDanhMucHtml(List<ThietBiLyLich> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (ThietBiLyLich tb : items) {
            rows.append("<tr>")
                .append("<td>").append(stt++).append("</td>")
                .append("<td>").append(safe(tb.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(tb.getMaSoQuanLy())).append("</td>")
                .append("<td>1</td>")
                .append("<td>").append(safe(tb.getNhaSanXuat())).append("</td>")
                .append("<td>").append(safe(tb.getModelType())).append("</td>")
                .append("<td>").append(tb.getNamSanXuat() == null ? "" : tb.getNamSanXuat()).append("</td>")
                .append("<td>").append(safe(tb.getTrangThaiVanHanh())).append("</td>")
                .append("</tr>");
        }
        if (items.isEmpty()) {
            rows.append("<tr><td colspan='8' style='text-align:center;'>Không có dữ liệu</td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:11pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;font-size:10pt;}"
                + "h1{text-align:center;font-size:14pt;font-weight:bold;margin:0 0 12px;}"
                + "table{width:100%;border-collapse:collapse;margin-top:12px;}"
                + "th,td{border:1px solid #000;padding:8px;font-size:10pt;}th{background:#e8e8e8;font-weight:bold;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.02</div>"
                + "<h1>DANH MỤC THIẾT BỊ, MÁY MÓC</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu/Mã số</th><th>Số lượng</th>"
                + "<th>Xuất xứ</th><th>Thông số kỹ thuật</th><th>Năm sản xuất</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "</div></body></html>";
    }

    private String buildKeHoachHtml(List<KeHoachBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (KeHoachBaoTri k : items) {
            List<Integer> months = List.of();
            if (k.getNgayBatDau() != null && k.getNgayKetThuc() != null) {
                months = k.getNgayBatDau().datesUntil(k.getNgayKetThuc().plusDays(1))
                        .map(d -> d.getMonthValue()).distinct().sorted().collect(Collectors.toList());
            }
            StringBuilder monthCells = new StringBuilder();
            for (int m = 1; m <= 12; m++) {
                monthCells.append("<td style='text-align:center;'>").append(months.contains(m) ? "X" : "").append("</td>");
            }
            rows.append("<tr>")
                .append("<td>").append(stt++).append("</td>")
                .append("<td>").append(safe(k.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(k.getMaThietBi())).append("</td>")
                .append("<td>").append(safe(k.getLoaiBaoTri())).append("</td>")
                .append(monthCells)
                .append("<td>").append(safe(k.getTrangThai())).append("</td>")
                .append("</tr>");
        }
        if (items.isEmpty()) {
            rows.append("<tr><td colspan='17' style='text-align:center;'>Không có dữ liệu</td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:10pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;font-size:10pt;}"
                + "h1{text-align:center;margin:0 0 8px;font-size:13pt;font-weight:bold;}"
                + ".subtitle{text-align:center;margin:0 0 12px;font-size:11pt;}"
                + "table{width:100%;border-collapse:collapse;margin-top:8px;}"
                + "th,td{border:1px solid #000;padding:6px;text-align:center;font-size:9pt;}th{background:#e8e8e8;font-weight:bold;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.03</div>"
                + "<h1>KẾ HOẠCH BẢO DƯỠNG, HIỆU CHUẨN, KIỂM ĐỊNH</h1>"
                + "<p class='subtitle'>Năm " + java.time.Year.now().getValue() + "</p>"
                + "<table><thead><tr><th>Stt</th><th>Tên máy móc, thiết bị</th><th>Mã số</th><th>Loại bảo trì</th>"
                + "<th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th>"
                + "<th>7</th><th>8</th><th>9</th><th>10</th><th>11</th><th>12</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></div></body></html>";
    }

    private String buildBienBanHtml(List<YeuCauBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (YeuCauBaoTri y : items) {
            rows.append("<tr>")
                .append("<td>").append(stt++).append("</td>")
                .append("<td>").append(safe(y.getMaThietBi())).append("<br/>").append(safe(y.getTenThietBi())).append("</td>")
                .append("<td style='text-align:center;'>1</td>")
                .append("<td>").append(safe(y.getKichThicTinhHoatDong())).append("</td>")
                .append("<td>").append(safe(y.getNguoiNghiemThu())).append("</td>")
                .append("<td>").append(y.getNgayNghiemThu() == null ? "" : y.getNgayNghiemThu().format(DATE_FORMAT)).append("</td>")
                .append("</tr>");
        }
        if (items.isEmpty()) {
            rows.append("<tr><td colspan='6' style='text-align:center;'>Chưa có nghiệm thu hoàn thành</td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:11pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;font-size:10pt;}"
                + "h1{text-align:center;font-size:14pt;font-weight:bold;margin:0 0 12px;}"
                + ".intro{margin:12px 0;line-height:1.6;}"
                + "table{width:100%;border-collapse:collapse;margin-top:12px;}"
                + "th,td{border:1px solid #000;padding:8px;font-size:10pt;}th{background:#e8e8e8;font-weight:bold;}"
                + ".sig-table{width:100%;margin-top:32px;border:none;}"
                + ".sig-table td{border:none;text-align:center;padding:40px 8px 8px;vertical-align:top;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.04</div><h1>BIÊN BẢN NGHIỆM THU</h1>"
                + "<p class='intro'>Căn cứ vào biên bản kiểm tra kỹ thuật đã được ký phê duyệt, kết quả nghiệm thu bảo dưỡng như sau:</p>"
                + "<table><thead><tr><th>TT</th><th>Tên TB sửa chữa – thay thế</th><th>Số lượng</th>"
                + "<th>Tình hình hoạt động</th><th>Người nghiệm thu</th><th>Ngày nghiệm thu</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "<table class='sig-table'><tr>"
                + "<td><strong>Người thực hiện</strong><br/>..............................</td>"
                + "<td><strong>Người nghiệm thu</strong><br/>..............................</td>"
                + "<td><strong>Quản lý trạm</strong><br/>..............................</td>"
                + "</tr></table>"
                + "</div></body></html>";
    }

    private String buildSoTheoDoiHtml(List<KeHoachBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (KeHoachBaoTri k : items) {
            // try to find related completed YeuCau (repair) entry for additional handover/times
            YeuCauBaoTri related = yeuCauRepo.findAll().stream()
                    .filter(y -> "Hoàn thành".equals(y.getTrangThai()) && k.getMaThietBi() != null
                            && k.getMaThietBi().equalsIgnoreCase(y.getMaThietBi()))
                    .max((a, b) -> {
                        java.time.LocalDate ad = a.getNgayNghiemThu() != null ? a.getNgayNghiemThu() : a.getNgayBanGiao();
                        java.time.LocalDate bd = b.getNgayNghiemThu() != null ? b.getNgayNghiemThu() : b.getNgayBanGiao();
                        if (ad == null && bd == null) return 0;
                        if (ad == null) return -1;
                        if (bd == null) return 1;
                        return ad.compareTo(bd);
                    }).orElse(null);

            String yeuCauNgayPhatHien = related != null && related.getNgayYeuCau() != null ? related.getNgayYeuCau().format(DATE_FORMAT) : "";
            String yeuCauNgayKetThuc = related != null && related.getNgayNghiemThu() != null ? related.getNgayNghiemThu().format(DATE_FORMAT)
                    : (related != null && related.getNgayBanGiao() != null ? related.getNgayBanGiao().format(DATE_FORMAT) : "");

            rows.append("<tr>")
                .append("<td>").append(stt++).append("</td>")
                .append("<td>").append(safe(k.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(k.getMaThietBi())).append("</td>")
                .append("<td>").append(safe(k.getLoaiBaoTri())).append("</td>")
                .append("<td>").append(yeuCauNgayPhatHien).append("</td>")
                .append("<td>").append(k.getNgayThucHienThucTe() == null ? "" : k.getNgayThucHienThucTe().format(DATE_FORMAT)).append("</td>")
                .append("<td>").append(yeuCauNgayKetThuc).append("</td>")
                .append("<td>").append(safe(k.getNguoiThucHien())).append("</td>")
                .append("<td>").append(related != null ? safe(related.getNguoiBanGiao()) : "").append("</td>")
                .append("<td>").append(related != null ? safe(related.getNguoiTiepNhan()) : "").append("</td>")
                .append("<td>").append(safe(k.getDonViThueNgoai())).append("</td>")
                .append("<td>").append(safe(k.getPhuTungThayThe())).append("</td>")
                .append("<td>").append(safe(k.getKetQuaBaoDuong())).append("</td>")
                .append("</tr>");
        }
        if (items.isEmpty()) {
            rows.append("<tr><td colspan='13' style='text-align:center;'>Chưa có bảo dưỡng hoàn thành</td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:9pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;font-size:10pt;}"
                + "h1{text-align:center;font-size:13pt;font-weight:bold;margin:0 0 12px;}"
                + "table{width:100%;border-collapse:collapse;margin-top:12px;}"
                + "th,td{border:1px solid #000;padding:6px;font-size:8.5pt;}th{background:#e8e8e8;font-weight:bold;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.05</div>"
                + "<h1>SỔ THEO DÕI BẢO DƯỠNG MÁY MÓC, THIẾT BỊ</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên thiết bị</th><th>Ký hiệu</th><th>Loại bảo trì</th>"
                + "<th>Ngày phát hiện</th><th>Ngày thực hiện</th><th>Ngày kết thúc</th><th>Người thực hiện</th><th>Người bàn giao</th><th>Người tiếp nhận</th><th>Đơn vị thuê ngoài</th><th>Phụ tùng thay thế</th><th>Kết quả</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></div></body></html>";
    }

    private String safe(Object value) { return value == null ? "" : value.toString(); }

    private String row(String label, Object value) {
        return "<tr><th>" + label + "</th><td>" + safe(value) + "</td></tr>";
    }
}
