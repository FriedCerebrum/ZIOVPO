package com.example.signatureapp.controller;

import com.example.signatureapp.dto.ScanEngineDto;
import com.example.signatureapp.service.ScanEngineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan-engines")
public class ScanEngineController {

    private final ScanEngineService scanEngineService;

    @Autowired
    public ScanEngineController(ScanEngineService scanEngineService) {
        this.scanEngineService = scanEngineService;
    }

    @PostMapping
    public ResponseEntity<ScanEngineDto> createScanEngine(@Valid @RequestBody ScanEngineDto scanEngineDto) {
        ScanEngineDto createdScanEngine = scanEngineService.createScanEngine(scanEngineDto);
        return new ResponseEntity<>(createdScanEngine, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanEngineDto> getScanEngineById(@PathVariable Long id) {
        ScanEngineDto scanEngineDto = scanEngineService.getScanEngineById(id);
        return ResponseEntity.ok(scanEngineDto);
    }

    @GetMapping
    public ResponseEntity<List<ScanEngineDto>> getAllScanEngines() {
        List<ScanEngineDto> scanEngines = scanEngineService.getAllScanEngines();
        return ResponseEntity.ok(scanEngines);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ScanEngineDto>> getActiveScanEngines() {
        List<ScanEngineDto> activeScanEngines = scanEngineService.getActiveScanEngines();
        return ResponseEntity.ok(activeScanEngines);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<ScanEngineDto> getScanEngineByName(@PathVariable String name) {
        ScanEngineDto scanEngineDto = scanEngineService.findByName(name);
        return ResponseEntity.ok(scanEngineDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScanEngineDto> updateScanEngine(@PathVariable Long id, @Valid @RequestBody ScanEngineDto scanEngineDto) {
        ScanEngineDto updatedScanEngine = scanEngineService.updateScanEngine(id, scanEngineDto);
        return ResponseEntity.ok(updatedScanEngine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanEngine(@PathVariable Long id) {
        scanEngineService.deleteScanEngine(id);
        return ResponseEntity.noContent().build();
    }
}
