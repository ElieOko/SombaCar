#!/usr/bin/env python3
"""Generate scripts/supabase-import.sql from backup + Flyway migrations."""

from __future__ import annotations

import os
import re
from collections import defaultdict, deque
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BACKUP = Path("/home/elieoko/Documents/db_cluster-24-08-2026@09-40-23.backup")
MIGRATIONS_DIR = ROOT / "src/main/resources/db/migration"
OUT = ROOT / "scripts/supabase-import.sql"


def parse_copy_blocks(section: str) -> dict[str, tuple[list[str], list[list[str | None]]]]:
    blocks: dict[str, tuple[list[str], list[list[str | None]]]] = {}
    lines = section.split("\n")
    i = 0
    while i < len(lines):
        line = lines[i]
        if not line.startswith("COPY public."):
            i += 1
            continue

        match = re.match(r"COPY public\.(\w+) \((.*)\) FROM stdin;", line)
        if not match:
            i += 1
            continue

        table, cols = match.group(1), [c.strip() for c in match.group(2).split(",")]
        i += 1
        data_lines: list[str] = []
        while i < len(lines) and lines[i].strip() != "\\.":
            data_lines.append(lines[i])
            i += 1

        rows: list[list[str | None]] = []
        for data_line in data_lines:
            if not data_line.strip():
                continue
            values = data_line.split("\t")
            rows.append([
                None if value == "\\N" else value.replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\")
                for value in values
            ])

        blocks[table] = (cols, rows)
        i += 1

    return blocks


def sql_literal(value: str | None) -> str:
    if value is None:
        return "NULL"
    if value in {"t", "true"}:
        return "TRUE"
    if value in {"f", "false"}:
        return "FALSE"
    if re.fullmatch(r"-?\d+", value):
        return value
    if re.fullmatch(r"-?\d+\.\d+", value):
        return value
    return "'" + value.replace("'", "''") + "'"


def build_insert(table: str, cols: list[str], rows: list[list[str | None]]) -> str:
    if not rows:
        return f"-- {table}: aucune donnée\n"
    col_list = ", ".join(cols)
    lines = [f"-- Données: {table} ({len(rows)} lignes)"]
    for row in rows:
        values = ", ".join(sql_literal(value) for value in row)
        lines.append(f"INSERT INTO {table} ({col_list}) VALUES ({values});")
    lines.append("")
    return "\n".join(lines)


def topo_sort(tables: list[str], deps: dict[str, set[str]]) -> list[str]:
    local_deps = {table: set(deps.get(table, set())) for table in tables}
    result: list[str] = []
    ready = deque(sorted(table for table in tables if not local_deps[table]))

    while ready:
        table = ready.popleft()
        result.append(table)
        for other in tables:
            if table in local_deps[other]:
                local_deps[other].remove(table)
                if not local_deps[other] and other not in result and other not in ready:
                    ready.append(other)

    result.extend(sorted(table for table in tables if table not in result))
    return result


def main() -> None:
    content = BACKUP.read_text(errors="replace")
    start = content.find("\\connect postgres\n")
    end = content.find("-- PostgreSQL database cluster dump complete", start)
    section = content[start:end]

    copy_blocks = parse_copy_blocks(section)
    data_tables = [table for table, (_, rows) in copy_blocks.items() if table != "flyway_schema_history" and rows]

    foreign_keys = re.findall(
        r"ALTER TABLE ONLY public\.(\w+)\s+ADD CONSTRAINT \w+ FOREIGN KEY \([^)]+\) REFERENCES public\.(\w+)",
        section,
    )
    deps: dict[str, set[str]] = defaultdict(set)
    for child, parent in foreign_keys:
        if child in data_tables and parent in data_tables and child != parent:
            deps[child].add(parent)

    insert_order = topo_sort(data_tables, deps)

    migration_files = sorted(
        MIGRATIONS_DIR.glob("V*.sql"),
        key=lambda path: int(re.search(r"V(\d+)__", path.name).group(1)),
    )

    parts: list[str] = [
        """-- =============================================================================
-- Import CarTransit / VehnixAuto vers un nouveau projet Supabase
-- Généré depuis: db_cluster-24-08-2026@09-40-23.backup + migrations V1-V18
--
-- Usage:
--   1. Créer un nouveau projet Supabase
--   2. Ouvrir SQL Editor
--   3. Exécuter ce script en entier
--
-- Contenu:
--   - Structure complète (39 tables, migrations Flyway V1 -> V18)
--   - Données extraites du backup
--   - Historique Flyway synchronisé
-- =============================================================================

BEGIN;

SET session_replication_role = replica;

DO $$
DECLARE r RECORD;
BEGIN
  FOR r IN (
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'public'
      AND tablename NOT IN ('schema_migrations')
  ) LOOP
    EXECUTE format('DROP TABLE IF EXISTS public.%I CASCADE', r.tablename);
  END LOOP;
END $$;

-- =============================================================================
-- STRUCTURE (Flyway V1 -> V18)
-- =============================================================================
""",
    ]

    for migration_file in migration_files:
        parts.append(f"-- {migration_file.name}\n{migration_file.read_text().strip()}\n")

    parts.append(
        """
-- Table d'historique Flyway (créée automatiquement par Flyway en production)
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank   INT PRIMARY KEY,
    version          VARCHAR(50),
    description      VARCHAR(200) NOT NULL,
    type             VARCHAR(20) NOT NULL,
    script           VARCHAR(1000) NOT NULL,
    checksum         INT,
    installed_by     VARCHAR(100) NOT NULL,
    installed_on     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time   INT NOT NULL,
    success          BOOLEAN NOT NULL
);

-- =============================================================================
-- DONNÉES
-- =============================================================================

-- Supprime les seeds Flyway (documents, subscription_plans, etc.) avant import
DO $$
DECLARE
  tables_list TEXT;
BEGIN
  SELECT string_agg(format('%I.%I', schemaname, tablename), ', ' ORDER BY tablename)
  INTO tables_list
  FROM pg_tables
  WHERE schemaname = 'public'
    AND tablename NOT IN ('flyway_schema_history', 'schema_migrations');

  IF tables_list IS NOT NULL THEN
    EXECUTE 'TRUNCATE TABLE ' || tables_list || ' RESTART IDENTITY CASCADE';
  END IF;
END $$;

"""
    )

    for table in insert_order:
        cols, rows = copy_blocks[table]
        parts.append(build_insert(table, cols, rows))

    parts.append("-- Historique Flyway (V1-V18)\n")
    flyway_rows = [
        ("1", "create user tables", "V1__create_user_tables.sql", -1170692291),
        ("2", "create car tables", "V2__create_car_tables.sql", 788516455),
        ("3", "create tools tables", "V3__create_tools_tables.sql", 1497510995),
        ("4", "create payment tables", "V4__create_payment_tables.sql", -1295806058),
        ("5", "create notification tables", "V5__create_notification_tables.sql", 142054914),
        ("6", "create message tables", "V6__create_message_tables.sql", -1364471732),
        ("7", "create part images table", "V7__create_part_images_table.sql", 117367315),
        ("8", "create car listing message tables", "V8__create_car_listing_message_tables.sql", 855965419),
        ("9", "create car documents table", "V9__create_car_documents_table.sql", -1421367268),
        ("10", "create moto tables", "V10__create_moto_tables.sql", 116971234),
        ("11", "create glossary tables", "V11__create_glossary_tables.sql", 1432675181),
        ("12", "create sale offers and extend payments", "V12__create_sale_offers_and_extend_payments.sql", -1567098843),
        ("13", "create cart items", "V13__create_cart_items.sql", -616884892),
        ("14", "create listing reports", "V14__create_listing_reports.sql", -1069192051),
        ("15", "create favorites and garages", "V15__create_favorites_and_garages.sql", 118757109),
        ("16", "add geographic coordinates", "V16__add_geographic_coordinates.sql", 311652902),
        ("17", "create subscriptions and mechanics", "V17__create_subscriptions_and_mechanics.sql", 10893865),
        ("18", "add car listing vin and devise", "V18__add_car_listing_vin_and_devise.sql", 1385761439),
    ]
    for rank, (version, description, script, checksum) in enumerate(flyway_rows, start=1):
        parts.append(
            "INSERT INTO flyway_schema_history "
            "(installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) "
            f"VALUES ({rank}, '{version}', '{description}', 'SQL', '{script}', {checksum}, 'supabase-import', NOW(), 0, TRUE);"
        )

    parts.append(
        """
-- =============================================================================
-- Réinitialisation des séquences
-- =============================================================================
"""
    )
    for table in insert_order:
        if copy_blocks[table][1]:
            parts.append(
                f"SELECT setval(pg_get_serial_sequence('{table}', 'id'), "
                f"COALESCE((SELECT MAX(id) FROM {table}), 1), TRUE);"
            )

    parts.append(
        """
SET session_replication_role = DEFAULT;
COMMIT;

-- Import terminé.
"""
    )

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text("\n".join(parts))

    print(f"Generated: {OUT}")
    print(f"Size: {OUT.stat().st_size / 1024:.1f} KB")
    print(f"Tables with data: {len(insert_order)}")
    for table in insert_order:
        print(f"  - {table}: {len(copy_blocks[table][1])} rows")


if __name__ == "__main__":
    main()
