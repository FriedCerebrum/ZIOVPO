package com.example.signatureapp.service;

import com.example.signatureapp.dto.SignatureDto;

import java.util.List;

public interface SignatureService {
    SignatureDto createSignature(SignatureDto signatureDto);
    SignatureDto getSignatureById(Long id);
    List<SignatureDto> getAllSignatures();
    List<SignatureDto> getSignaturesByObjectName(String objectName);
    List<SignatureDto> getSignaturesByFileType(Long fileTypeId);
    SignatureDto findByFirst8Bytes(String first8Bytes);
    List<SignatureDto> findByOffsetRange(Long startOffset, Long endOffset);
    SignatureDto updateSignature(Long id, SignatureDto signatureDto);
    void deleteSignature(Long id);
}
