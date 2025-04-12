package com.example.signatureapp.service;

import com.example.signatureapp.dto.SignatureDto;
import com.example.signatureapp.model.SignatureAudit;
import com.example.signatureapp.model.SignatureHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SignatureService {
    /**
     * Создание новой сигнатуры с формированием ЭЦП
     * @param signatureDto данные сигнатуры
     * @param userId идентификатор пользователя, создающего сигнатуру
     * @return созданная сигнатура с ЭЦП
     */
    SignatureDto createSignature(SignatureDto signatureDto, String userId);
    
    /**
     * Получение сигнатуры по идентификатору
     * @param id идентификатор сигнатуры
     * @return найденная сигнатура или null
     */
    SignatureDto getSignatureById(UUID id);
    
    /**
     * Получение всех актуальных (ACTUAL) сигнатур
     * @return список актуальных сигнатур
     */
    List<SignatureDto> getAllActualSignatures();
    
    /**
     * Получение сигнатур по названию угрозы
     * @param threatName название угрозы
     * @return список найденных сигнатур
     */
    List<SignatureDto> getSignaturesByThreatName(String threatName);
    
    /**
     * Получение сигнатур по типу файла
     * @param fileTypeId идентификатор типа файла
     * @return список найденных сигнатур
     */
    List<SignatureDto> getSignaturesByFileType(Long fileTypeId);
    
    /**
     * Поиск сигнатуры по первым 8 байтам
     * @param first8Bytes строковое представление первых 8 байт
     * @return найденная сигнатура или null
     */
    SignatureDto findByFirst8Bytes(String first8Bytes);
    
    /**
     * Поиск сигнатур по диапазону смещений
     * @param offsetStart начальное смещение
     * @param offsetEnd конечное смещение
     * @return список найденных сигнатур
     */
    List<SignatureDto> findByOffsetRange(Integer offsetStart, Integer offsetEnd);
    
    /**
     * Обновление сигнатуры с сохранением истории и формированием новой ЭЦП
     * @param id идентификатор сигнатуры
     * @param signatureDto новые данные сигнатуры
     * @param userId идентификатор пользователя, выполняющего обновление
     * @return обновленная сигнатура
     */
    SignatureDto updateSignature(UUID id, SignatureDto signatureDto, String userId);
    
    /**
     * Удаление сигнатуры (изменение статуса на DELETED)
     * @param id идентификатор сигнатуры
     * @param userId идентификатор пользователя, выполняющего удаление
     */
    void deleteSignature(UUID id, String userId);
    
    /**
     * Получение сигнатур, обновленных после указанной даты
     * @param since дата, после которой искать обновления
     * @return список обновленных сигнатур
     */
    List<SignatureDto> getSignaturesUpdatedSince(LocalDateTime since);
    
    /**
     * Получение списка сигнатур по указанным идентификаторам
     * @param ids список идентификаторов
     * @return список найденных сигнатур
     */
    List<SignatureDto> getSignaturesByIds(List<UUID> ids);
    
    /**
     * Получение истории изменений для сигнатуры
     * @param signatureId идентификатор сигнатуры
     * @return список версий сигнатуры
     */
    List<SignatureHistory> getSignatureHistory(UUID signatureId);
    
    /**
     * Получение аудита изменений для сигнатуры
     * @param signatureId идентификатор сигнатуры
     * @return список записей аудита
     */
    List<SignatureAudit> getSignatureAudit(UUID signatureId);
    
    /**
     * Проверка ЭЦП для сигнатуры
     * @param signatureId идентификатор сигнатуры
     * @return true если ЭЦП верна, false иначе
     */
    boolean verifySignatureDigitalSignature(UUID signatureId);
    
    /**
     * Проверка ЭЦП для всех сигнатур, обновленных с указанной даты
     * @param since дата, с которой проверять сигнатуры
     * @return количество проверенных сигнатур
     */
    int verifyAllSignaturesUpdatedSince(LocalDateTime since);
    
    /**
     * Получение сигнатур по статусу
     * @param status статус (ACTUAL, DELETED, CORRUPTED)
     * @return список сигнатур с указанным статусом
     */
    List<SignatureDto> getSignaturesByStatus(String status);
}
