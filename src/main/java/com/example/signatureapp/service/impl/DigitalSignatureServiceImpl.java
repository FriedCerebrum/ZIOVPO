package com.example.signatureapp.service.impl;

import com.example.signatureapp.service.DigitalSignatureService;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class DigitalSignatureServiceImpl implements DigitalSignatureService {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;

    private KeyPair keyPair;
    
    @Value("${signature.key.public:#{null}}")
    private String publicKeyBase64;
    
    @Value("${signature.key.private:#{null}}")
    private String privateKeyBase64;

    @PostConstruct
    public void init() {
        // u0414u043eu0431u0430u0432u043bu044fu0435u043c BouncyCastle u043fu0440u043eu0432u0430u0439u0434u0435u0440
        Security.addProvider(new BouncyCastleProvider());
        
        // u0418u043du0438u0446u0438u0430u043bu0438u0437u0438u0440u0443u0435u043c u043au043bu044eu0447u0438 u043fu0440u0438 u0437u0430u043fu0443u0441u043au0435
        if (publicKeyBase64 == null || privateKeyBase64 == null || 
            publicKeyBase64.isEmpty() || privateKeyBase64.isEmpty()) {
            initializeKeyPair();
        } else {
            loadKeyPair();
        }
    }

    @Override
    public String signData(String data) {
        try {
            // Создаем объект для цифровой подписи
            java.security.Signature sig = java.security.Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initSign(keyPair.getPrivate());
            sig.update(data.getBytes(StandardCharsets.UTF_8));
            
            // Формируем подпись и кодируем в Base64
            byte[] signatureBytes = sig.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error signing data: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(String data, String signature) {
        try {
            // Раскодируем подпись из Base64
            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            
            // Создаем объект для проверки подписи
            java.security.Signature sig = java.security.Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(keyPair.getPublic());
            sig.update(data.getBytes(StandardCharsets.UTF_8));
            
            // Проверяем подпись
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            // В случае ошибки вернем false
            return false;
        }
    }

    @Override
    public void initializeKeyPair() {
        try {
            // Создаем генератор ключей
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            
            // Генерируем пару ключей
            this.keyPair = keyPairGenerator.generateKeyPair();
            
            // Сохраняем ключи в Base64 формате
            this.publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            this.privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            
            // Здесь можно добавить код для сохранения ключей в надежном хранилище
        } catch (Exception e) {
            throw new RuntimeException("Error initializing key pair: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getPublicKey() {
        return publicKeyBase64;
    }
    
    private void loadKeyPair() {
        try {
            // Загружаем ключи из Base64 формата
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            
            // Создаем ключи из байтов
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            
            // Создаем пару ключей
            this.keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException("Error loading key pair: " + e.getMessage(), e);
        }
    }
}
