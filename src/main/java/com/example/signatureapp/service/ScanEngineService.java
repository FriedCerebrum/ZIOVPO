package com.example.signatureapp.service;

import com.example.signatureapp.dto.ScanEngineDto;

import java.util.List;

public interface ScanEngineService {
    ScanEngineDto createScanEngine(ScanEngineDto scanEngineDto);
    ScanEngineDto getScanEngineById(Long id);
    List<ScanEngineDto> getAllScanEngines();
    List<ScanEngineDto> getActiveScanEngines();
    ScanEngineDto updateScanEngine(Long id, ScanEngineDto scanEngineDto);
    void deleteScanEngine(Long id);
    ScanEngineDto findByName(String name);
}
