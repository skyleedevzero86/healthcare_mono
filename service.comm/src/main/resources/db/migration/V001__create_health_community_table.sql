CREATE TABLE IF NOT EXISTS health_community (
    community_seq SERIAL PRIMARY KEY,
    content TEXT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    heartrate INT,
    temperature NUMERIC,
    bloodpress NUMERIC,
    smoking INT,
    drinking INT,
    exercise INT,
    age INT,
    user_id VARCHAR(255),
    user_nm VARCHAR(255),
    body_age INT
);

CREATE INDEX IF NOT EXISTS idx_health_community_user_id ON health_community(user_id);
CREATE INDEX IF NOT EXISTS idx_health_community_reg_date ON health_community(reg_date);

