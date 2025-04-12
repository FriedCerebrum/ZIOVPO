package com.example.signatureapp.scheduler;

import com.example.signatureapp.service.SignatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Планировщик для регулярной проверки ЭЦП сигнатур
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SignatureVerificationScheduler {

    private final SignatureService signatureService;
    
    @Value("${signature.verification.check-period-hours:24}")
    private int verificationPeriodHours;
    
    private LocalDateTime lastVerificationTime = LocalDateTime.now().minusDays(1);
    
    /**
     * Задача по расписанию для проверки ЭЦП сигнатур
     * По умолчанию запускается раз в сутки
     */
    @Scheduled(cron = "${signature.verification.cron:0 0 0 * * ?}")
    public void verifySignatures() {
        log.info("Starting scheduled signature verification task");
        
        try {
            LocalDateTime checkFrom = lastVerificationTime;
            lastVerificationTime = LocalDateTime.now();
            
            int verifiedCount = signatureService.verifyAllSignaturesUpdatedSince(checkFrom);
            
            log.info("Completed signature verification. Verified {} signatures", verifiedCount);
        } catch (Exception e) {
            log.error("Error during scheduled signature verification: {}", e.getMessage(), e);
        }
    }
}
