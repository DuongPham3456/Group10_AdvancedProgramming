package com.tinthanh.prototype.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tinthanh.prototype.model.ThietBiLyLich;
import com.tinthanh.prototype.model.User;
import com.tinthanh.prototype.repository.ThietBiRepository;
import com.tinthanh.prototype.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            ThietBiRepository thietBiRepo,
            UserRepository userRepo) {
        return args -> {
            if (userRepo.count() == 0) {
                BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
                String pw = enc.encode("123456");
                userRepo.save(makeUser("admin.tram", pw, "Nguyễn Quản Lý Trạm", "QUAN_LY_TRAM"));
                userRepo.save(makeUser("bp.qltb",    pw, "Trần BP. QLTB",        "BP_QLTB"));
                userRepo.save(makeUser("giamdoc",    pw, "Lê Giám Đốc",          "GIAM_DOC"));
                userRepo.save(makeUser("congnhan",   pw, "Phạm Công Nhân",       "CONG_NHAN"));
                userRepo.save(makeUser("ql.vung",    pw, "Hoàng Quản Lý Vùng",  "QUAN_LY_VUNG"));
            }

            if (thietBiRepo.count() > 0) return;

            // TB-001: Máy bơm nước thải
            ThietBiLyLich tb1 = new ThietBiLyLich();
            tb1.setTenThietBi("Máy bơm nước thải ly tâm");
            tb1.setMaSoQuanLy("TB-001");
            tb1.setSoSerial("SN-GRUNDFOS-2020-001");
            tb1.setModelType("SE1.110.220.4.54H");
            tb1.setNhaSanXuat("Grundfos");
            tb1.setNamSanXuat(2020);
            tb1.setTrangThaiVanHanh("Đang hoạt động");
            tb1.setDacDiem("Công suất 11kW, lưu lượng tối đa 220 m³/h, cột áp 4m, vật liệu thân gang");
            tb1.setNgayTiepNhan(LocalDate.of(2020, 3, 15));
            tb1.setNgayDuaVaoSuDung(LocalDate.of(2020, 4, 1));
            tb1.setPhuTungKemTheo("Bộ khớp nối, van một chiều DN150, tủ điện điều khiển");
            tb1.setTaiLieuKemTheo("Catalogue kỹ thuật, hướng dẫn lắp đặt, bản vẽ chi tiết");
            tb1.setTinhTrangTiepNhan("Mới 100%, đầy đủ phụ kiện, tem niêm phong nguyên vẹn");
            thietBiRepo.save(tb1);

            // TB-002: Máy thổi khí
            ThietBiLyLich tb2 = new ThietBiLyLich();
            tb2.setTenThietBi("Máy thổi khí dạng Root");
            tb2.setMaSoQuanLy("TB-002");
            tb2.setSoSerial("SN-LONGTECH-2019-047");
            tb2.setModelType("LT-100");
            tb2.setNhaSanXuat("Longtech");
            tb2.setNamSanXuat(2019);
            tb2.setTrangThaiVanHanh("Đang hoạt động");
            tb2.setDacDiem("Công suất 5.5kW, lưu lượng 100 m³/h, áp suất tối đa 0.5 bar, cấp khí cho bể Aerotank");
            tb2.setNgayTiepNhan(LocalDate.of(2019, 6, 10));
            tb2.setNgayDuaVaoSuDung(LocalDate.of(2019, 7, 1));
            tb2.setPhuTungKemTheo("Bộ lọc không khí, van an toàn, đồng hồ áp suất");
            tb2.setTaiLieuKemTheo("Hướng dẫn vận hành, catalogue, chứng nhận CE");
            tb2.setTinhTrangTiepNhan("Mới 100%, kèm hộp phụ kiện đầy đủ");
            thietBiRepo.save(tb2);

            // TB-003: Máy ép bùn
            ThietBiLyLich tb3 = new ThietBiLyLich();
            tb3.setTenThietBi("Máy ép bùn băng tải");
            tb3.setMaSoQuanLy("TB-003");
            tb3.setSoSerial("SN-ANDRITZ-2021-003");
            tb3.setModelType("BF-1000");
            tb3.setNhaSanXuat("Andritz");
            tb3.setNamSanXuat(2021);
            tb3.setTrangThaiVanHanh("Bảo dưỡng");
            tb3.setDacDiem("Công suất 3kW, chiều rộng băng 1000mm, năng suất 15–20 kg DS/h, vật liệu thép không gỉ 304");
            tb3.setNgayTiepNhan(LocalDate.of(2021, 1, 20));
            tb3.setNgayDuaVaoSuDung(LocalDate.of(2021, 2, 15));
            tb3.setPhuTungKemTheo("Bộ băng tải dự phòng, hệ thống rửa băng, bơm polymer");
            tb3.setTaiLieuKemTheo("Manual vận hành, bản vẽ cơ khí, phiếu kiểm tra xuất xưởng");
            tb3.setTinhTrangTiepNhan("Mới 100%, lắp đặt và chạy thử tại nhà máy trước khi bàn giao");
            thietBiRepo.save(tb3);

            // TB-004: Tủ điện MCC
            ThietBiLyLich tb4 = new ThietBiLyLich();
            tb4.setTenThietBi("Tủ điện điều khiển trung tâm MCC");
            tb4.setMaSoQuanLy("TB-004");
            tb4.setSoSerial("SN-SIEMENS-2020-MCC01");
            tb4.setModelType("SIVACON S8");
            tb4.setNhaSanXuat("Siemens");
            tb4.setNamSanXuat(2020);
            tb4.setTrangThaiVanHanh("Đang hoạt động");
            tb4.setDacDiem("Điện áp 380V/3 pha, tích hợp PLC S7-1200, màn hình HMI 7 inch, điều khiển toàn bộ hệ thống xử lý nước thải");
            tb4.setNgayTiepNhan(LocalDate.of(2020, 3, 10));
            tb4.setNgayDuaVaoSuDung(LocalDate.of(2020, 4, 1));
            tb4.setPhuTungKemTheo("Aptomat dự phòng, cầu chì, rơ-le trung gian, dây tín hiệu");
            tb4.setTaiLieuKemTheo("Sơ đồ điện tổng, tài liệu lập trình PLC, hướng dẫn HMI");
            tb4.setTinhTrangTiepNhan("Mới 100%, đã kiểm tra FAT tại nhà máy Siemens");
            thietBiRepo.save(tb4);

            // TB-005: Máy đo lưu lượng
            ThietBiLyLich tb5 = new ThietBiLyLich();
            tb5.setTenThietBi("Đồng hồ đo lưu lượng điện từ");
            tb5.setMaSoQuanLy("TB-005");
            tb5.setSoSerial("SN-ENDRESS-2022-FM01");
            tb5.setModelType("Promag W 400");
            tb5.setNhaSanXuat("Endress+Hauser");
            tb5.setNamSanXuat(2022);
            tb5.setTrangThaiVanHanh("Đang hoạt động");
            tb5.setDacDiem("DN150, đo lưu lượng nước thải đầu vào, ngõ ra 4–20mA, cấp bảo vệ IP68, chống ăn mòn");
            tb5.setNgayTiepNhan(LocalDate.of(2022, 5, 5));
            tb5.setNgayDuaVaoSuDung(LocalDate.of(2022, 5, 20));
            tb5.setPhuTungKemTheo("Bộ cáp tín hiệu 10m, mặt bích kết nối DN150");
            tb5.setTaiLieuKemTheo("Certificate hiệu chuẩn, hướng dẫn lắp đặt, datasheet");
            tb5.setTinhTrangTiepNhan("Mới, có chứng chỉ hiệu chuẩn xuất xưởng kèm theo");
            thietBiRepo.save(tb5);

            // TB-006: Máy khuấy chìm
            ThietBiLyLich tb6 = new ThietBiLyLich();
            tb6.setTenThietBi("Máy khuấy chìm bể điều hòa");
            tb6.setMaSoQuanLy("TB-006");
            tb6.setSoSerial("SN-FLYGT-2021-MX01");
            tb6.setModelType("Flygt 4630");
            tb6.setNhaSanXuat("Xylem (Flygt)");
            tb6.setNamSanXuat(2021);
            tb6.setTrangThaiVanHanh("Sự cố");
            tb6.setDacDiem("Công suất 2.2kW, 960 vòng/phút, cánh khuấy 350mm, vật liệu thép không gỉ 316L");
            tb6.setNgayTiepNhan(LocalDate.of(2021, 8, 12));
            tb6.setNgayDuaVaoSuDung(LocalDate.of(2021, 9, 1));
            tb6.setPhuTungKemTheo("Xích treo inox, cáp điện chuyên dụng 15m, bộ cảm biến nhiệt độ cuộn dây");
            tb6.setTaiLieuKemTheo("Hướng dẫn bảo trì, catalogue, chứng nhận chống nước IP68");
            tb6.setTinhTrangTiepNhan("Mới 100%, bao gói nguyên kiện");
            thietBiRepo.save(tb6);

            // TB-007: Hệ thống châm hóa chất
            ThietBiLyLich tb7 = new ThietBiLyLich();
            tb7.setTenThietBi("Bơm định lượng hóa chất PAC");
            tb7.setMaSoQuanLy("TB-007");
            tb7.setSoSerial("SN-PROMINENT-2022-DL01");
            tb7.setModelType("Sigma/ 3 Control");
            tb7.setNhaSanXuat("ProMinent");
            tb7.setNamSanXuat(2022);
            tb7.setTrangThaiVanHanh("Đang hoạt động");
            tb7.setDacDiem("Lưu lượng tối đa 7.5 L/h, áp suất 10 bar, điều khiển 4–20mA, vật liệu đầu bơm PVDF chịu hóa chất");
            tb7.setNgayTiepNhan(LocalDate.of(2022, 11, 3));
            tb7.setNgayDuaVaoSuDung(LocalDate.of(2022, 11, 20));
            tb7.setPhuTungKemTheo("Bộ van đầu bơm, ống dẫn hóa chất PE, bình châm hóa chất 200L");
            tb7.setTaiLieuKemTheo("Hướng dẫn vận hành, certificate kiểm tra xuất xưởng, datasheet");
            tb7.setTinhTrangTiepNhan("Mới 100%, nguyên đai nguyên kiện");
            thietBiRepo.save(tb7);
        };
    }

    private User makeUser(String username, String password, String hoTen, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setHoTen(hoTen);
        u.setRole(role);
        return u;
    }
}
