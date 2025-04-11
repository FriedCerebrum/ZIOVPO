package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scan_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "scan_date")
    private LocalDateTime scanDate;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "CLEAN", "INFECTED", "SUSPICIOUS"

    @Column(name = "result_details", length = 2000)
    private String resultDetails;
    
    // ManyToOne relationship with ScanEngine
    @ManyToOne
    @JoinColumn(name = "scan_engine_id", nullable = false)
    private ScanEngine scanEngine;
    
    // OneToOne relationship with Signature (if a signature is detected)
    @OneToOne
    @JoinColumn(name = "detected_signature_id")
    private Signature detectedSignature;
}
