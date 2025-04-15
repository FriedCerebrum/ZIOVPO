package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "file_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String extension;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "is_binary")
    private boolean isBinary;

    // OneToMany relationship with Signature
    @OneToMany(mappedBy = "fileType", cascade = CascadeType.ALL)
    @Builder.Default  // Добавляем эту аннотацию
    private Set<Signature> signatures = new HashSet<>();

    // ManyToMany relationship with ScanEngine
    @ManyToMany
    @JoinTable(
        name = "filetype_scanengine",
        joinColumns = @JoinColumn(name = "filetype_id"),
        inverseJoinColumns = @JoinColumn(name = "scanengine_id")
    )
    @Builder.Default  // Добавляем эту аннотацию
    private Set<ScanEngine> compatibleScanEngines = new HashSet<>();
}
