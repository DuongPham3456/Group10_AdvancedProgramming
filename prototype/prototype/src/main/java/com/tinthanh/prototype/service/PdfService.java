package com.tinthanh.prototype.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {

    public byte[] renderPdf(String html) throws IOException {
        return renderPdf(html, null);
    }

    public byte[] renderPdf(String html, String baseUri) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // If a font that supports Vietnamese is available under /static/fonts, register it.
            try {
                java.io.InputStream fontStream = PdfService.class.getResourceAsStream("/static/fonts/NotoSans-Regular.ttf");
                if (fontStream != null) {
                    builder.useFont(() -> PdfService.class.getResourceAsStream("/static/fonts/NotoSans-Regular.ttf"), "Noto Sans");
                }
            } catch (Throwable ignore) {
                // proceed without custom font; system fonts may be used as fallback
            }

            String usedBase = baseUri == null ? "" : baseUri;
            builder.withHtmlContent(html, usedBase);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IOException("Lỗi tạo PDF", ex);
        }
    }
}
