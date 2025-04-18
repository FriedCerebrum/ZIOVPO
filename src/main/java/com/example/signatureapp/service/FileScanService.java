package com.example.signatureapp.service;

import com.example.signatureapp.dto.SignatureScanResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// Изменяем class на interface
public interface FileScanService {

    /**
     * Scans the uploaded file for signatures.
     *
     * @param file The file to scan.
     * @return A list of detected signature results.
     * @throws IOException If an I/O error occurs reading the file.
     */
    List<SignatureScanResult> scanFile(MultipartFile file) throws IOException;
}
