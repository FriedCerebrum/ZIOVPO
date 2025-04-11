package com.example.signatureapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanEngineDto {
    private Long id;
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    private String description;
    
    private String version;
    
    private boolean isActive;
}
