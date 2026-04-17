CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE problems (
id              UUID            NOT NULL DEFAULT gen_random_uuid(),
title           VARCHAR(100)    NOT NULL,
description     TEXT            NOT NULL,
difficulty      VARCHAR(10)     NOT NULL,
categories      TEXT[]          NOT NULL DEFAULT '{}',
created_by      UUID            NOT NULL,
created_at      TIMESTAMP       NOT NULL DEFAULT now(),
updated_at      TIMESTAMP       NOT NULL DEFAULT now(),
record_status   VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',

CONSTRAINT pk_problems PRIMARY KEY (id),

CONSTRAINT chk_problems_difficulty
    CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),

CONSTRAINT chk_problems_record_status
    CHECK (record_status IN ('ACTIVE', 'ARCHIVED', 'DELETED'))
);

CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_created_by ON problems(created_by);
