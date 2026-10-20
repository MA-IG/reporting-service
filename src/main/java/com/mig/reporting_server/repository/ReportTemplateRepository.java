package com.mig.reporting_server.repository;

import com.mig.reporting_server.entity.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    Optional<ReportTemplate> findByName(String name);
}
