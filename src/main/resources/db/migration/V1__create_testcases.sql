CREATE TABLE test_cases (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    problem_id          UUID            NOT NULL,
    input_data          TEXT,
    expected_output     TEXT,
    input_file_key      VARCHAR(1000),
    output_file_key     VARCHAR(1000),
    is_sample           BOOLEAN         NOT NULL DEFAULT FALSE,
    sequence_order      INTEGER         NOT NULL,
    storage_type        VARCHAR(10)     NOT NULL DEFAULT 'INLINE',
    record_status       VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT pk_test_cases PRIMARY KEY (id),
    CONSTRAINT fk_test_cases_problem FOREIGN KEY (problem_id)
        REFERENCES problems(id),
    CONSTRAINT chk_test_cases_storage_type
        CHECK (storage_type IN ('INLINE', 'CLOUD')),
    CONSTRAINT chk_test_cases_record_status
        CHECK (record_status IN ('ACTIVE', 'ARCHIVED', 'DELETED'))
);

CREATE INDEX idx_test_cases_problem_id ON test_cases(problem_id);