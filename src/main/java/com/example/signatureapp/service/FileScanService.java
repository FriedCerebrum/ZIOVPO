package com.example.signatureapp.service;

import com.example.signatureapp.dto.SignatureScanResultDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface FileScanService {
    List<SignatureScanResultDto> scanFile(MultipartFile file) throws IOException;
}
