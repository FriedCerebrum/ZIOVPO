package com.example.signatureapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signature_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    // Ссылка на запись в таблице сигнатур
    @Column(name = "signature_id", nullable = false)
    private UUID signatureId;

    // Момент времени, когда появилась эта версия
    @Column(name = "version_created_at", nullable = false)
    private LocalDateTime versionCreatedAt;

    // Название угрозы (копия поля на момент сохранения в истории)
    @Column(name = "threat_name", nullable = false)
    private String threatName;
    
    // Первые 8 байт сигнатуры (копия поля)
    @Column(name = "first_8_bytes", nullable = false)
    private String first8Bytes;
    
    // Хэш от остатка сигнатуры (копия поля)
    @Column(name = "remainder_hash", nullable = false)
    private String remainderHash;
    
    // Длина остатка сигнатуры (копия поля)
    @Column(name = "remainder_length", nullable = false)
    private Integer remainderLength;
    
    // Тип файла (копия поля) - сохраняем только ID
    @Column(name = "file_type_id", nullable = false)
    private Long fileTypeId;
    
    // Смещение начала сигнатуры (копия поля)
    @Column(name = "start_offset", nullable = false)
    private Integer startOffset;
    
    // Смещение конца сигнатуры (копия поля)
    @Column(name = "offset_end", nullable = false)
    private Long endOffset;
    
    // Копия ЭЦП
    @Column(name = "digital_signature", nullable = false, columnDefinition = "TEXT")
    private String digitalSignature;
    
    // Копия статуса записи на момент сохранения версии
    @Column(name = "status", nullable = false)
    private String status;
    
    // Копия значения updated_at
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
