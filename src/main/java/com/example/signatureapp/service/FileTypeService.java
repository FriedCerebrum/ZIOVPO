package com.example.signatureapp.service;

import com.example.signatureapp.dto.FileTypeDto;

import java.util.List;

public interface FileTypeService {
    FileTypeDto createFileType(FileTypeDto fileTypeDto);
    FileTypeDto getFileTypeById(Long id);
    List<FileTypeDto> getAllFileTypes();
    FileTypeDto updateFileType(Long id, FileTypeDto fileTypeDto);
    void deleteFileType(Long id);
    FileTypeDto findByName(String name);
    FileTypeDto findByExtension(String extension);
}
