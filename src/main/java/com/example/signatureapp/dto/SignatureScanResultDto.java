package com.example.signatureapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureScanResultDto {
    private UUID signatureId;
    private String threatName;
    private long offsetFromStart;
    private long offsetFromEnd;
    private boolean matched;
}
