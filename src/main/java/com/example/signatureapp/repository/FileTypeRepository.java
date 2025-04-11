package com.example.signatureapp.repository;

import com.example.signatureapp.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Long> {
    Optional<FileType> findByName(String name);
    Optional<FileType> findByExtension(String extension);
    boolean existsByName(String name);
}
