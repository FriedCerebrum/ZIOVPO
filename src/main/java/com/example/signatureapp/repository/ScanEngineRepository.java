package com.example.signatureapp.repository;

import com.example.signatureapp.model.ScanEngine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScanEngineRepository extends JpaRepository<ScanEngine, Long> {
    Optional<ScanEngine> findByName(String name);
    List<ScanEngine> findByIsActive(boolean isActive);
    boolean existsByName(String name);
}
