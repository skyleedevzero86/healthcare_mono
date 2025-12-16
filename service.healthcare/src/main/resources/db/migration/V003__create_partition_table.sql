DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = 'health_data' AND n.nspname = 'healthcare'
        AND c.relkind = 'p'
    ) THEN
        CREATE TABLE IF NOT EXISTS healthcare.health_data (
            user_seq INTEGER NOT NULL,
            heartrate_min INTEGER,
            heartrate_max INTEGER,
            heartrate_avg INTEGER,
            temperature_min NUMERIC,
            temperature_max NUMERIC,
            temperature_avg NUMERIC,
            spo2_min INTEGER,
            spo2_max INTEGER,
            spo2_avg INTEGER,
            step INTEGER,
            stress_min INTEGER,
            stress_max INTEGER,
            stress_avg INTEGER,
            bloodpress_min INTEGER,
            bloodpress_max INTEGER,
            bloodpress_avg INTEGER,
            repiratory_min INTEGER,
            repiratory_max INTEGER,
            repiratory_avg INTEGER,
            sleep INTEGER,
            time VARCHAR(255),
            receive_time DATE NOT NULL,
            "YEAR" VARCHAR(4),
            "MONTH" VARCHAR(2),
            "DAY" VARCHAR(2),
            "HOUR" VARCHAR(2),
            "MINUTE" VARCHAR(2),
            week_cnt INTEGER,
            CONSTRAINT health_data_pkey PRIMARY KEY (user_seq, receive_time)
        ) PARTITION BY RANGE (receive_time);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS healthcare.health_data_2024 PARTITION OF healthcare.health_data
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE IF NOT EXISTS healthcare.health_data_2025 PARTITION OF healthcare.health_data
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE IF NOT EXISTS healthcare.health_data_2026 PARTITION OF healthcare.health_data
FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

CREATE INDEX IF NOT EXISTS idx_health_data_2024_user_date ON healthcare.health_data_2024(user_seq, receive_time);
CREATE INDEX IF NOT EXISTS idx_health_data_2025_user_date ON healthcare.health_data_2025(user_seq, receive_time);
CREATE INDEX IF NOT EXISTS idx_health_data_2026_user_date ON healthcare.health_data_2026(user_seq, receive_time);

