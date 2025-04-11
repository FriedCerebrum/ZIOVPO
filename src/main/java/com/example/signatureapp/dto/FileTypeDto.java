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
public class FileTypeDto {
    private Long id;
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Extension cannot be blank")
    private String extension;
    
    private String mimeType;
    
    private boolean isBinary;
}
