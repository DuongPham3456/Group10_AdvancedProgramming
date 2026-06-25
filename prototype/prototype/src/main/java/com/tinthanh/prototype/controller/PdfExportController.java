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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tinthanh.prototype.model.DanhMucThietBi;
import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.model.NghiemThu;
import com.tinthanh.prototype.model.SoTheoDoi;
import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.repository.DanhMucRepository;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.NghiemThuRepository;
import com.tinthanh.prototype.repository.SoTheoDoiRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.service.PdfService;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin("*")
public class PdfExportController {

    private final PdfService pdfService;
    private final ThietBiRepository thietBiRepo;
    private final KeHoachRepository keHoachRepo;
    private final NghiemThuRepository nghiemThuRepo;
    private final SoTheoDoiRepository soTheoDoiRepo;
    private final DanhMucRepository danhMucRepo;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PdfExportController(PdfService pdfService,
                               ThietBiRepository thietBiRepo,
                               KeHoachRepository keHoachRepo,
                               NghiemThuRepository nghiemThuRepo,
                               SoTheoDoiRepository soTheoDoiRepo,
                               DanhMucRepository danhMucRepo) {
        this.pdfService = pdfService;
        this.thietBiRepo = thietBiRepo;
        this.keHoachRepo = keHoachRepo;
        this.nghiemThuRepo = nghiemThuRepo;
        this.soTheoDoiRepo = soTheoDoiRepo;
        this.danhMucRepo = danhMucRepo;
    }

    // PDF exports are read-only catalog items, open to any authenticated user.
    @GetMapping(value = "/thietbi/lylich/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportThietBiLyLich(@PathVariable UUID id) throws IOException {
        ThietBiLyLich tb = thietBiRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thiết bị không tồn tại"));
        return pdf(buildLyLichHtml(tb), "BM.BT.01.01_LyLichThietBi.pdf");
    }

    @GetMapping(value = "/thietbi/danhmuc", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportDanhMucThietBi() throws IOException {
        return pdf(buildDanhMucHtml(danhMucRepo.findAll()), "BM.BT.01.02_DanhMucThietBi.pdf");
    }

    @GetMapping(value = "/kehoach", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportKeHoach() throws IOException {
        return pdf(buildKeHoachHtml(keHoachRepo.findAll()), "BM.BT.01.03_KeHoachBaoDuong.pdf");
    }

    // 01.04 — biên bản nghiệm thu (dữ liệu nhập từ trang Nghiệm thu)
    @GetMapping(value = "/nghiemthu", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportBienBanNghiemThu() throws IOException {
        return pdf(buildBienBanHtml(nghiemThuRepo.findAll()), "BM.BT.01.04_BienBanNghiemThu.pdf");
    }

    @GetMapping(value = "/so-theo-doi", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSoTheoDoi() throws IOException {
        return pdf(buildSoTheoDoiHtml(soTheoDoiRepo.findAll()), "BM.BT.01.05_SoTheoDoiBaoDuong.pdf");
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

    private String buildDanhMucHtml(List<DanhMucThietBi> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (DanhMucThietBi tb : items) {
            rows.append("<tr>")
                .append("<td>").append(stt++).append("</td>")
                .append("<td>").append(safe(tb.getTenThietBi())).append("</td>")
                .append("<td>").append(safe(tb.getKyHieu())).append("</td>")
                .append("<td>").append(tb.getSoLuong() == null ? "" : tb.getSoLuong()).append("</td>")
                .append("<td>").append(safe(tb.getXuatXu())).append("</td>")
                .append("<td>").append(safe(tb.getThongSoKyThuat())).append("</td>")
                .append("<td>").append(tb.getNamSanXuat() == null ? "" : tb.getNamSanXuat()).append("</td>")
                .append("<td>").append(safe(tb.getGhiChu())).append("</td>")
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
                + "<table><thead><tr><th>Stt</th><th>Tên Thiết bị</th><th>Ký hiệu</th><th>Số lượng</th>"
                + "<th>Xuất xứ</th><th>Thông số kỹ thuật</th><th>Năm sản xuất</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "</div></body></html>";
    }

    private String buildKeHoachHtml(List<KeHoachBaoTri> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (KeHoachBaoTri k : items) {
            List<Integer> months = List.of();
            if (k.getNgayBatDau() != null && k.getNgayKetThuc() != null
                    && !k.getNgayKetThuc().isBefore(k.getNgayBatDau())) {
                months = k.getNgayBatDau().datesUntil(k.getNgayKetThuc().plusDays(1))
                        .map(d -> d.getMonthValue()).distinct().sorted().collect(Collectors.toList());
            }
            String code = loaiCode(k.getLoaiBaoTri());
            StringBuilder monthCells = new StringBuilder();
            for (int m = 1; m <= 12; m++) {
                monthCells.append("<td style='text-align:center;'>").append(months.contains(m) ? code : "").append("</td>");
            }
            ThietBiLyLich tb = deviceByMa(k.getMaThietBi());
            String boPhan = tb != null ? safe(tb.getNguoiQuanLyTram()) : "";
            rows.append("<tr>")
                .append("<td style='text-align:center;'>").append(stt++).append("</td>")
                .append("<td>").append(safe(k.getTenThietBi())).append("</td>")
                .append("<td style='text-align:center;'>").append(safe(k.getMaThietBi())).append("</td>")
                .append("<td>").append(boPhan).append("</td>")
                .append(monthCells)
                .append("<td>").append(safe(k.getLoaiBaoTri())).append("</td>")
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
                + "h1{text-align:center;margin:0 0 4px;font-size:13pt;font-weight:bold;}"
                + ".subtitle{text-align:center;margin:0 0 8px;font-size:11pt;font-weight:bold;}"
                + ".legend{margin:0 0 10px;font-size:9pt;}"
                + ".legend span{margin-right:28px;}"
                + "table{width:100%;border-collapse:collapse;margin-top:6px;}"
                + "th,td{border:1px solid #000;padding:6px;font-size:9pt;}th{background:#e8e8e8;font-weight:bold;text-align:center;}"
                + "</style></head><body><div class='page'>"
                + "<div class='form-code'>BM.BT.01.03</div>"
                + "<h1>KẾ HOẠCH BẢO DƯỠNG, HIỆU CHUẨN, KIỂM ĐỊNH</h1>"
                + "<p class='subtitle'>NĂM " + java.time.Year.now().getValue() + "</p>"
                + "<div class='legend'><span>Bảo dưỡng: BD</span><span>Hiệu chuẩn: HC</span>"
                + "<span>Kiểm định: KĐ</span><span>Đột xuất: ĐX</span></div>"
                + "<table><thead>"
                + "<tr><th rowspan='2'>Stt</th><th rowspan='2'>Tên máy móc, thiết bị</th><th rowspan='2'>Mã số</th>"
                + "<th rowspan='2'>Bộ phận sử dụng</th><th colspan='12'>Tháng thực hiện</th><th rowspan='2'>Ghi chú</th></tr>"
                + "<tr><th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th>"
                + "<th>7</th><th>8</th><th>9</th><th>10</th><th>11</th><th>12</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table></div></body></html>";
    }

    // 01.04 — biên bản nghiệm thu (dữ liệu nhập từ trang Nghiệm thu)
    private String buildBienBanHtml(List<NghiemThu> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (NghiemThu n : items) {
            rows.append("<tr>")
                .append("<td style='text-align:center;'>").append(stt++).append("</td>")
                .append("<td>").append(safe(n.getTenThietBi())).append("</td>")
                .append("<td style='text-align:center;'>").append(n.getSoLuong() == null ? "" : n.getSoLuong()).append("</td>")
                .append("<td>").append(safe(n.getTinhHinhHoatDong())).append("</td>")
                .append("<td>").append(safe(n.getGhiChu())).append("</td>")
                .append("</tr>");
        }
        for (int i = items.size(); i < 3; i++) {
            rows.append("<tr><td style='text-align:center;'>").append(i + 1).append("</td><td></td><td></td><td></td><td></td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:11pt;margin:0;padding:0;}"
                + ".page{padding:24px;}"
                + ".form-code{text-align:center;}"
                + "h1{text-align:center;font-size:14pt;font-weight:bold;margin:0 0 18px;}"
                + "p{margin:4px 0;line-height:1.5;}"
                + ".rep{margin-left:30px;}"
                + "table{width:100%;border-collapse:collapse;margin:12px 0;}"
                + "th,td{border:1px solid #000;padding:8px;font-size:10pt;}th{background:#e8e8e8;font-weight:bold;text-align:center;}"
                + ".sig-table{width:100%;border:none;margin-top:28px;}"
                + ".sig-table td{border:none;text-align:center;font-weight:bold;padding:8px;}"
                + "</style></head><body><div class='page'>"
                + "<h1>BIÊN BẢN NGHIỆM THU – BM.BT.01.04</h1>"
                + "<p>Căn cứ đơn đề nghị vào ngày.......tháng.......năm 20.....</p>"
                + "<p>Đơn vị đề nghị: .................................................................................................</p>"
                + "<p>Căn cứ biên bản kiểm tra kỹ thuật và đề xuất đã ký ngày....../ ...../ 20....</p>"
                + "<p>Sau khi hoàn thành việc sửa chữa, lắp đặt thay thế phụ tùng thiết bị.</p>"
                + "<p>Hôm nay, ngày.......tháng......năm 20.. ., đại diện gồm có:</p>"
                + "<p><strong>Đại diện Bên A:</strong></p>"
                + "<p class='rep'>1. Ông (bà):....................................................Chức vụ................................</p>"
                + "<p class='rep'>2. Ông (bà):....................................................Chức vụ................................</p>"
                + "<p><strong>Đại diện Bên B</strong></p>"
                + "<p class='rep'>1. Ông (bà): ...................................................Chức vụ................................</p>"
                + "<p class='rep'>2. Ông (bà):....................................................Chức vụ................................</p>"
                + "<p>Chúng tôi đã cho thiết bị vận hành, tiến hành kiểm tra đánh giá tình hình hoạt động "
                + "và cùng thống nhất với kết quả nghiệm thu như sau:</p>"
                + "<table><thead><tr><th>TT</th><th>Tên TB sửa chữa – thay thế, xuất xứ</th>"
                + "<th>Số lượng</th><th>Tình hình hoạt động</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "<p>Các thiết bị trên đạt yêu cầu, đúng chủng loại và bảo hành theo đúng Tem bảo hành "
                + "của nhà phân phối. Sau khi sửa chữa thay thế hiện nay thiết bị đang hoạt động bình thường.</p>"
                + "<p>Đại diện hai bên thống nhất ký vào biên bản, Biên bản lập thành 02 bản cùng ngày.</p>"
                + "<table class='sig-table'><tr>"
                + "<td>ĐẠI DIỆN BÊN A</td><td>ĐẠI DIỆN BÊN B</td></tr></table>"
                + "</div></body></html>";
    }

    // 01.05 — sổ theo dõi bảo dưỡng (dữ liệu nhập từ trang Sổ theo dõi)
    private String buildSoTheoDoiHtml(List<SoTheoDoi> items) {
        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (SoTheoDoi s : items) {
            rows.append("<tr>")
                .append("<td style='text-align:center;'>").append(stt++).append("</td>")
                .append("<td>").append(safe(s.getTenThietBi())).append("</td>")
                .append("<td style='text-align:center;'>").append(safe(s.getKyHieu())).append("</td>")
                .append("<td>").append(safe(s.getThongSoKyThuat())).append("</td>")
                .append("<td style='text-align:center;'>").append(s.getNamSanXuat() == null ? "" : s.getNamSanXuat()).append("</td>")
                .append("<td style='text-align:center;'>").append(s.getNgayKiemTra() == null ? "" : s.getNgayKiemTra().format(DATE_FORMAT)).append("</td>")
                .append("<td>").append(safe(s.getTinhTrang())).append("</td>")
                .append("<td>").append(safe(s.getGhiChu())).append("</td>")
                .append("</tr>");
        }
        if (items.isEmpty()) {
            rows.append("<tr><td colspan='8' style='text-align:center;'>Chưa có dữ liệu</td></tr>");
        }
        return "<html><head><meta charset='utf-8'/><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><style>"
                + "* { margin: 0; padding: 0; }"
                + "body{font-family:'Noto Sans',sans-serif;font-size:9pt;margin:0;padding:0;}"
                + ".page{padding:20px;}"
                + ".form-code{float:right;font-weight:bold;font-size:10pt;}"
                + "h1{text-align:center;font-size:13pt;font-weight:bold;margin:0 0 12px;}"
                + "table{width:100%;border-collapse:collapse;margin-top:12px;}"
                + "th,td{border:1px solid #000;padding:6px;font-size:8.5pt;}th{background:#e8e8e8;font-weight:bold;text-align:center;}"
                + ".sig{margin-top:28px;text-align:right;font-weight:bold;padding-right:40px;}"
                + "</style></head><body><div class='page'>"
                + "<h1>SỔ THEO DÕI BẢO DƯỠNG MÁY MÓC, THIẾT BỊ - BM.BT.01.05</h1>"
                + "<table><thead><tr><th>Stt</th><th>Tên Thiết bị</th><th>Ký hiệu</th><th>Thông số kỹ thuật</th>"
                + "<th>Năm sản xuất</th><th>Ngày kiểm tra</th><th>Tình trạng</th><th>Ghi chú</th></tr></thead>"
                + "<tbody>" + rows + "</tbody></table>"
                + "<p class='sig'>Xác nhận người kiểm tra</p>"
                + "</div></body></html>";
    }

    private ThietBiLyLich deviceByMa(String ma) {
        if (ma == null || ma.isBlank()) return null;
        return thietBiRepo.findByMaSoQuanLyIgnoreCase(ma).orElse(null);
    }

    private String loaiCode(String loai) {
        if (loai == null) return "BD";
        String l = loai.toLowerCase();
        if (l.contains("hiệu chuẩn")) return "HC";
        if (l.contains("kiểm định")) return "KĐ";
        if (l.contains("đột xuất") || l.contains("sửa")) return "ĐX";
        return "BD";
    }

    private String safe(Object value) { return value == null ? "" : value.toString(); }

    private String row(String label, Object value) {
        return "<tr><th>" + label + "</th><td>" + safe(value) + "</td></tr>";
    }
}
