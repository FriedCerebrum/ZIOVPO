package com.example.signatureapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureDto {
    private UUID id;
    
    @NotBlank(message = "Threat name cannot be blank")
    private String threatName;
    
    @NotBlank(message = "First 8 bytes cannot be blank")
    private String first8Bytes;
    
    @NotBlank(message = "Remainder hash cannot be blank")
    private String remainderHash;
    
    @NotNull(message = "Remainder length must be specified")
    private Integer remainderLength;
    
    @NotNull(message = "Start offset must be specified")
    private Integer startOffset;
    
    @NotNull(message = "End offset must be specified")
    private Integer endOffset;
    
    @NotNull(message = "File type must be specified")
    private Long fileTypeId;
    
    private String digitalSignature;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String status;
}
