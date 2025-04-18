package com.example.signatureapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data // Геттеры, сеттеры, toString, equals, hashCode
@Builder // Паттерн Builder
@NoArgsConstructor // Конструктор без аргументов
@AllArgsConstructor // Конструктор со всеми аргументами
public class SignatureScanResult {

    private UUID signatureId;       // ID найденной сигнатуры
    private String threatName;      // Имя угрозы
    private long offsetFromStart;   // Смещение начала совпадения от начала файла
    private long offsetFromEnd;     // Смещение конца совпадения от начала файла (включительно)
    private boolean matched;        // Флаг, подтверждающий совпадение (можно оставить true по умолчанию)

}
