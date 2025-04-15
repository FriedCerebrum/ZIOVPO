package com.example.signatureapp.repository;

import com.example.signatureapp.model.ScanEngine;
import com.example.signatureapp.model.ScanReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScanReportRepository extends JpaRepository<ScanReport, Long> {  // Заменяем UUID на Long
    List<ScanReport> findByScanEngine(ScanEngine scanEngine);
    List<ScanReport> findByStatus(String status);
    List<ScanReport> findByFileName(String fileName);
    List<ScanReport> findByScanDateBetween(LocalDateTime start, LocalDateTime end);
}
