package com.example.signatureapp.service;

public interface DigitalSignatureService {
    /**
     * Создает цифровую подпись для строковых данных
     *
     * @param data Данные для подписи
     * @return Строка с цифровой подписью
     */
    String signData(String data);
    
    /**
     * Проверяет цифровую подпись для строковых данных
     *
     * @param data Данные для проверки
     * @param signature Цифровая подпись для проверки
     * @return true если подпись верна, false иначе
     */
    boolean verifySignature(String data, String signature);

    /**
     * Инициализирует пару ключей для подписи
     */
    void initializeKeyPair();
    
    /**
     * Возвращает публичный ключ в формате Base64
     * 
     * @return Строка с публичным ключом в формате Base64
     */
    String getPublicKey();
}
