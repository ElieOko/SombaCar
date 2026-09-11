-- =============================================================================
-- Repair Flyway checksums après import Supabase (supabase-import.sql)
-- Exécuter dans Supabase SQL Editor si l'application échoue au démarrage avec:
--   "Migration checksum mismatch"
-- =============================================================================

UPDATE flyway_schema_history SET checksum = -1170692291 WHERE version = '1';
UPDATE flyway_schema_history SET checksum = 788516455    WHERE version = '2';
UPDATE flyway_schema_history SET checksum = 1497510995   WHERE version = '3';
UPDATE flyway_schema_history SET checksum = -1295806058  WHERE version = '4';
UPDATE flyway_schema_history SET checksum = 142054914    WHERE version = '5';
UPDATE flyway_schema_history SET checksum = -1364471732  WHERE version = '6';
UPDATE flyway_schema_history SET checksum = 117367315    WHERE version = '7';
UPDATE flyway_schema_history SET checksum = 855965419    WHERE version = '8';
UPDATE flyway_schema_history SET checksum = -1421367268  WHERE version = '9';
UPDATE flyway_schema_history SET checksum = 116971234    WHERE version = '10';
UPDATE flyway_schema_history SET checksum = 1432675181   WHERE version = '11';
UPDATE flyway_schema_history SET checksum = -1567098843  WHERE version = '12';
UPDATE flyway_schema_history SET checksum = -616884892   WHERE version = '13';
UPDATE flyway_schema_history SET checksum = -1069192051  WHERE version = '14';
UPDATE flyway_schema_history SET checksum = 118757109    WHERE version = '15';
UPDATE flyway_schema_history SET checksum = 311652902    WHERE version = '16';
UPDATE flyway_schema_history SET checksum = 10893865     WHERE version = '17';
UPDATE flyway_schema_history SET checksum = 1385761439   WHERE version = '18';

-- Vérification
SELECT version, description, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;
