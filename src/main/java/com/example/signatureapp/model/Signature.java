package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signatures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signature {

    @Id
    @Column(name = "id")
    private UUID id;

    // Название угрозы
    @Column(name = "threat_name", nullable = false)
    private String threatName;
    
    // Первые 8 байт сигнатуры (хранится как строка)
    @Column(name = "first_8_bytes", nullable = false)
    private String first8Bytes;
    
    // Хэш от остатка сигнатуры
    @Column(name = "remainder_hash", nullable = false)
    private String remainderHash;
    
    // Длина остатка сигнатуры
    @Column(name = "remainder_length", nullable = false)
    private Integer remainderLength;
    
    // Тип файла, для которого актуальна сигнатура
    @ManyToOne
    @JoinColumn(name = "file_type_id", nullable = false)
    private FileType fileType;
    
    // Смещение начала сигнатуры в файле
    @Column(name = "offset_start", nullable = false)
    private Integer startOffset;
    
    // Смещение конца сигнатуры в файле
    @Column(name = "offset_end", nullable = false)
    private Integer endOffset;
    
    // Электронная цифровая подпись
    @Column(name = "digital_signature", nullable = false, columnDefinition = "TEXT")
    private String digitalSignature;
    
    // Дата создания записи
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Дата последнего обновления записи
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Статус записи (ACTUAL, DELETED, CORRUPTED)
    @Column(name = "status", nullable = false)
    private String status;
    
    // OneToOne relationship with ScanReport
    @OneToOne(mappedBy = "detectedSignature")
    private ScanReport scanReport;
}
