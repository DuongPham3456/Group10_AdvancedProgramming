package com.tinthanh.prototype.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinthanh.prototype.model.ChiPhi;
import com.tinthanh.prototype.model.CongViec;
import com.tinthanh.prototype.model.KeHoachBaoTri;
import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.model.ThongSoMay;
import com.tinthanh.prototype.model.YeuCauBaoTri;
import com.tinthanh.prototype.repository.ChiPhiRepository;
import com.tinthanh.prototype.repository.CongViecRepository;
import com.tinthanh.prototype.repository.KeHoachRepository;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.ThongSoRepository;
import com.tinthanh.prototype.repository.YeuCauRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            ThietBiRepository thietBiRepo,
            YeuCauRepository yeuCauRepo,
            KeHoachRepository keHoachRepo,
            CongViecRepository congViecRepo,
            ChiPhiRepository chiPhiRepo,
            ThongSoRepository thongSoRepo) {
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
            yc.setNguoiYeuCau("Nguyễn Văn A");
            yeuCauRepo.save(yc);

            KeHoachBaoTri kh = new KeHoachBaoTri();
            kh.setTenKeHoach("Bảo dưỡng định kỳ CNC Q2/2026");
            kh.setMaThietBi("TB-001");
            kh.setTenThietBi("Máy CNC Haas VF-2");
            kh.setLoaiBaoTri("Định kỳ");
            kh.setNgayBatDau(LocalDate.of(2026, 6, 1));
            kh.setNgayKetThuc(LocalDate.of(2026, 6, 15));
            kh.setChuKyNgay(90);
            kh.setTrangThai("Đã duyệt");
            kh.setNoiDung("Thay dầu bôi trơn, kiểm tra băng tải phoi, hiệu chuẩn trục");
            keHoachRepo.save(kh);

            CongViec cv = new CongViec();
            cv.setTenCongViec("Kiểm tra trục chính máy tiện Okuma");
            cv.setMaThietBi("TB-003");
            cv.setNguoiPhuTrach("Trần Văn B");
            cv.setHanHoanThanh(LocalDate.of(2026, 6, 12));
            cv.setDoUuTien("Cao");
            cv.setTrangThai("Đang xử lý");
            cv.setMoTa("Tháo kiểm tra vòng bi trục chính");
            congViecRepo.save(cv);

            ChiPhi cp = new ChiPhi();
            cp.setMaThietBi("TB-002");
            cp.setTenThietBi("Máy phay Bridgeport");
            cp.setLoaiChiPhi("Bảo dưỡng");
            cp.setSoTien(3500000.0);
            cp.setMoTa("Thay dầu cắt và bộ lọc");
            chiPhiRepo.save(cp);

            ThongSoMay ts1 = new ThongSoMay();
            ts1.setMaThietBi("TB-001");
            ts1.setTenThietBi("Máy CNC Haas VF-2");
            ts1.setTenThongSo("Nhiệt độ trục chính");
            ts1.setGiaTri(42.5);
            ts1.setDonVi("°C");
            ts1.setGioMay(12500);
            thongSoRepo.save(ts1);

            ThongSoMay ts2 = new ThongSoMay();
            ts2.setMaThietBi("TB-001");
            ts2.setTenThietBi("Máy CNC Haas VF-2");
            ts2.setTenThongSo("Tốc độ trục chính");
            ts2.setGiaTri(8000.0);
            ts2.setDonVi("RPM");
            ts2.setGioMay(12500);
            thongSoRepo.save(ts2);
        };
    }
}
