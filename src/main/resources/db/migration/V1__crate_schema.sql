CREATE TABLE users (
 user_id UUID PRIMARY KEY,
 username VARCHAR(50) NOT NULL,
 email VARCHAR(100) NOT NULL,
 password_hash VARCHAR(255) NOT NULL,
 full_name VARCHAR(100),
 phone_number VARCHAR(15),
 address TEXT,
 role VARCHAR(20) NOT NULL,

 create_at TIMESTAMP,
 update_create_at TIMESTAMP,
 last_login TIMESTAMP,

 failed_login_attempts INTEGER,
 locked_until TIMESTAMP,

 email_verified BOOLEAN DEFAULT FALSE,
 phone_verified BOOLEAN DEFAULT FALSE,
 mfa_enabled BOOLEAN DEFAULT FALSE,
 mfa_secret VARCHAR(255),

 deleted BOOLEAN DEFAULT FALSE,

 CONSTRAINT uk_users_username UNIQUE (username),
 CONSTRAINT uk_users_email UNIQUE (email)

)

