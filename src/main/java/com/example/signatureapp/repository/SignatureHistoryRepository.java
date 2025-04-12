package com.example.signatureapp.repository;

import com.example.signatureapp.model.SignatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, UUID> {
    // Поиск истории изменений для конкретной сигнатуры
    List<SignatureHistory> findBySignatureIdOrderByVersionCreatedAtDesc(UUID signatureId);
}
