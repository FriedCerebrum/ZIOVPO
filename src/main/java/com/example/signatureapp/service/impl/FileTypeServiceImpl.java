package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.FileTypeDto;
import com.example.signatureapp.model.FileType;
import com.example.signatureapp.repository.FileTypeRepository;
import com.example.signatureapp.service.FileTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileTypeServiceImpl implements FileTypeService {

    private final FileTypeRepository fileTypeRepository;

    @Autowired
    public FileTypeServiceImpl(FileTypeRepository fileTypeRepository) {
        this.fileTypeRepository = fileTypeRepository;
    }

    @Override
    public FileTypeDto createFileType(FileTypeDto fileTypeDto) {
        // Check if a file type with the same name already exists
        if (fileTypeRepository.existsByName(fileTypeDto.getName())) {
            throw new IllegalArgumentException("A file type with name " + fileTypeDto.getName() + " already exists");
        }

        FileType fileType = mapToEntity(fileTypeDto);
        FileType savedFileType = fileTypeRepository.save(fileType);
        return mapToDto(savedFileType);
    }

    @Override
    public FileTypeDto getFileTypeById(Long id) {
        FileType fileType = fileTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + id));
        return mapToDto(fileType);
    }

    @Override
    public List<FileTypeDto> getAllFileTypes() {
        return fileTypeRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FileTypeDto updateFileType(Long id, FileTypeDto fileTypeDto) {
        FileType existingFileType = fileTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File type not found with id: " + id));

        // Check if a different file type with the same name exists
        if (!existingFileType.getName().equals(fileTypeDto.getName()) && 
                fileTypeRepository.existsByName(fileTypeDto.getName())) {
            throw new IllegalArgumentException("A file type with name " + fileTypeDto.getName() + " already exists");
        }

        // Update fields
        existingFileType.setName(fileTypeDto.getName());
        existingFileType.setDescription(fileTypeDto.getDescription());
        existingFileType.setExtension(fileTypeDto.getExtension());
        existingFileType.setMimeType(fileTypeDto.getMimeType());
        existingFileType.setBinary(fileTypeDto.isBinary());

        FileType updatedFileType = fileTypeRepository.save(existingFileType);
        return mapToDto(updatedFileType);
    }

    @Override
    public void deleteFileType(Long id) {
        if (!fileTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("File type not found with id: " + id);
        }
        fileTypeRepository.deleteById(id);
    }

    @Override
    public FileTypeDto findByName(String name) {
        FileType fileType = fileTypeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("File type not found with name: " + name));
        return mapToDto(fileType);
    }

    @Override
    public FileTypeDto findByExtension(String extension) {
        FileType fileType = fileTypeRepository.findByExtension(extension)
                .orElseThrow(() -> new EntityNotFoundException("File type not found with extension: " + extension));
        return mapToDto(fileType);
    }

    // Helper method to map DTO to entity
    private FileType mapToEntity(FileTypeDto fileTypeDto) {
        return FileType.builder()
                .id(fileTypeDto.getId())
                .name(fileTypeDto.getName())
                .description(fileTypeDto.getDescription())
                .extension(fileTypeDto.getExtension())
                .mimeType(fileTypeDto.getMimeType())
                .isBinary(fileTypeDto.isBinary())
                .build();
    }

    // Helper method to map entity to DTO
    private FileTypeDto mapToDto(FileType fileType) {
        return FileTypeDto.builder()
                .id(fileType.getId())
                .name(fileType.getName())
                .description(fileType.getDescription())
                .extension(fileType.getExtension())
                .mimeType(fileType.getMimeType())
                .isBinary(fileType.isBinary())
                .build();
    }
}
