DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = 'health_data' AND n.nspname = 'healthcare'
        AND c.relkind = 'p'
    ) THEN
        CREATE TABLE IF NOT EXISTS healthcare.health_data_2024 PARTITION OF healthcare.health_data
        FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

        CREATE TABLE IF NOT EXISTS healthcare.health_data_2025 PARTITION OF healthcare.health_data
        FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

        CREATE TABLE IF NOT EXISTS healthcare.health_data_2026 PARTITION OF healthcare.health_data
        FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

        CREATE INDEX IF NOT EXISTS idx_health_data_2024_user_date ON healthcare.health_data_2024(user_seq, receive_time);
        CREATE INDEX IF NOT EXISTS idx_health_data_2025_user_date ON healthcare.health_data_2025(user_seq, receive_time);
        CREATE INDEX IF NOT EXISTS idx_health_data_2026_user_date ON healthcare.health_data_2026(user_seq, receive_time);
    END IF;
END $$;

