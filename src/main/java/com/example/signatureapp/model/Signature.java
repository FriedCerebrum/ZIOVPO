package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "signatures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название детектируемого объекта
    @Column(name = "object_name", nullable = false)
    private String objectName;
    
    // Первые 8 байт сигнатуры (хранится как строка)
    @Column(name = "first_8_bytes", nullable = false)
    private String first8Bytes;
    
    // Хэш от остатка сигнатуры
    @Column(name = "remainder_hash", nullable = false)
    private String remainderHash;
    
    // Длина остатка сигнатуры
    @Column(name = "remainder_length", nullable = false)
    private Integer remainderLength;
    
    // Смещение начала сигнатуры в файле
    @Column(name = "start_offset", nullable = false)
    private Long startOffset;
    
    // Смещение конца сигнатуры в файле
    @Column(name = "end_offset", nullable = false)
    private Long endOffset;
    
    // ManyToOne relationship with FileType
    @ManyToOne
    @JoinColumn(name = "file_type_id", nullable = false)
    private FileType fileType;
    
    // OneToOne relationship with ScanReport
    @OneToOne(mappedBy = "detectedSignature")
    private ScanReport scanReport;
}
