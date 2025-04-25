package com.example.signatureapp.controller;

import com.example.signatureapp.dto.SignatureScanResultDto;
import com.example.signatureapp.service.FileScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileScanController {

    private final FileScanService fileScanService;

    @Autowired
    public FileScanController(FileScanService fileScanService) {
        this.fileScanService = fileScanService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<SignatureScanResultDto>> uploadFileForScan(
            @RequestParam("file") MultipartFile file) throws IOException {
        // Delegate to service to scan file
        List<SignatureScanResultDto> scanResults = fileScanService.scanFile(file);
        return ResponseEntity.ok(scanResults);
    }
}
