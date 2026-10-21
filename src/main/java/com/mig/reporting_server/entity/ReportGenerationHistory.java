package com.mig.reporting_server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_generation_history")
@Data
public class ReportGenerationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String parameters;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String data;

    @Column(nullable = false)
    private boolean favorite = false;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}
