package com.example.signatureapp.repository;

import com.example.signatureapp.model.FileType;
import com.example.signatureapp.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
    List<Signature> findByThreatName(String threatName);
    List<Signature> findByFileType(FileType fileType);
    Optional<Signature> findByFirst8Bytes(String first8Bytes);
    List<Signature> findByStartOffsetBetween(Integer startOffset, Integer endOffset);
    
    // Поиск сигнатур, обновленных после указанной даты
    List<Signature> findByUpdatedAtAfter(LocalDateTime since);
    
    // Поиск сигнатур по статусу
    List<Signature> findByStatus(String status);
    
    // Поиск сигнатур, обновленных после указанной даты и с определенным статусом
    List<Signature> findByUpdatedAtAfterAndStatus(LocalDateTime since, String status);
    
    // Поиск сигнатур по списку GUID
    @Query("SELECT s FROM Signature s WHERE s.id IN :ids")
    List<Signature> findByIdList(@Param("ids") List<UUID> ids);
}
