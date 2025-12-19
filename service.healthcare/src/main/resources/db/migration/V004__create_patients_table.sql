CREATE TABLE IF NOT EXISTS patients (
    patient_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),
    address TEXT,
    region_id VARCHAR(100),
    shard_key INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_patients_region_id ON patients(region_id);
CREATE INDEX IF NOT EXISTS idx_patients_shard_key ON patients(shard_key);
CREATE INDEX IF NOT EXISTS idx_patients_email ON patients(email);
CREATE INDEX IF NOT EXISTS idx_patients_created_at ON patients(created_at);

