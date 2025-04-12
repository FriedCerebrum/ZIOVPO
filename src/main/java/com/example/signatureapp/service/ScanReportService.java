package com.example.signatureapp.service;

import com.example.signatureapp.dto.ScanReportDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScanReportService {
    ScanReportDto createScanReport(ScanReportDto scanReportDto);
    ScanReportDto getScanReportById(UUID id);
    List<ScanReportDto> getAllScanReports();
    List<ScanReportDto> getScanReportsByEngine(Long engineId);
    List<ScanReportDto> getScanReportsByStatus(String status);
    List<ScanReportDto> getScanReportsByFileName(String fileName);
    List<ScanReportDto> getScanReportsByDateRange(LocalDateTime start, LocalDateTime end);
    ScanReportDto updateScanReport(UUID id, ScanReportDto scanReportDto);
    void deleteScanReport(UUID id);
}
