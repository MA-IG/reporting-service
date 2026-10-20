package com.mig.reporting_server.controller;

import com.mig.reporting_server.dto.GenerateReportRequest;
import com.mig.reporting_server.service.ReportingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "PDF report generation")
public class ApiController {


    private final ReportingService service;

    public ApiController(ReportingService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/generate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generate(
            @RequestBody GenerateReportRequest request)
            throws IOException {

        byte[] pdf = service.generatePdf(request);

        String filename = request.reportName()
                .replaceAll("[^a-zA-Z0-9._-]", "_") + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(pdf);
    }


}
