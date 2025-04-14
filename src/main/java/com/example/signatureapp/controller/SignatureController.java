package com.example.signatureapp.controller;

import com.example.signatureapp.dto.SignatureDto;
import com.example.signatureapp.model.SignatureAudit;
import com.example.signatureapp.model.SignatureHistory;
import com.example.signatureapp.service.SignatureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/signatures")
public class SignatureController {

    private final SignatureService signatureService;
    
    // u041au043eu043du0441u0442u0430u043du0442u044b u0434u043bu044f u0441u0442u0430u0442u0443u0441u043eu0432 u0441u0438u0433u043du0430u0442u0443u0440
    private static final String STATUS_ACTUAL = "ACTUAL";
    private static final String STATUS_DELETED = "DELETED";
    private static final String STATUS_CORRUPTED = "CORRUPTED";

    @Autowired
    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0432u0441u0435u0445 u0430u043au0442u0443u0430u043lu044cu043du044bu0445 u0441u0438u0433u043du0430u0442u0443u0440
     */

    @GetMapping
    public ResponseEntity<List<SignatureDto>> getAllSignatures() {
        List<SignatureDto> signatures = signatureService.getAllActualSignatures();
        return ResponseEntity.ok(signatures);
    }

    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440u044b u043fu043e u0438u0434u0435u043du0442u0438u0444u0438u043au0430u0442u043eu0440u0443
     */
    @GetMapping("/{id}")
    public ResponseEntity<SignatureDto> getSignatureById(@PathVariable UUID id) {
        SignatureDto signatureDto = signatureService.getSignatureById(id);
        return ResponseEntity.ok(signatureDto);
    }

    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440 u043fu043e u043du0430u0437u0432u0430u043du0438u044e u0443u0433u0440u043eu0437u044b
     */
    @GetMapping("/by-threat-name/{threatName}")
    public ResponseEntity<List<SignatureDto>> getSignaturesByThreatName(@PathVariable String threatName) {
        List<SignatureDto> signatures = signatureService.getSignaturesByThreatName(threatName);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * u0421u043eu0437u0434u0430u043du0438u0435 u043du043eu0432u043eu0439 u0441u0438u0433u043du0430u0442u0443u0440u044b (u0442u043eu043bu044cu043au043e u0434u043bu044f u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440u043eu0432)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignatureDto> createSignature(
            @Valid @RequestBody SignatureDto signatureDto,
            @RequestHeader("User-Id") String userId) {
        SignatureDto createdSignature = signatureService.createSignature(signatureDto, userId);
        return new ResponseEntity<>(createdSignature, HttpStatus.CREATED);
    }

    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440 u043fu043e u0442u0438u043fu0443 u0444u0430u0439u043bu0430
     */
    @GetMapping("/by-file-type/{fileTypeId}")
    public ResponseEntity<List<SignatureDto>> getSignaturesByFileType(@PathVariable Long fileTypeId) {
        List<SignatureDto> signatures = signatureService.getSignaturesByFileType(fileTypeId);
        return ResponseEntity.ok(signatures);
    }

    /**
     * u041fu043eu0438u0441u043a u0441u0438u0433u043du0430u0442u0443u0440u044b u043fu043e u043fu0435u0440u0432u044bu043c 8 u0431u0430u0439u0442u0430u043c
     */
    @GetMapping("/by-first-8-bytes/{first8Bytes}")
    public ResponseEntity<SignatureDto> getSignatureByFirst8Bytes(@PathVariable String first8Bytes) {
        SignatureDto signatureDto = signatureService.findByFirst8Bytes(first8Bytes);
        return ResponseEntity.ok(signatureDto);
    }

    /**
     * u041fu043eu0438u0441u043a u0441u0438u0433u043du0430u0442u0443u0440 u043fu043e u0434u0438u0430u043fu0430u0437u043eu043du0443 u0441u043cu0435u0449u0435u043du0438u0439
     */
    @GetMapping("/by-offset-range")
    public ResponseEntity<List<SignatureDto>> getSignaturesByOffsetRange(
            @RequestParam Integer offsetStart,
            @RequestParam Integer offsetEnd) {
        List<SignatureDto> signatures = signatureService.findByOffsetRange(offsetStart, offsetEnd);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * u0423u0431u043du043eu0432u043bu0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440u044b (u0442u043eu043bu044cu043au043e u0434u043bu044f u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440u043eu0432)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignatureDto> updateSignature(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureDto signatureDto,
            @RequestHeader("User-Id") String userId) {
        SignatureDto updatedSignature = signatureService.updateSignature(id, signatureDto, userId);
        return ResponseEntity.ok(updatedSignature);
    }

    /**
     * u0423u0434u0430u043bu0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440u044b (u0438u0437u043cu0435u043du0435u043du0438u0435 u0441u0442u0430u0442u0443u0441u0430) - u0442u043eu043bu044cu043au043e u0434u043bu044f u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440u043eu0432
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSignature(
            @PathVariable UUID id,
            @RequestHeader("User-Id") String userId) {
        signatureService.deleteSignature(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440, u043eu0431u043du043eu0432u043bu0435u043du043du044bu0445 u043fu043eu0441u043bu0435 u0443u043au0430u0437u0430u043du043du043eu0439 u0434u0430u0442u044b ("u0434u0438u0444u0444")
     */
    @GetMapping("/diff")
    public ResponseEntity<List<SignatureDto>> getSignaturesDiff(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        List<SignatureDto> signatures = signatureService.getSignaturesUpdatedSince(since);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440 u043fu043e u0441u043fu0438u0441u043au0443 GUID
     */
    @PostMapping("/by-ids")
    public ResponseEntity<List<SignatureDto>> getSignaturesByIds(@RequestBody List<UUID> ids) {
        List<SignatureDto> signatures = signatureService.getSignaturesByIds(ids);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0438u0441u0442u043eu0440u0438u0438 u0438u0437u043cu0435u043du0435u043du0438u0439 u0441u0438u0433u043du0430u0442u0443u0440u044b
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<SignatureHistory>> getSignatureHistory(@PathVariable UUID id) {
        List<SignatureHistory> history = signatureService.getSignatureHistory(id);
        return ResponseEntity.ok(history);
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0430u0443u0434u0438u0442u0430 u0438u0437u043cu0435u043du0435u043du0438u0439 u0441u0438u0433u043du0430u0442u0443u0440u044b
     */
    @GetMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SignatureAudit>> getSignatureAudit(@PathVariable UUID id) {
        List<SignatureAudit> audit = signatureService.getSignatureAudit(id);
        return ResponseEntity.ok(audit);
    }
    
    /**
     * u041fu0440u043eu0432u0435u0440u043au0430 u0446u0438u0444u0440u043eu0432u043eu0439 u043fu043eu0434u043fu0438u0441u0438 u0441u0438u0433u043du0430u0442u0443u0440u044b
     */
    @GetMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> verifySignature(@PathVariable UUID id) {
        boolean isValid = signatureService.verifySignatureDigitalSignature(id);
        return ResponseEntity.ok(isValid);
    }
    
    /**
     * u041fu043eu043bu0443u0447u0435u043du0438u0435 u0441u0438u0433u043du0430u0442u0443u0440 u043fu043e u0441u0442u0430u0442u0443u0441u0443 (u0442u043eu043bu044cu043au043e u0434u043bu044f u0430u0434u043cu0438u043du0438u0441u0442u0440u0430u0442u043eu0440u043eu0432)
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SignatureDto>> getSignaturesByStatus(@PathVariable String status) {
        List<SignatureDto> signatures = signatureService.getSignaturesByStatus(status);
        return ResponseEntity.ok(signatures);
    }
}
