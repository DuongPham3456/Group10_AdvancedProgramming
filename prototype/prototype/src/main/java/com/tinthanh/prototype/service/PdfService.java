package com.tinthanh.prototype.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {

    public byte[] renderPdf(String html, String baseUri) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, baseUri.isEmpty() ? null : baseUri);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        }
    }
}
