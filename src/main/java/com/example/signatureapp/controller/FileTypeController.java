package com.example.signatureapp.controller;

import com.example.signatureapp.dto.FileTypeDto;
import com.example.signatureapp.service.FileTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-types")
public class FileTypeController {

    private final FileTypeService fileTypeService;

    @Autowired
    public FileTypeController(FileTypeService fileTypeService) {
        this.fileTypeService = fileTypeService;
    }

    @PostMapping
    public ResponseEntity<FileTypeDto> createFileType(@Valid @RequestBody FileTypeDto fileTypeDto) {
        FileTypeDto createdFileType = fileTypeService.createFileType(fileTypeDto);
        return new ResponseEntity<>(createdFileType, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileTypeDto> getFileTypeById(@PathVariable Long id) {
        FileTypeDto fileTypeDto = fileTypeService.getFileTypeById(id);
        return ResponseEntity.ok(fileTypeDto);
    }

    @GetMapping
    public ResponseEntity<List<FileTypeDto>> getAllFileTypes() {
        List<FileTypeDto> fileTypes = fileTypeService.getAllFileTypes();
        return ResponseEntity.ok(fileTypes);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<FileTypeDto> getFileTypeByName(@PathVariable String name) {
        FileTypeDto fileTypeDto = fileTypeService.findByName(name);
        return ResponseEntity.ok(fileTypeDto);
    }

    @GetMapping("/by-extension/{extension}")
    public ResponseEntity<FileTypeDto> getFileTypeByExtension(@PathVariable String extension) {
        FileTypeDto fileTypeDto = fileTypeService.findByExtension(extension);
        return ResponseEntity.ok(fileTypeDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileTypeDto> updateFileType(@PathVariable Long id, @Valid @RequestBody FileTypeDto fileTypeDto) {
        FileTypeDto updatedFileType = fileTypeService.updateFileType(id, fileTypeDto);
        return ResponseEntity.ok(updatedFileType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileType(@PathVariable Long id) {
        fileTypeService.deleteFileType(id);
        return ResponseEntity.noContent().build();
    }
}
