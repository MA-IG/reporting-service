package com.mig.reporting_server.repository;

import com.mig.reporting_server.entity.ReportGenerationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportGenerationHistoryRepository extends JpaRepository<ReportGenerationHistory, Long> {

    List<ReportGenerationHistory> findAllByOrderByGeneratedAtDesc();

    List<ReportGenerationHistory> findByFavoriteTrueOrderByGeneratedAtDesc();
}
