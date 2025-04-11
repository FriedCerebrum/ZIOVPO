package com.example.signatureapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureDto {
    private Long id;
    
    @NotBlank(message = "Object name cannot be blank")
    private String objectName;
    
    @NotBlank(message = "First 8 bytes cannot be blank")
    private String first8Bytes;
    
    @NotBlank(message = "Remainder hash cannot be blank")
    private String remainderHash;
    
    @NotNull(message = "Remainder length must be specified")
    private Integer remainderLength;
    
    @NotNull(message = "Start offset must be specified")
    private Long startOffset;
    
    @NotNull(message = "End offset must be specified")
    private Long endOffset;
    
    @NotNull(message = "File type must be specified")
    private Long fileTypeId;
}
