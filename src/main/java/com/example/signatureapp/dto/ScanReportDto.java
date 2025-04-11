package com.example.signatureapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanReportDto {
    private Long id;
    
    @NotBlank(message = "File name cannot be blank")
    private String fileName;
    
    private Long fileSize;
    
    private LocalDateTime scanDate;
    
    @NotBlank(message = "Status cannot be blank")
    private String status;
    
    private String resultDetails;
    
    @NotNull(message = "Scan engine must be specified")
    private Long scanEngineId;
    
    private Long detectedSignatureId;
}
