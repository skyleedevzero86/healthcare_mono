CREATE TABLE IF NOT EXISTS recommends (
    recommend_id BIGSERIAL PRIMARY KEY,
    community_id INT,
    user_seq VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES health_community(community_seq) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_recommends_community_id ON recommends(community_id);
CREATE INDEX IF NOT EXISTS idx_recommends_user_seq ON recommends(user_seq);

