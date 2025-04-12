package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signature_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    // u0421u0441u044bu043bu043au0430 u043du0430 u0437u0430u043fu0438u0441u044c u0432 u0442u0430u0431u043bu0438u0446u0435 u0441u0438u0433u043du0430u0442u0443u0440
    @Column(name = "signature_id", nullable = false)
    private UUID signatureId;

    // u0423u043au0430u0437u0430u0442u0435u043bu044c u043du0430 u043fu043eu043bu044cu0437u043eu0432u0430u0442u0435u043bu044f, u0441u043eu0432u0435u0440u0448u0438u0432u0448u0435u0433u043e u0438u0437u043cu0435u043du0435u043du0438u0435
    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    // u0422u0438u043f u0438u0437u043cu0435u043du0435u043du0438u044f (CREATED, UPDATED, DELETED, CORRUPTED u0438 u0442.u0434.)
    @Column(name = "change_type", nullable = false)
    private String changeType;

    // u0412u0440u0435u043cu044f, u043au043eu0433u0434u0430 u043fu0440u043eu0438u0437u043eu0448u043bu043e u0438u0437u043cu0435u043du0435u043du0438u0435
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    // u0421u043fu0438u0441u043eu043a u0438u0437u043cu0435u043du0451u043du043du044bu0445 u043fu043eu043bu0435u0439, u043cu043eu0436u043du043e u0445u0440u0430u043du0438u0442u044c u0432 u0432u0438u0434u0435 JSON
    @Column(name = "fields_changed", columnDefinition = "TEXT")
    private String fieldsChanged;
}
