package com.example.signatureapp.service;

import com.example.signatureapp.dto.ScanReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScanReportService {
    ScanReportDto createScanReport(ScanReportDto scanReportDto);
    ScanReportDto getScanReportById(Long id);
    List<ScanReportDto> getAllScanReports();
    List<ScanReportDto> getScanReportsByEngine(Long engineId);
    List<ScanReportDto> getScanReportsByStatus(String status);
    List<ScanReportDto> getScanReportsByFileName(String fileName);
    List<ScanReportDto> getScanReportsByDateRange(LocalDateTime start, LocalDateTime end);
    ScanReportDto updateScanReport(Long id, ScanReportDto scanReportDto);
    void deleteScanReport(Long id);
}
