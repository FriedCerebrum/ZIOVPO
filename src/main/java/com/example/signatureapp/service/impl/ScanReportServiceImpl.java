package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.ScanReportDto;
import com.example.signatureapp.model.ScanEngine;
import com.example.signatureapp.model.ScanReport;
import com.example.signatureapp.model.Signature;
import com.example.signatureapp.repository.ScanEngineRepository;
import com.example.signatureapp.repository.ScanReportRepository;
import com.example.signatureapp.repository.SignatureRepository;
import com.example.signatureapp.service.ScanReportService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScanReportServiceImpl implements ScanReportService {

    private final ScanReportRepository scanReportRepository;
    private final ScanEngineRepository scanEngineRepository;
    private final SignatureRepository signatureRepository;

    @Autowired
    public ScanReportServiceImpl(ScanReportRepository scanReportRepository,
                              ScanEngineRepository scanEngineRepository,
                              SignatureRepository signatureRepository) {
        this.scanReportRepository = scanReportRepository;
        this.scanEngineRepository = scanEngineRepository;
        this.signatureRepository = signatureRepository;
    }

    @Override
    public ScanReportDto createScanReport(ScanReportDto scanReportDto) {
        // Set scan date if not specified
        if (scanReportDto.getScanDate() == null) {
            scanReportDto.setScanDate(LocalDateTime.now());
        }

        // Find the scan engine
        ScanEngine scanEngine = scanEngineRepository.findById(scanReportDto.getScanEngineId())
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with id: " + scanReportDto.getScanEngineId()));

        // Create and save the scan report
        ScanReport scanReport = mapToEntity(scanReportDto, scanEngine);
        ScanReport savedScanReport = scanReportRepository.save(scanReport);
        return mapToDto(savedScanReport);
    }

    @Override
    public ScanReportDto getScanReportById(Long id) {  // Заменяем UUID на Long
        ScanReport scanReport = scanReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scan report not found with id: " + id));
        return mapToDto(scanReport);
    }

    @Override
    public List<ScanReportDto> getAllScanReports() {
        return scanReportRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScanReportDto> getScanReportsByEngine(Long engineId) {
        ScanEngine scanEngine = scanEngineRepository.findById(engineId)
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with id: " + engineId));
        
        return scanReportRepository.findByScanEngine(scanEngine).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScanReportDto> getScanReportsByStatus(String status) {
        return scanReportRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScanReportDto> getScanReportsByFileName(String fileName) {
        return scanReportRepository.findByFileName(fileName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScanReportDto> getScanReportsByDateRange(LocalDateTime start, LocalDateTime end) {
        return scanReportRepository.findByScanDateBetween(start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScanReportDto updateScanReport(Long id, ScanReportDto scanReportDto) {
        ScanReport existingScanReport = scanReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scan report not found with id: " + id));

        // Find the scan engine
        ScanEngine scanEngine = scanEngineRepository.findById(scanReportDto.getScanEngineId())
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with id: " + scanReportDto.getScanEngineId()));

        // Update fields
        existingScanReport.setFileName(scanReportDto.getFileName());
        existingScanReport.setFileSize(scanReportDto.getFileSize());
        existingScanReport.setScanDate(scanReportDto.getScanDate());
        existingScanReport.setStatus(scanReportDto.getStatus());
        existingScanReport.setResultDetails(scanReportDto.getResultDetails());
        existingScanReport.setScanEngine(scanEngine);

        // Update detected signature if provided
        if (scanReportDto.getDetectedSignatureId() != null) {
            Signature signature = signatureRepository.findById(scanReportDto.getDetectedSignatureId())
                    .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + scanReportDto.getDetectedSignatureId()));
            existingScanReport.setDetectedSignature(signature);
        } else {
            existingScanReport.setDetectedSignature(null);
        }

        ScanReport updatedScanReport = scanReportRepository.save(existingScanReport);
        return mapToDto(updatedScanReport);
    }

    @Override
    public void deleteScanReport(Long id) { // Меняем UUID на Long
        if (!scanReportRepository.existsById(id)) {
            throw new EntityNotFoundException("Scan report not found with id: " + id);
        }
        scanReportRepository.deleteById(id);
    }

    // Helper method to map DTO to entity
    private ScanReport mapToEntity(ScanReportDto scanReportDto, ScanEngine scanEngine) {
        ScanReport scanReport = ScanReport.builder()
                .id(scanReportDto.getId())
                .fileName(scanReportDto.getFileName())
                .fileSize(scanReportDto.getFileSize())
                .scanDate(scanReportDto.getScanDate())
                .status(scanReportDto.getStatus())
                .resultDetails(scanReportDto.getResultDetails())
                .scanEngine(scanEngine)
                .build();

        // Set detected signature if provided
        if (scanReportDto.getDetectedSignatureId() != null) {
            signatureRepository.findById(scanReportDto.getDetectedSignatureId())
                    .ifPresent(scanReport::setDetectedSignature);
        }

        return scanReport;
    }

    // Helper method to map entity to DTO
    private ScanReportDto mapToDto(ScanReport scanReport) {
        ScanReportDto scanReportDto = ScanReportDto.builder()
                .id(scanReport.getId())
                .fileName(scanReport.getFileName())
                .fileSize(scanReport.getFileSize())
                .scanDate(scanReport.getScanDate())
                .status(scanReport.getStatus())
                .resultDetails(scanReport.getResultDetails())
                .scanEngineId(scanReport.getScanEngine().getId())
                .build();

        // Set detected signature ID if available
        if (scanReport.getDetectedSignature() != null) {
            scanReportDto.setDetectedSignatureId(scanReport.getDetectedSignature().getId());
        }

        return scanReportDto;
    }
}
