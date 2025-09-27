-- Enable required extensions
create extension if not exists pgcrypto; -- for gen_random_uuid()

-- Schemas table: stores JSON Schema definitions
create table if not exists custom_schema (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    version integer not null,
    json_schema jsonb not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique(name, version)
);

-- Records table: stores data conforming to a schema
create table if not exists custom_record (
    id uuid primary key default gen_random_uuid(),
    schema_id uuid not null references custom_schema(id) on delete cascade,
    data jsonb not null,
    -- simple full-text materialization; cross-version compatible
    data_tsv tsvector generated always as (to_tsvector('simple', coalesce(data::text, ''))) stored,
    created_at timestamptz not null default now()
);

-- Indexes for efficient search
create index if not exists idx_custom_record_schema_id on custom_record(schema_id);
create index if not exists idx_custom_record_data_gin on custom_record using gin (data);
create index if not exists idx_custom_record_data_tsv_gin on custom_record using gin (data_tsv);
