-- Для преобразования bigint в uuid мы используем функцию uuid_generate_v4() для создания случайных UUID
-- или можем создать функцию для детерминированного преобразования

-- Создаем расширение для работы с UUID, если оно еще не создано
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Создаем временную функцию для преобразования bigint в uuid
CREATE OR REPLACE FUNCTION bigint_to_uuid(bigint_val bigint) RETURNS uuid AS $$
BEGIN
    -- Создаем UUID на основе bigint значения, форматируя его как часть UUID
    -- Первые 32 бита UUID
    RETURN ('00000000-0000-0000-0000-' || lpad(to_hex(bigint_val), 12, '0'))::uuid;
END;
$$ LANGUAGE plpgsql;

-- Изменение типа столбца detected_signature_id на UUID в таблице scan_reports
ALTER TABLE IF EXISTS scan_reports
    ADD COLUMN detected_signature_id_uuid uuid;

-- Заполняем новую колонку значениями, преобразованными из старой
UPDATE scan_reports
SET detected_signature_id_uuid = bigint_to_uuid(detected_signature_id)
WHERE detected_signature_id IS NOT NULL;

-- Удаляем старую колонку
ALTER TABLE IF EXISTS scan_reports
    DROP COLUMN detected_signature_id;

-- Переименовываем новую колонку
ALTER TABLE IF EXISTS scan_reports
    RENAME COLUMN detected_signature_id_uuid TO detected_signature_id;

-- Изменение типа столбца id на UUID в таблице signatures
ALTER TABLE IF EXISTS signatures
    ADD COLUMN id_uuid uuid;

-- Заполняем новую колонку значениями, преобразованными из старой
UPDATE signatures
SET id_uuid = bigint_to_uuid(id)
WHERE id IS NOT NULL;

-- Удаляем старую колонку
ALTER TABLE IF EXISTS signatures
    DROP COLUMN id;

-- Переименовываем новую колонку
ALTER TABLE IF EXISTS signatures
    RENAME COLUMN id_uuid TO id;

-- Удаляем временную функцию, она больше не нужна
DROP FUNCTION IF EXISTS bigint_to_uuid(bigint);
