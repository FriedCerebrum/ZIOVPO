package com.example.signatureapp.service.impl;

import com.example.signatureapp.constants.SignatureConstants;
import com.example.signatureapp.dto.SignatureScanResult;
import com.example.signatureapp.model.Signature;
import com.example.signatureapp.repository.SignatureRepository;
import com.example.signatureapp.service.FileScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileScanServiceImpl implements FileScanService {

    private final SignatureRepository signatureRepository;

    // Rabin-Karp constants
    private static final int WINDOW_SIZE = 8; // Size of first_bytes
    private static final long BASE = 256; // Number of possible byte values
    private static final long MOD = 1_000_000_007; // A large prime number for modulo operation
    private static final long MAX_POWER = precomputeMaxPower(); // BASE^(WINDOW_SIZE - 1) % MOD

    // Precompute BASE^(WINDOW_SIZE - 1) % MOD
    private static long precomputeMaxPower() {
        long power = 1;
        for (int i = 0; i < WINDOW_SIZE - 1; i++) {
            power = (power * BASE) % MOD;
        }
        return power;
    }

    @Override
    public List<SignatureScanResult> scanFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File for scanning cannot be null or empty.");
        }

        Path tempFilePath = null;
        RandomAccessFile raf = null;
        List<SignatureScanResult> results = new ArrayList<>();

        try {
            // 1. Save uploaded file to a temporary file for random access
            tempFilePath = Files.createTempFile("scan_", "_" + file.getOriginalFilename());
            file.transferTo(tempFilePath);
            log.info("Saved uploaded file to temporary path: {}", tempFilePath);

            // 2. Load actual signatures and prepare for lookup
            List<Signature> signatures = signatureRepository.findByStatus(SignatureConstants.STATUS_ACTUAL);
            if (signatures.isEmpty()) {
                log.warn("No actual signatures found in the database. Scanning will yield no results.");
                return Collections.emptyList();
            }

            // Precompute hashes for first_bytes and store in a map for quick lookup
            // Key: Rabin-Karp hash of first_bytes, Value: List of signatures with that hash
            Map<Long, List<Signature>> signatureHashMap = precomputeSignatureHashes(signatures);

            // 3. Open temporary file for reading
            raf = new RandomAccessFile(tempFilePath.toFile(), "r");
            long fileSize = raf.length();
            if (fileSize < WINDOW_SIZE) {
                log.info("File size ({}) is smaller than window size ({}). No signatures can match.", fileSize, WINDOW_SIZE);
                return Collections.emptyList();
            }

            // 4. Perform Rabin-Karp sliding window scan
            byte[] window = new byte[WINDOW_SIZE];
            long currentHash = 0;
            long currentPosition = 0; // Position of the *start* of the window

            // Initialize the first window and its hash
            raf.readFully(window);
            currentHash = calculateInitialHash(window);

            // Main scanning loop
            while (true) {
                // Check if current hash matches any signature's first_bytes hash
                if (signatureHashMap.containsKey(currentHash)) {
                    List<Signature> potentialMatches = signatureHashMap.get(currentHash);
                    byte[] currentWindowBytes = readBytesFromFile(raf, currentPosition, WINDOW_SIZE); // Read actual bytes for verification

                    if (currentWindowBytes != null) {
                        for (Signature sig : potentialMatches) {
                            byte[] sigFirstBytes = sig.getFirst8Bytes().getBytes(StandardCharsets.UTF_8); // Assuming UTF-8, adjust if needed

                            // a) Verify byte-by-byte (handle hash collisions)
                            if (Arrays.equals(currentWindowBytes, sigFirstBytes)) {
                                log.debug("Rabin-Karp hash and byte match for signature '{}' at offset {}", sig.getThreatName(), currentPosition);

                                // b) Check offset constraints
                                if (currentPosition >= sig.getStartOffset() && currentPosition <= sig.getEndOffset()) {
                                     log.debug("Offset check passed for signature '{}' at offset {}", sig.getThreatName(), currentPosition);

                                    // c) Verify remainder hash (tail check)
                                    if (verifyRemainder(raf, currentPosition + WINDOW_SIZE, sig)) {
                                        log.info("Full signature match found: '{}' at offset {}", sig.getThreatName(), currentPosition);
                                        results.add(SignatureScanResult.builder()
                                                .signatureId(sig.getId())
                                                .threatName(sig.getThreatName())
                                                .offsetFromStart(currentPosition)
                                                .offsetFromEnd(currentPosition + WINDOW_SIZE + sig.getRemainderLength() - 1)
                                                .matched(true)
                                                .build());
                                        // Optional: break if only one match per position is needed
                                    } else {
                                         log.debug("Remainder hash mismatch for signature '{}' at offset {}", sig.getThreatName(), currentPosition);
                                    }
                                } else {
                                     log.debug("Offset check failed for signature '{}' at offset {}. Allowed range: [{}, {}]",
                                             sig.getThreatName(), currentPosition, sig.getStartOffset(), sig.getEndOffset());
                                }
                            }
                        }
                    }
                }

                // Slide the window: Check if we can read the next byte
                if (currentPosition + WINDOW_SIZE >= fileSize) {
                    break; // End of file reached
                }

                // Read the next byte to slide the window
                raf.seek(currentPosition + WINDOW_SIZE);
                int nextByteInt = raf.read();
                if (nextByteInt == -1) {
                    break; // Should not happen if check above is correct, but safety first
                }
                byte nextByte = (byte) nextByteInt;
                byte oldestByte = window[0]; // Byte to remove

                // Update hash using rolling hash formula
                currentHash = updateHash(currentHash, oldestByte, nextByte);

                // Update the window buffer (shift bytes left, add new byte at the end)
                System.arraycopy(window, 1, window, 0, WINDOW_SIZE - 1);
                window[WINDOW_SIZE - 1] = nextByte;

                currentPosition++; // Move window start position
            }

            log.info("Scanning completed for file '{}'. Found {} matches.", file.getOriginalFilename(), results.size());
            return results;

        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Error during file scanning: {}", e.getMessage(), e);
            throw new IOException("Failed to scan file due to an internal error.", e);
        } finally {
            // 5. Clean up: Close file and delete temporary file
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    log.error("Failed to close RandomAccessFile: {}", e.getMessage(), e);
                }
            }
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                    log.info("Deleted temporary file: {}", tempFilePath);
                } catch (IOException e) {
                    log.error("Failed to delete temporary file {}: {}", tempFilePath, e.getMessage(), e);
                }
            }
        }
    }

    // Helper to calculate initial hash for the first window
    private long calculateInitialHash(byte[] window) {
        long hash = 0;
        for (int i = 0; i < WINDOW_SIZE; i++) {
            hash = (hash * BASE + (window[i] & 0xFF)) % MOD; // Use & 0xFF to treat byte as unsigned
        }
        return hash;
    }

    // Helper to update hash when sliding the window
    private long updateHash(long oldHash, byte oldestByte, byte newByte) {
        long termToRemove = (MAX_POWER * (oldestByte & 0xFF)) % MOD;
        long updatedHash = (oldHash - termToRemove + MOD) % MOD; // Add MOD to handle potential negative result
        updatedHash = (updatedHash * BASE + (newByte & 0xFF)) % MOD;
        return updatedHash;
    }

    // Helper to precompute Rabin-Karp hashes for all signatures' first_bytes
    private Map<Long, List<Signature>> precomputeSignatureHashes(List<Signature> signatures) {
        Map<Long, List<Signature>> signatureHashMap = new HashMap<>();
        for (Signature sig : signatures) {
            String firstBytesStr = sig.getFirst8Bytes();
            if (firstBytesStr != null && firstBytesStr.length() == WINDOW_SIZE) { // Ensure it's exactly 8 bytes/chars
                byte[] firstBytes = firstBytesStr.getBytes(StandardCharsets.UTF_8); // Assuming UTF-8
                long hash = calculateInitialHash(firstBytes);
                signatureHashMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(sig);
            } else if (firstBytesStr != null) {
                 log.warn("Signature '{}' (ID: {}) has first_bytes length != {}. Skipping hash calculation.",
                         sig.getThreatName(), sig.getId(), WINDOW_SIZE);
            } else {
                 log.warn("Signature '{}' (ID: {}) has null first_bytes. Skipping hash calculation.",
                         sig.getThreatName(), sig.getId());
            }
        }
        log.info("Precomputed Rabin-Karp hashes for {} signature patterns.", signatureHashMap.size());
        return signatureHashMap;
    }

    // Helper to read a specific number of bytes from a file at a given offset
    private byte[] readBytesFromFile(RandomAccessFile raf, long position, int length) throws IOException {
        if (position + length > raf.length()) {
            return null; // Not enough bytes left in the file
        }
        byte[] buffer = new byte[length];
        raf.seek(position);
        int bytesRead = raf.read(buffer);
        if (bytesRead != length) {
            // This might happen if the file is modified concurrently, or seek issue
             log.warn("Expected to read {} bytes at offset {}, but read {}.", length, position, bytesRead);
            return null;
        }
        return buffer;
    }


    // Helper to verify the remainder hash (tail check)
    private boolean verifyRemainder(RandomAccessFile raf, long remainderStartPosition, Signature sig) throws IOException, NoSuchAlgorithmException {
        int remainderLength = sig.getRemainderLength();
        String expectedHash = sig.getRemainderHash();

        if (remainderLength <= 0 || expectedHash == null || expectedHash.isEmpty()) {
            log.debug("Signature '{}' has no remainder to check (length={}, hash={}). Assuming match.",
                     sig.getThreatName(), remainderLength, expectedHash);
            return true; // No remainder to check, so it matches by default
        }

        if (remainderStartPosition + remainderLength > raf.length()) {
            log.debug("Not enough bytes left in file to read remainder for signature '{}'. Start: {}, Length: {}, FileSize: {}",
                     sig.getThreatName(), remainderStartPosition, remainderLength, raf.length());
            return false; // Not enough bytes left in the file for the remainder
        }

        byte[] remainderBytes = readBytesFromFile(raf, remainderStartPosition, remainderLength);
        if (remainderBytes == null) {
             log.warn("Failed to read remainder bytes for signature '{}' at offset {}", sig.getThreatName(), remainderStartPosition);
            return false;
        }

        // Calculate SHA-256 hash of the remainder bytes
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] calculatedHashBytes = digest.digest(remainderBytes);
        String calculatedHashHex = bytesToHex(calculatedHashBytes);

         log.debug("Remainder check for '{}': Expected Hash = {}, Calculated Hash = {}",
                  sig.getThreatName(), expectedHash, calculatedHashHex);

        // Compare calculated hash with the expected hash (case-insensitive)
        return calculatedHashHex.equalsIgnoreCase(expectedHash);
    }

    // Helper to convert byte array to hex string
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
