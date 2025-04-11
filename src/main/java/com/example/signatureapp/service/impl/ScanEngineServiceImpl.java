package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.ScanEngineDto;
import com.example.signatureapp.model.ScanEngine;
import com.example.signatureapp.repository.ScanEngineRepository;
import com.example.signatureapp.service.ScanEngineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScanEngineServiceImpl implements ScanEngineService {

    private final ScanEngineRepository scanEngineRepository;

    @Autowired
    public ScanEngineServiceImpl(ScanEngineRepository scanEngineRepository) {
        this.scanEngineRepository = scanEngineRepository;
    }

    @Override
    public ScanEngineDto createScanEngine(ScanEngineDto scanEngineDto) {
        // Check if a scan engine with the same name already exists
        if (scanEngineRepository.existsByName(scanEngineDto.getName())) {
            throw new IllegalArgumentException("A scan engine with name " + scanEngineDto.getName() + " already exists");
        }

        ScanEngine scanEngine = mapToEntity(scanEngineDto);
        ScanEngine savedScanEngine = scanEngineRepository.save(scanEngine);
        return mapToDto(savedScanEngine);
    }

    @Override
    public ScanEngineDto getScanEngineById(Long id) {
        ScanEngine scanEngine = scanEngineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with id: " + id));
        return mapToDto(scanEngine);
    }

    @Override
    public List<ScanEngineDto> getAllScanEngines() {
        return scanEngineRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScanEngineDto> getActiveScanEngines() {
        return scanEngineRepository.findByIsActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScanEngineDto updateScanEngine(Long id, ScanEngineDto scanEngineDto) {
        ScanEngine existingScanEngine = scanEngineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with id: " + id));

        // Check if a different scan engine with the same name exists
        if (!existingScanEngine.getName().equals(scanEngineDto.getName()) && 
                scanEngineRepository.existsByName(scanEngineDto.getName())) {
            throw new IllegalArgumentException("A scan engine with name " + scanEngineDto.getName() + " already exists");
        }

        // Update fields
        existingScanEngine.setName(scanEngineDto.getName());
        existingScanEngine.setDescription(scanEngineDto.getDescription());
        existingScanEngine.setVersion(scanEngineDto.getVersion());
        existingScanEngine.setActive(scanEngineDto.isActive());

        ScanEngine updatedScanEngine = scanEngineRepository.save(existingScanEngine);
        return mapToDto(updatedScanEngine);
    }

    @Override
    public void deleteScanEngine(Long id) {
        if (!scanEngineRepository.existsById(id)) {
            throw new EntityNotFoundException("Scan engine not found with id: " + id);
        }
        scanEngineRepository.deleteById(id);
    }

    @Override
    public ScanEngineDto findByName(String name) {
        ScanEngine scanEngine = scanEngineRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Scan engine not found with name: " + name));
        return mapToDto(scanEngine);
    }

    // Helper method to map DTO to entity
    private ScanEngine mapToEntity(ScanEngineDto scanEngineDto) {
        return ScanEngine.builder()
                .id(scanEngineDto.getId())
                .name(scanEngineDto.getName())
                .description(scanEngineDto.getDescription())
                .version(scanEngineDto.getVersion())
                .isActive(scanEngineDto.isActive())
                .build();
    }

    // Helper method to map entity to DTO
    private ScanEngineDto mapToDto(ScanEngine scanEngine) {
        return ScanEngineDto.builder()
                .id(scanEngine.getId())
                .name(scanEngine.getName())
                .description(scanEngine.getDescription())
                .version(scanEngine.getVersion())
                .isActive(scanEngine.isActive())
                .build();
    }
}
