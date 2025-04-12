# Signature Detection Application

## Описание проекта

Это Spring Boot приложение для хранения и управления сигнатурами файлов, типами файлов, движками сканирования и отчетами сканирования. Оно демонстрирует различные типы связей между сущностями в JPA и включает в себя функциональность аудита, проверки и верификации сигнатур с помощью ЭЦП.

## Сущности

- **FileType**: Типы файлов (например, PE, Java class, .NET, PowerShell и т.д.)
- **ScanEngine**: Движки сканирования файлов
- **ScanReport**: Отчеты о результатах сканирования
- **Signature**: Сигнатуры для обнаружения вредоносного кода в файлах
- **SignatureAudit**: Аудит изменений сигнатур
- **SignatureHistory**: История изменений сигнатур

## Типы связей в проекте

- **OneToOne**: между ScanReport и Signature
- **OneToMany/ManyToOne**: между FileType и Signature, ScanEngine и ScanReport
- **ManyToMany**: между FileType и ScanEngine

## Требования

- Java 17+
- Maven
- PostgreSQL

## Основные функциональности

- Управление сигнатурами и типами файлов
- Интеграция с движками сканирования
- Генерация и проверка отчётов сканирования
- Поддержка цифровой подписи (ЭЦП) для верификации сигнатур
- Аудит и хранение истории изменений сигнатур
- Планировщик для автоматической верификации сигнатур
- Безопасный доступ с использованием Spring Security

## Настройка базы данных

1. Создайте базу данных в PostgreSQL:

```sql
CREATE DATABASE signature_db;
```

2. Настройте доступ к базе данных в файле `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/signature_db
spring.datasource.username=ваш_пользователь
spring.datasource.password=ваш_пароль
```

## Запуск приложения

1. Клонируйте репозиторий
2. Настройте базу данных как описано выше
3. Запустите приложение:

```bash
./mvnw spring-boot:run
```

Или с использованием Maven:

```bash
mvn spring-boot:run
```

## API Endpoints

### FileType
- `GET /api/file-types` - Получить все типы файлов
- `GET /api/file-types/{id}` - Получить тип файла по ID
- `POST /api/file-types` - Создать новый тип файла
- `PUT /api/file-types/{id}` - Обновить тип файла
- `DELETE /api/file-types/{id}` - Удалить тип файла

### ScanEngine
- `GET /api/scan-engines` - Получить все движки сканирования
- `GET /api/scan-engines/{id}` - Получить движок по ID
- `POST /api/scan-engines` - Создать новый движок сканирования
- `PUT /api/scan-engines/{id}` - Обновить движок сканирования
- `DELETE /api/scan-engines/{id}` - Удалить движок сканирования

### ScanReport
- `GET /api/scan-reports` - Получить все отчеты
- `GET /api/scan-reports/{id}` - Получить отчет по ID
- `POST /api/scan-reports` - Создать новый отчет
- `PUT /api/scan-reports/{id}` - Обновить отчет
- `DELETE /api/scan-reports/{id}` - Удалить отчет

### Signature
- `GET /api/signatures` - Получить все сигнатуры
- `GET /api/signatures/{id}` - Получить сигнатуру по ID
- `POST /api/signatures` - Создать новую сигнатуру
- `PUT /api/signatures/{id}` - Обновить сигнатуру
- `DELETE /api/signatures/{id}` - Удалить сигнатуру

## Безопасность

Приложение использует Spring Security для защиты API-эндпоинтов. Аутентификация выполняется через фильтр `UserRoleAuthenticationFilter`.

## Технологии

- Spring Boot 3.1.5
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- BouncyCastle (для работы с цифровыми подписями)
