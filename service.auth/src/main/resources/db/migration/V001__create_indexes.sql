CREATE INDEX IF NOT EXISTS idx_user_mng_user_id ON healthcare.user_mng(user_id);
CREATE INDEX IF NOT EXISTS idx_user_mng_user_seq ON healthcare.user_mng(user_seq);
CREATE INDEX IF NOT EXISTS idx_user_mng_email ON healthcare.user_mng(email);
CREATE INDEX IF NOT EXISTS idx_user_auth_info_user_seq ON healthcare.user_auth_info(user_seq);
CREATE INDEX IF NOT EXISTS idx_user_auth_info_role ON healthcare.user_auth_info(user_role_fk);
CREATE INDEX IF NOT EXISTS idx_user_auth_info_user_role ON healthcare.user_auth_info(user_seq, user_role_fk);

