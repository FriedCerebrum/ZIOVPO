package com.example.signatureapp.service.impl;

import com.example.signatureapp.dto.SignatureScanResultDto;
import com.example.signatureapp.model.Signature;
import com.example.signatureapp.repository.SignatureRepository;
import com.example.signatureapp.service.FileScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileScanServiceImpl implements FileScanService {

    private static final int WINDOW_SIZE = 8;
    private static final long BASE = 256;
    private static final long MOD = 1000000007;

    private final SignatureRepository signatureRepository;

    @Autowired
    public FileScanServiceImpl(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    // Scans the uploaded file for known byte signatures using a rolling hash and SHA-256 verification
    @Override
    public List<SignatureScanResultDto> scanFile(MultipartFile file) throws IOException {
        log.info("Starting scanFile for file: {}", file.getOriginalFilename());
        // Write uploaded file content to a temporary file for scanning
        File tempFile = File.createTempFile("upload_", "");
        // file saved to temp
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(file.getBytes());
        }

        // Load valid signatures from database, filtering out incomplete entries
        List<Signature> signatures = signatureRepository.findAll().stream()
                .filter(sig -> sig.getFirst8Bytes() != null
                            && sig.getRemainderHash() != null
                            && sig.getRemainderLength() != null)
                .collect(Collectors.toList());
        log.info("Valid signatures loaded for scanning: {}", signatures.size());
        log.debug("Hash index buckets: {}", signatures.size());
        // Build index mapping rolling hash of first WINDOW_SIZE bytes to signature list
        Map<Long, List<Signature>> hashIndex = signatures.stream()
                .collect(Collectors.groupingBy(sig -> computeHash(sig.getFirst8Bytes().getBytes())));

        List<SignatureScanResultDto> results = new ArrayList<>();

        // Open temporary file for random access to perform rolling-hash scanning
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "r")) {
            // Initialize rolling hash and precompute BASE^(WINDOW_SIZE-1) % MOD
            long windowHash = 0;
            long power = 1; // BASE^(WINDOW_SIZE-1) % MOD
            for (int i = 0; i < WINDOW_SIZE - 1; i++) {
                power = (power * BASE) % MOD;
            }

            // Read first WINDOW_SIZE bytes to initialize rolling hash
            byte[] window = new byte[WINDOW_SIZE];
            int bytesRead;
            raf.seek(0);
            bytesRead = raf.read(window);
            if (bytesRead < WINDOW_SIZE) {
                return results; // file smaller than window
            }
            for (byte b : window) {
                windowHash = (windowHash * BASE + (b & 0xFF)) % MOD;
            }

            // Slide over file content byte by byte
            long offset = 0;
            long fileLength = raf.length(); // длина файла для проверки чтения остатка
            while (true) {
                // Retrieve candidate signatures matching current rolling hash
                List<Signature> candidates = hashIndex.getOrDefault(windowHash, List.of());
                log.debug("Offset {}: {} candidate signatures", offset, candidates.size());
                if (!candidates.isEmpty()) {
                    for (Signature sig : candidates) {
                        // Verify exact match of first WINDOW_SIZE bytes to avoid hash collisions
                        byte[] firstBytes = new byte[WINDOW_SIZE];
                        raf.seek(offset);
                        raf.readFully(firstBytes);
                        if (MessageDigest.isEqual(firstBytes, sig.getFirst8Bytes().getBytes())) {
                            // Compute remainder offset and length
                            long remOffset = offset + WINDOW_SIZE;
                            int remLen = sig.getRemainderLength();
                            //Проверка на границы начало/конца сдвига из БД
                            //начало окна (первый байт) раньше, чем минимально допустимый startOffset из записи сигнатуры.
                            //ИЛИ конец окна (последний байт: смещение остатка плюс длина минус 1) выходит за максимально допустимый 
                            if (offset < sig.getStartOffset() || remOffset + remLen - 1 > sig.getEndOffset()) {
                                log.debug("Skipping signature {}: offset {} outside allowed range {}-{}", sig.getId(), offset, sig.getStartOffset(), sig.getEndOffset());
                                continue;
                            }
                            // Check remainder within file bounds
                            if (remOffset + remLen > fileLength) {
                                log.debug("Skipping signature {}: remainder beyond file end (offset {} + length {} > {})", sig.getId(), remOffset, remLen, fileLength);
                                continue;
                            }
                            // Read the remainder bytes and compute SHA-256 hash for final verification
                            byte[] remBytes = new byte[remLen];
                            raf.seek(remOffset);
                            raf.readFully(remBytes);
                            String remHash = hashBytes(remBytes);
                            if (remHash.equals(sig.getRemainderHash())) {
                                // Match found: record result with start and end offsets
                                log.info("Signature {} ({}) matched at offsets {}-{}", sig.getId(), sig.getThreatName(), offset, remOffset + remLen - 1);
                                SignatureScanResultDto dto = new SignatureScanResultDto(
                                        sig.getId(),
                                        sig.getThreatName(),
                                        offset,
                                        remOffset + remLen - 1,
                                        true
                                );
                                results.add(dto);
                            }
                        }
                    }
                }

                // Slide window by one byte and update rolling hash in O(1)
                raf.seek(offset + WINDOW_SIZE);
                int next = raf.read();
                if (next == -1) break;
                byte old = window[0];
                // Shift window array
                System.arraycopy(window, 1, window, 0, WINDOW_SIZE - 1);
                window[WINDOW_SIZE - 1] = (byte) next;

                // Update hash
                windowHash = (windowHash - ((old & 0xFF) * power) % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + (next & 0xFF)) % MOD;

                offset++;
            }
        } finally {
            tempFile.delete();
        }

        log.info("Scan completed for file {}: {} matches found", file.getOriginalFilename(), results.size());
        return results;
    }

    // Compute rolling hash for a byte array using base and mod constants
    private long computeHash(byte[] data) {
        long h = 0;
        for (byte b : data) {
            h = (h * BASE + (b & 0xFF)) % MOD;
        }
        return h;
    }

    // Compute SHA-256 hash of data and return hexadecimal string
    private String hashBytes(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error hashing bytes", e);
            throw new RuntimeException(e);
        }
    }
}
