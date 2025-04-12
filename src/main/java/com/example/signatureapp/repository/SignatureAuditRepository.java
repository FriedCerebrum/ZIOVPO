package com.example.signatureapp.repository;

import com.example.signatureapp.model.SignatureAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureAuditRepository extends JpaRepository<SignatureAudit, UUID> {
    // u041fu043eu0438u0441u043a u0437u0430u043fu0438u0441u0435u0439 u0430u0443u0434u0438u0442u0430 u0434u043bu044f u043au043eu043du043au0440u0435u0442u043du043eu0439 u0441u0438u0433u043du0430u0442u0443u0440u044b
    List<SignatureAudit> findBySignatureIdOrderByChangedAtDesc(UUID signatureId);
    
    // u041fu043eu0438u0441u043a u0437u0430u043fu0438u0441u0435u0439 u0430u0443u0434u0438u0442u0430 u043fu043e u0442u0438u043fu0443 u0438u0437u043cu0435u043du0435u043du0438u044f
    List<SignatureAudit> findByChangeType(String changeType);
    
    // u041fu043eu0438u0441u043a u0437u0430u043fu0438u0441u0435u0439 u0430u0443u0434u0438u0442u0430 u043fu043e u043fu043eu043bu044cu0437u043eu0432u0430u0442u0435u043bu044e
    List<SignatureAudit> findByChangedBy(String changedBy);
    
    // u041fu043eu0438u0441u043a u0437u0430u043fu0438u0441u0435u0439 u0430u0443u0434u0438u0442u0430 u0437u0430 u043fu0435u0440u0438u043eu0434 u0432u0440u0435u043cu0435u043du0438
    List<SignatureAudit> findByChangedAtBetween(LocalDateTime start, LocalDateTime end);
}
