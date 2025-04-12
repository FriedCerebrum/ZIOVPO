package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.SignatureDto;
import com.example.signatureapp.model.FileType;
import com.example.signatureapp.model.Signature;
import com.example.signatureapp.model.SignatureAudit;
import com.example.signatureapp.model.SignatureHistory;
import com.example.signatureapp.repository.FileTypeRepository;
import com.example.signatureapp.repository.SignatureAuditRepository;
import com.example.signatureapp.repository.SignatureHistoryRepository;
import com.example.signatureapp.repository.SignatureRepository;
import com.example.signatureapp.service.DigitalSignatureService;
import com.example.signatureapp.service.SignatureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;
    private final FileTypeRepository fileTypeRepository;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final SignatureAuditRepository signatureAuditRepository;
    private final DigitalSignatureService digitalSignatureService;

    @Autowired
    public SignatureServiceImpl(
            SignatureRepository signatureRepository, 
            FileTypeRepository fileTypeRepository,
            SignatureHistoryRepository signatureHistoryRepository,
            SignatureAuditRepository signatureAuditRepository,
            DigitalSignatureService digitalSignatureService) {
        this.signatureRepository = signatureRepository;
        this.fileTypeRepository = fileTypeRepository;
        this.signatureHistoryRepository = signatureHistoryRepository;
        this.signatureAuditRepository = signatureAuditRepository;
        this.digitalSignatureService = digitalSignatureService;
    }

    @Override
    public SignatureDto createSignature(SignatureDto signatureDto, String userId) {
        // Находим тип файла
        FileType fileType = fileTypeRepository.findById(signatureDto.getFileTypeId())
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + signatureDto.getFileTypeId()));

        // Создаем новую сигнатуру
        Signature signature = mapToEntity(signatureDto, fileType);
        signature.setId(UUID.randomUUID()); // Генерируем UUID
        signature.setCreatedAt(LocalDateTime.now());
        signature.setUpdatedAt(LocalDateTime.now());
        signature.setStatus("ACTUAL");
        
        // Формируем цифровую подпись
        String signatureContent = signature.getThreatName() + signature.getFirst8Bytes() + signature.getRemainderHash();
        String digitalSignatureValue = digitalSignatureService.signData(signatureContent);
        signature.setDigitalSignature(digitalSignatureValue);
        
        // Сохраняем сигнатуру
        Signature savedSignature = signatureRepository.save(signature);
        
        // Добавляем запись в аудит
        createAuditRecord(savedSignature.getId(), userId, "CREATE", "Создана новая сигнатура");
        
        return mapToDto(savedSignature);
    }

    @Override
    public SignatureDto getSignatureById(UUID id) {
        Signature signature = signatureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + id));
        return mapToDto(signature);
    }

    @Override
    public List<SignatureDto> getAllActualSignatures() {
        return signatureRepository.findByStatus("ACTUAL").stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SignatureDto> getSignaturesByThreatName(String threatName) {
        return signatureRepository.findByThreatName(threatName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SignatureDto> getSignaturesByFileType(Long fileTypeId) {
        FileType fileType = fileTypeRepository.findById(fileTypeId)
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + fileTypeId));

        return signatureRepository.findByFileType(fileType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SignatureDto findByFirst8Bytes(String first8Bytes) {
        Signature signature = signatureRepository.findByFirst8Bytes(first8Bytes)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with first 8 bytes: " + first8Bytes));
        return mapToDto(signature);
    }

    @Override
    public List<SignatureDto> findByOffsetRange(Integer startOffset, Integer endOffset) {
        return signatureRepository.findByStartOffsetBetween(startOffset, endOffset).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SignatureDto updateSignature(UUID id, SignatureDto signatureDto, String userId) {
        Signature existingSignature = signatureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + id));

        // Проверяем, что сигнатура актуальна
        if (!"ACTUAL".equals(existingSignature.getStatus())) {
            throw new IllegalStateException("Cannot update signature with status: " + existingSignature.getStatus());
        }

        // Находим тип файла
        FileType fileType = fileTypeRepository.findById(signatureDto.getFileTypeId())
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + signatureDto.getFileTypeId()));

        // Сохраняем историю перед обновлением
        saveSignatureHistory(existingSignature);

        // Обновляем поля
        existingSignature.setThreatName(signatureDto.getThreatName());
        existingSignature.setFirst8Bytes(signatureDto.getFirst8Bytes());
        existingSignature.setRemainderHash(signatureDto.getRemainderHash());
        existingSignature.setRemainderLength(signatureDto.getRemainderLength());
        existingSignature.setStartOffset(signatureDto.getStartOffset());
        existingSignature.setEndOffset(signatureDto.getEndOffset());
        existingSignature.setFileType(fileType);
        existingSignature.setUpdatedAt(LocalDateTime.now());
        
        // Формируем новую цифровую подпись
        String signatureContent = existingSignature.getThreatName() + existingSignature.getFirst8Bytes() + existingSignature.getRemainderHash();
        String digitalSignatureValue = digitalSignatureService.signData(signatureContent);
        existingSignature.setDigitalSignature(digitalSignatureValue);

        // Сохраняем обновленную сигнатуру
        Signature updatedSignature = signatureRepository.save(existingSignature);
        
        // Добавляем запись в аудит
        createAuditRecord(id, userId, "UPDATE", "Обновлена сигнатура");
        
        return mapToDto(updatedSignature);
    }

    @Override
    public void deleteSignature(UUID id, String userId) {
        Signature signature = signatureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + id));
        
        // Меняем статус на DELETED вместо физического удаления
        signature.setStatus("DELETED");
        signature.setUpdatedAt(LocalDateTime.now());
        signatureRepository.save(signature);
        
        // Добавляем запись в аудит
        createAuditRecord(id, userId, "DELETE", "Удалена сигнатура");
    }

    @Override
    public List<SignatureDto> getSignaturesUpdatedSince(LocalDateTime since) {
        return signatureRepository.findByUpdatedAtAfter(since).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SignatureDto> getSignaturesByIds(List<UUID> ids) {
        return signatureRepository.findAllById(ids).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SignatureHistory> getSignatureHistory(UUID signatureId) {
        return signatureHistoryRepository.findBySignatureIdOrderByVersionCreatedAtDesc(signatureId);
    }

    @Override
    public List<SignatureAudit> getSignatureAudit(UUID signatureId) {
        return signatureAuditRepository.findBySignatureIdOrderByChangedAtDesc(signatureId);
    }

    @Override
    public boolean verifySignatureDigitalSignature(UUID signatureId) {
        Signature signature = signatureRepository.findById(signatureId)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + signatureId));
        
        // Проверяем цифровую подпись
        String signatureContent = signature.getThreatName() + signature.getFirst8Bytes() + signature.getRemainderHash();
        boolean isValid = digitalSignatureService.verifySignature(signatureContent, signature.getDigitalSignature());
        
        // Если подпись некорректна, обновляем статус
        if (!isValid && "ACTUAL".equals(signature.getStatus())) {
            signature.setStatus("CORRUPTED");
            signatureRepository.save(signature);
            createAuditRecord(signatureId, "SYSTEM", "VERIFY", "Обнаружена некорректная цифровая подпись");
        }
        
        return isValid;
    }

    @Override
    public int verifyAllSignaturesUpdatedSince(LocalDateTime since) {
        List<Signature> signatures = signatureRepository.findByUpdatedAtAfter(since);
        int count = 0;
        
        for (Signature signature : signatures) {
            String signatureContent = signature.getThreatName() + signature.getFirst8Bytes() + signature.getRemainderHash();
            boolean isValid = digitalSignatureService.verifySignature(signatureContent, signature.getDigitalSignature());
            
            if (!isValid && "ACTUAL".equals(signature.getStatus())) {
                signature.setStatus("CORRUPTED");
                signatureRepository.save(signature);
                createAuditRecord(signature.getId(), "SYSTEM", "VERIFY", "Обнаружена некорректная цифровая подпись при массовой проверке");
            }
            
            count++;
        }
        
        return count;
    }

    @Override
    public List<SignatureDto> getSignaturesByStatus(String status) {
        return signatureRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    // Вспомогательный метод для сохранения истории сигнатуры
    private void saveSignatureHistory(Signature signature) {
        SignatureHistory history = SignatureHistory.builder()
                .signatureId(signature.getId())
                .threatName(signature.getThreatName())
                .first8Bytes(signature.getFirst8Bytes())
                .remainderHash(signature.getRemainderHash())
                .remainderLength(signature.getRemainderLength())
                .startOffset(signature.getStartOffset())
                .endOffset(signature.getEndOffset())
                .fileTypeId(signature.getFileType().getId())
                .digitalSignature(signature.getDigitalSignature())
                .versionCreatedAt(LocalDateTime.now())
                .status(signature.getStatus())
                .updatedAt(signature.getUpdatedAt())
                .build();
        
        signatureHistoryRepository.save(history);
    }
    
    // Вспомогательный метод для создания записи аудита
    private void createAuditRecord(UUID signatureId, String userId, String action, String details) {
        SignatureAudit audit = SignatureAudit.builder()
                .signatureId(signatureId)
                .changedBy(userId)
                .changeType(action)
                .changedAt(LocalDateTime.now())
                .fieldsChanged(details)
                .build();
        
        signatureAuditRepository.save(audit);
    }
    
    // Вспомогательный метод для маппинга DTO в сущность
    private Signature mapToEntity(SignatureDto signatureDto, FileType fileType) {
        return Signature.builder()
                .id(signatureDto.getId())
                .threatName(signatureDto.getThreatName())
                .first8Bytes(signatureDto.getFirst8Bytes())
                .remainderHash(signatureDto.getRemainderHash())
                .remainderLength(signatureDto.getRemainderLength())
                .startOffset(signatureDto.getStartOffset())
                .endOffset(signatureDto.getEndOffset())
                .fileType(fileType)
                .status(signatureDto.getStatus())
                .digitalSignature(signatureDto.getDigitalSignature())
                .createdAt(signatureDto.getCreatedAt())
                .updatedAt(signatureDto.getUpdatedAt())
                .build();
    }

    // Вспомогательный метод для маппинга сущности в DTO
    private SignatureDto mapToDto(Signature signature) {
        return SignatureDto.builder()
                .id(signature.getId())
                .threatName(signature.getThreatName())
                .first8Bytes(signature.getFirst8Bytes())
                .remainderHash(signature.getRemainderHash())
                .remainderLength(signature.getRemainderLength())
                .startOffset(signature.getStartOffset())
                .endOffset(signature.getEndOffset())
                .fileTypeId(signature.getFileType().getId())
                .status(signature.getStatus())
                .digitalSignature(signature.getDigitalSignature())
                .createdAt(signature.getCreatedAt())
                .updatedAt(signature.getUpdatedAt())
                .build();
    }
}
