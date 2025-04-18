package com.example.signatureapp.controller;

import com.example.signatureapp.dto.SignatureScanResult;
import com.example.signatureapp.service.FileScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileScanController {

    private final FileScanService fileScanService;

    /**
     * Handles file upload for signature scanning.
     *
     * @param file The file to be scanned (multipart/form-data).
     * @return A list of detected signatures or an empty list if none found.
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()") // Require authentication to upload files
    public ResponseEntity<List<SignatureScanResult>> uploadAndScanFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Upload request received with an empty file.");
            // Returning Bad Request might be more appropriate than an empty list
            return ResponseEntity.badRequest().body(Collections.emptyList());
             // Or throw new IllegalArgumentException("Uploaded file is empty."); handled by GlobalExceptionHandler
        }

        log.info("Received file '{}' for scanning. Size: {} bytes.", file.getOriginalFilename(), file.getSize());

        try {
            List<SignatureScanResult> results = fileScanService.scanFile(file);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
             log.warn("Bad request during file upload/scan: {}", e.getMessage());
             // Let GlobalExceptionHandler handle this for a consistent 400 response
             throw e;
        }
         catch (IOException e) {
            log.error("Failed to process uploaded file '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
             // Or rethrow a custom exception to be handled by GlobalExceptionHandler
        }
    }
}
