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
    private static final int CHUNK_SIZE = 4096;
    private static final long BASE = 256;
    private static final long MOD = 1000000007;

    private final SignatureRepository signatureRepository;

    @Autowired
    public FileScanServiceImpl(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    @Override
    public List<SignatureScanResultDto> scanFile(MultipartFile file) throws IOException {
        log.info("Starting scanFile for file: {}", file.getOriginalFilename());
        // Save multipart file to temp file
        File tempFile = File.createTempFile("upload_", "");
        // file saved to temp
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(file.getBytes());
        }

        // Load all valid signatures and index by rolling hash of first8Bytes
        List<Signature> signatures = signatureRepository.findAll().stream()
                .filter(sig -> sig.getFirst8Bytes() != null
                            && sig.getRemainderHash() != null
                            && sig.getRemainderLength() != null)
                .collect(Collectors.toList());
        log.info("Valid signatures loaded for scanning: {}", signatures.size());
        log.debug("Hash index buckets: {}", signatures.size());
        Map<Long, List<Signature>> hashIndex = signatures.stream()
                .collect(Collectors.groupingBy(sig -> computeHash(sig.getFirst8Bytes().getBytes())));

        List<SignatureScanResultDto> results = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "r")) {
            long windowHash = 0;
            long power = 1; // BASE^(WINDOW_SIZE-1) % MOD
            for (int i = 0; i < WINDOW_SIZE - 1; i++) {
                power = (power * BASE) % MOD;
            }

            byte[] window = new byte[WINDOW_SIZE];
            int bytesRead;
            long offset = 0;
            long fileLength = raf.length(); // длина файла для проверки чтения остатка

            // Pre-read first WINDOW_SIZE bytes
            raf.seek(0);
            bytesRead = raf.read(window);
            if (bytesRead < WINDOW_SIZE) {
                return results; // file smaller than window
            }
            for (byte b : window) {
                windowHash = (windowHash * BASE + (b & 0xFF)) % MOD;
            }

            while (true) {
                // Check for matches in index
                List<Signature> candidates = hashIndex.getOrDefault(windowHash, List.of());
                log.debug("Offset {}: {} candidate signatures", offset, candidates.size());
                if (!candidates.isEmpty()) {
                    for (Signature sig : candidates) {
                        byte[] firstBytes = new byte[WINDOW_SIZE];
                        raf.seek(offset);
                        raf.readFully(firstBytes);
                        if (MessageDigest.isEqual(firstBytes, sig.getFirst8Bytes().getBytes())) {
                            // Check remainder
                            long remOffset = offset + WINDOW_SIZE;
                            int remLen = sig.getRemainderLength();
                            if (remOffset + remLen > fileLength) {
                                log.debug("Skipping signature {}: remainder beyond file end (offset {} + length {} > {})", sig.getId(), remOffset, remLen, fileLength);
                                continue;
                            }
                            byte[] remBytes = new byte[remLen];
                            raf.seek(remOffset);
                            raf.readFully(remBytes);
                            String remHash = hashBytes(remBytes);
                            if (remHash.equals(sig.getRemainderHash())) {
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

                // Slide window
                // Read next byte
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

    private long computeHash(byte[] data) {
        long h = 0;
        for (byte b : data) {
            h = (h * BASE + (b & 0xFF)) % MOD;
        }
        return h;
    }

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
