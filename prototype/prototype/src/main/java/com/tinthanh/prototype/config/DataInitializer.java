package com.tinthanh.prototype.config;

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
