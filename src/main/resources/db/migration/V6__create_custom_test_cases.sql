CREATE TABLE custom_test_cases (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    problem_id      UUID            NOT NULL,
    input_data      TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now(),
    record_status   VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT pk_custom_test_cases PRIMARY KEY (id),
    CONSTRAINT fk_custom_tc_problem FOREIGN KEY (problem_id)
        REFERENCES problems(id),
    CONSTRAINT chk_custom_tc_record_status
        CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

CREATE INDEX idx_custom_tc_user_problem ON custom_test_cases(user_id, problem_id);