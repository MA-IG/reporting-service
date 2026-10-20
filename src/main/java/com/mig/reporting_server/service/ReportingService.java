package com.mig.reporting_server.service;


import com.mig.reporting_server.dto.GenerateReportRequest;
import com.mig.reporting_server.entity.ReportTemplate;
import com.mig.reporting_server.repository.ReportTemplateRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Transactional
public class ReportingService {


    private final ReportTemplateRepository repository;
    private final Path uploadDir;

    public ReportingService(
            ReportTemplateRepository repository,
            @Value("${app.upload.dir}") String uploadDirectory) {
        this.repository = repository;
        this.uploadDir = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ReportTemplate findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report template not found: " + id));
    }

    @Transactional
    public ReportTemplate add(String name, MultipartFile file)
            throws IOException {

        validateName(name);
        validateFile(file);

        Files.createDirectories(uploadDir);

        String storedName = generateFileName(file.getOriginalFilename());
        Path destination = resolveFile(storedName);

        file.transferTo(destination);

        try {
            ReportTemplate template = new ReportTemplate();
            template.setName(name.trim());
            template.setFileName(storedName);

            return repository.saveAndFlush(template);
        } catch (RuntimeException e) {
            deleteFileQuietly(destination);
            throw e;
        }
    }

    @Transactional
    public ReportTemplate update(
            Long id,
            String name,
            MultipartFile newFile) throws IOException {

        validateName(name);

        ReportTemplate template = findById(id);

        String previousFileName = template.getFileName();
        Path newDestination = null;

        if (newFile != null && !newFile.isEmpty()) {
            validateFile(newFile);

            Files.createDirectories(uploadDir);

            String newFileName =
                    generateFileName(newFile.getOriginalFilename());

            newDestination = resolveFile(newFileName);
            newFile.transferTo(newDestination);

            template.setFileName(newFileName);
        }

        template.setName(name.trim());

        try {
            ReportTemplate updated = repository.saveAndFlush(template);

            if (newDestination != null) {
                deleteFileQuietly(resolveFile(previousFileName));
            }

            return updated;
        } catch (RuntimeException e) {
            deleteFileQuietly(newDestination);
            throw e;
        }
    }

    @Transactional
    public void delete(Long id) {
        ReportTemplate template = findById(id);

        repository.delete(template);
        repository.flush();

        deleteFileQuietly(resolveFile(template.getFileName()));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Template name is required.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Template file is required.");
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase().endsWith(".jasper")) {
            throw new IllegalArgumentException(
                    "Only .jasper files are allowed.");
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.'))
                .toLowerCase();

        return UUID.randomUUID() + extension;
    }

    private Path resolveFile(String filename) {
        Path path = uploadDir.resolve(filename).normalize();

        if (!path.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        return path;
    }

    private void deleteFileQuietly(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Replace with application logging in production.
        }
    }


    public byte[] generatePdf(GenerateReportRequest request)
            throws IOException {

        ReportTemplate template = repository.findByName(
                request.reportName()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Report not found: " + request.reportName()
                )
        );

        Path reportPath = resolveFile(template.getFileName());

        if (!Files.exists(reportPath)) {
            throw new IllegalArgumentException(
                    "Report file not found: " + template.getFileName()
            );
        }

        try (InputStream inputStream = Files.newInputStream(reportPath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            JasperReport jasperReport =
                    (JasperReport) JRLoader.loadObject(inputStream);

            Map<String, Object> parameters =
                    new HashMap<>(request.parameters());

            Collection<Map<String, ?>> data = new ArrayList<>(request.data());

            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(data);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    dataSource
            );

            JasperExportManager.exportReportToPdfStream(
                    jasperPrint,
                    outputStream
            );

            return outputStream.toByteArray();
        } catch (JRException e) {
            throw new IllegalStateException(
                    "Failed to generate PDF for report: "
                            + request.reportName(),
                    e
            );
        }
    }
}

