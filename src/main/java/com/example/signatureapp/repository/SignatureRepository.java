package com.example.signatureapp.repository;

import com.example.signatureapp.model.FileType;
import com.example.signatureapp.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
    List<Signature> findByObjectName(String objectName);
    List<Signature> findByFileType(FileType fileType);
    Optional<Signature> findByFirst8Bytes(String first8Bytes);
    List<Signature> findByStartOffsetBetween(Long startOffset, Long endOffset);
}
