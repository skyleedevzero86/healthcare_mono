CREATE INDEX IF NOT EXISTS idx_community_commu_seq ON healthcare.community(commu_seq);
CREATE INDEX IF NOT EXISTS idx_community_user_seq ON healthcare.community(user_seq);
CREATE INDEX IF NOT EXISTS idx_community_reg_dt ON healthcare.community(reg_dt);
CREATE INDEX IF NOT EXISTS idx_community_title ON healthcare.community(title);
CREATE INDEX IF NOT EXISTS idx_user_mng_user_id ON healthcare.user_mng(user_id);
CREATE INDEX IF NOT EXISTS idx_user_mng_user_seq ON healthcare.user_mng(user_seq);

