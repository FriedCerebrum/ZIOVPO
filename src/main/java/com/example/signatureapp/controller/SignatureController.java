package com.example.signatureapp.controller;

import com.example.signatureapp.dto.SignatureDto;
import com.example.signatureapp.service.SignatureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    @Autowired
    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping
    public ResponseEntity<SignatureDto> createSignature(@Valid @RequestBody SignatureDto signatureDto) {
        SignatureDto createdSignature = signatureService.createSignature(signatureDto);
        return new ResponseEntity<>(createdSignature, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignatureDto> getSignatureById(@PathVariable Long id) {
        SignatureDto signatureDto = signatureService.getSignatureById(id);
        return ResponseEntity.ok(signatureDto);
    }

    @GetMapping
    public ResponseEntity<List<SignatureDto>> getAllSignatures() {
        List<SignatureDto> signatures = signatureService.getAllSignatures();
        return ResponseEntity.ok(signatures);
    }

    @GetMapping("/by-object-name/{objectName}")
    public ResponseEntity<List<SignatureDto>> getSignaturesByObjectName(@PathVariable String objectName) {
        List<SignatureDto> signatures = signatureService.getSignaturesByObjectName(objectName);
        return ResponseEntity.ok(signatures);
    }

    @GetMapping("/by-file-type/{fileTypeId}")
    public ResponseEntity<List<SignatureDto>> getSignaturesByFileType(@PathVariable Long fileTypeId) {
        List<SignatureDto> signatures = signatureService.getSignaturesByFileType(fileTypeId);
        return ResponseEntity.ok(signatures);
    }

    @GetMapping("/by-first-8-bytes/{first8Bytes}")
    public ResponseEntity<SignatureDto> getSignatureByFirst8Bytes(@PathVariable String first8Bytes) {
        SignatureDto signatureDto = signatureService.findByFirst8Bytes(first8Bytes);
        return ResponseEntity.ok(signatureDto);
    }

    @GetMapping("/by-offset-range")
    public ResponseEntity<List<SignatureDto>> getSignaturesByOffsetRange(
            @RequestParam Long startOffset,
            @RequestParam Long endOffset) {
        List<SignatureDto> signatures = signatureService.findByOffsetRange(startOffset, endOffset);
        return ResponseEntity.ok(signatures);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SignatureDto> updateSignature(
            @PathVariable Long id,
            @Valid @RequestBody SignatureDto signatureDto) {
        SignatureDto updatedSignature = signatureService.updateSignature(id, signatureDto);
        return ResponseEntity.ok(updatedSignature);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSignature(@PathVariable Long id) {
        signatureService.deleteSignature(id);
        return ResponseEntity.noContent().build();
    }
}
