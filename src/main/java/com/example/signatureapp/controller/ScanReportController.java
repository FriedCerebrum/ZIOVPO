package com.example.signatureapp.controller;

import com.example.signatureapp.dto.ScanReportDto;
import com.example.signatureapp.service.ScanReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/scan-reports")
public class ScanReportController {

    private final ScanReportService scanReportService;

    @Autowired
    public ScanReportController(ScanReportService scanReportService) {
        this.scanReportService = scanReportService;
    }

    @PostMapping
    public ResponseEntity<ScanReportDto> createScanReport(@Valid @RequestBody ScanReportDto scanReportDto) {
        ScanReportDto createdScanReport = scanReportService.createScanReport(scanReportDto);
        return new ResponseEntity<>(createdScanReport, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanReportDto> getScanReportById(@PathVariable UUID id) {
        ScanReportDto scanReportDto = scanReportService.getScanReportById(id);
        return ResponseEntity.ok(scanReportDto);
    }

    @GetMapping
    public ResponseEntity<List<ScanReportDto>> getAllScanReports() {
        List<ScanReportDto> scanReports = scanReportService.getAllScanReports();
        return ResponseEntity.ok(scanReports);
    }

    @GetMapping("/by-engine/{engineId}")
    public ResponseEntity<List<ScanReportDto>> getScanReportsByEngine(@PathVariable Long engineId) {
        List<ScanReportDto> scanReports = scanReportService.getScanReportsByEngine(engineId);
        return ResponseEntity.ok(scanReports);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ScanReportDto>> getScanReportsByStatus(@PathVariable String status) {
        List<ScanReportDto> scanReports = scanReportService.getScanReportsByStatus(status);
        return ResponseEntity.ok(scanReports);
    }

    @GetMapping("/by-filename/{fileName}")
    public ResponseEntity<List<ScanReportDto>> getScanReportsByFileName(@PathVariable String fileName) {
        List<ScanReportDto> scanReports = scanReportService.getScanReportsByFileName(fileName);
        return ResponseEntity.ok(scanReports);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<ScanReportDto>> getScanReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ScanReportDto> scanReports = scanReportService.getScanReportsByDateRange(start, end);
        return ResponseEntity.ok(scanReports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScanReportDto> updateScanReport(
            @PathVariable UUID id,
            @Valid @RequestBody ScanReportDto scanReportDto) {
        ScanReportDto updatedScanReport = scanReportService.updateScanReport(id, scanReportDto);
        return ResponseEntity.ok(updatedScanReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanReport(@PathVariable UUID id) {
        scanReportService.deleteScanReport(id);
        return ResponseEntity.noContent().build();
    }
}
