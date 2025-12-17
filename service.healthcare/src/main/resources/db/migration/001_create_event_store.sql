CREATE TABLE event_store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id VARCHAR(36) NOT NULL UNIQUE,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    version INT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    event_data TEXT NOT NULL,
    INDEX idx_aggregate_id (aggregate_id),
    INDEX idx_timestamp (timestamp)
);

