package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scan_engines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanEngine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "version")
    private String version;

    @Column(name = "is_active")
    private boolean isActive;

    // ManyToMany with FileType
    @ManyToMany(mappedBy = "compatibleScanEngines")
    @Builder.Default  // Добавляем эту аннотацию
    private Set<FileType> supportedFileTypes = new HashSet<>();

    // OneToMany with ScanReport
    @OneToMany(mappedBy = "scanEngine", cascade = CascadeType.ALL)
    @Builder.Default  // Добавляем эту аннотацию
    private Set<ScanReport> scanReports = new HashSet<>();
}
