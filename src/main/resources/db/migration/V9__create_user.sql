CREATE TABLE users (
id              UUID            NOT NULL DEFAULT gen_random_uuid(),
created_at      TIMESTAMP       NOT NULL DEFAULT now(),
updated_at      TIMESTAMP       NOT NULL DEFAULT now(),
email           VARCHAR(30)     NOT NULL,
name            VARCHAR(50)     NOT NULL,
phone_number    INTEGER         NOT NULL,
role            VARCHAR(60)     NOT NULL DEFAULT 'USER',
password        TEXT            NOT NUll
);

CREATE INDEX idx_users_email ON users(email);

