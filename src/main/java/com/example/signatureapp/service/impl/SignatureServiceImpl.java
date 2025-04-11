package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.SignatureDto;
import com.example.signatureapp.model.FileType;
import com.example.signatureapp.model.Signature;
import com.example.signatureapp.repository.FileTypeRepository;
import com.example.signatureapp.repository.SignatureRepository;
import com.example.signatureapp.service.SignatureService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;
    private final FileTypeRepository fileTypeRepository;

    @Autowired
    public SignatureServiceImpl(SignatureRepository signatureRepository, FileTypeRepository fileTypeRepository) {
        this.signatureRepository = signatureRepository;
        this.fileTypeRepository = fileTypeRepository;
    }

    @Override
    public SignatureDto createSignature(SignatureDto signatureDto) {
        // Find file type
        FileType fileType = fileTypeRepository.findById(signatureDto.getFileTypeId())
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + signatureDto.getFileTypeId()));

        Signature signature = mapToEntity(signatureDto, fileType);
        Signature savedSignature = signatureRepository.save(signature);
        return mapToDto(savedSignature);
    }

    @Override
    public SignatureDto getSignatureById(Long id) {
        Signature signature = signatureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + id));
        return mapToDto(signature);
    }

    @Override
    public List<SignatureDto> getAllSignatures() {
        return signatureRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SignatureDto> getSignaturesByObjectName(String objectName) {
        return signatureRepository.findByObjectName(objectName).stream()
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
    public List<SignatureDto> findByOffsetRange(Long startOffset, Long endOffset) {
        return signatureRepository.findByStartOffsetBetween(startOffset, endOffset).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SignatureDto updateSignature(Long id, SignatureDto signatureDto) {
        Signature existingSignature = signatureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Signature not found with id: " + id));

        // Find file type
        FileType fileType = fileTypeRepository.findById(signatureDto.getFileTypeId())
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + signatureDto.getFileTypeId()));

        // Update fields
        existingSignature.setObjectName(signatureDto.getObjectName());
        existingSignature.setFirst8Bytes(signatureDto.getFirst8Bytes());
        existingSignature.setRemainderHash(signatureDto.getRemainderHash());
        existingSignature.setRemainderLength(signatureDto.getRemainderLength());
        existingSignature.setStartOffset(signatureDto.getStartOffset());
        existingSignature.setEndOffset(signatureDto.getEndOffset());
        existingSignature.setFileType(fileType);

        Signature updatedSignature = signatureRepository.save(existingSignature);
        return mapToDto(updatedSignature);
    }

    @Override
    public void deleteSignature(Long id) {
        if (!signatureRepository.existsById(id)) {
            throw new EntityNotFoundException("Signature not found with id: " + id);
        }
        signatureRepository.deleteById(id);
    }

    // Helper method to map DTO to entity
    private Signature mapToEntity(SignatureDto signatureDto, FileType fileType) {
        return Signature.builder()
                .id(signatureDto.getId())
                .objectName(signatureDto.getObjectName())
                .first8Bytes(signatureDto.getFirst8Bytes())
                .remainderHash(signatureDto.getRemainderHash())
                .remainderLength(signatureDto.getRemainderLength())
                .startOffset(signatureDto.getStartOffset())
                .endOffset(signatureDto.getEndOffset())
                .fileType(fileType)
                .build();
    }

    // Helper method to map entity to DTO
    private SignatureDto mapToDto(Signature signature) {
        return SignatureDto.builder()
                .id(signature.getId())
                .objectName(signature.getObjectName())
                .first8Bytes(signature.getFirst8Bytes())
                .remainderHash(signature.getRemainderHash())
                .remainderLength(signature.getRemainderLength())
                .startOffset(signature.getStartOffset())
                .endOffset(signature.getEndOffset())
                .fileTypeId(signature.getFileType().getId())
                .build();
    }
}
