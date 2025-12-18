CREATE TABLE IF NOT EXISTS user_mng (
    user_seq VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    email VARCHAR(255),
    user_pw_enc VARCHAR(255),
    user_salt VARCHAR(255),
    user_nm VARCHAR(255),
    birth_enc VARCHAR(255),
    tel_num_enc VARCHAR(255),
    dept_nm VARCHAR(255),
    height VARCHAR(50),
    weight VARCHAR(50),
    blood_type VARCHAR(10),
    gender VARCHAR(10),
    reg_dt TIMESTAMP,
    reg_id VARCHAR(255),
    upt_dt TIMESTAMP,
    upt_id VARCHAR(255),
    web_token VARCHAR(500),
    mobile_token VARCHAR(500),
    agreement_yn VARCHAR(1),
    use_yn VARCHAR(1),
    user_profile TEXT
);

CREATE INDEX IF NOT EXISTS idx_user_mng_user_id ON user_mng(user_id);
CREATE INDEX IF NOT EXISTS idx_user_mng_email ON user_mng(email);

