package com.tinthanh.prototype.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {

    public byte[] renderPdf(String html, String baseUri) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            
            // Cấu hình font hỗ trợ Tiếng Việt: thử tải từ resource trước, nếu không có thì thử các font Windows
            builder.useFont(() -> openFontStream("/fonts/NotoSans-Regular.ttf", "C:/Windows/Fonts/times.ttf", "C:/Windows/Fonts/arial.ttf", "C:/Windows/Fonts/tahoma.ttf"), "Noto Sans");

            builder.useFont(() -> openFontStream("/fonts/NotoSans-Bold.ttf", "C:/Windows/Fonts/timesbd.ttf", "C:/Windows/Fonts/arialbd.ttf", "C:/Windows/Fonts/tahomabd.ttf"), "Noto Sans", 700, null, true);
            
            builder.useFastMode();
            builder.withHtmlContent(html, baseUri.isEmpty() ? null : baseUri);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        }
    }

    private InputStream openFontStream(String...paths) {
        for (String p : paths) {
            try {
                if (p == null) continue;
                InputStream is = PdfService.class.getResourceAsStream(p);
                if (is != null) return is;
                java.io.File f = new java.io.File(p);
                if (f.exists() && f.canRead()) return new java.io.FileInputStream(f);
            } catch (Exception e) {
                // try next
            }
        }
        return null;
    }
}
