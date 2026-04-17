CREATE TABLE test_cases (
id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
problem_id          UUID,
input_data          TEXT,
expected_output     TEXT,
input_file_key      VARCHAR(1000),
output_file_key     VARCHAR(1000),
is_sample           BOOLEAN         NOT NULL DEFAULT FALSE,
sequence_order      INTEGER,
storage_type        VARCHAR(20)     NOT NULL DEFAULT 'INLINE',
record_status       VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
created_at          TIMESTAMP       NOT NULL DEFAULT now(),
updated_at          TIMESTAMP       NOT NULL DEFAULT now(),

CONSTRAINTS pk_test_cases_id PRIMARY KEY (id)

CONSTRAINT fk_test_cases_problem FOREIGN KEY (problem_id)
    REFERENCES problems(id),

CONSTRAINT chk_storage_type
    CHECK (storage_type IN ('INLINE', 'CLOUD')),

CONSTRAINT chk_record_status
    CHECK (record_status IN ('ACTIVE', 'ARCHIVE', 'DELETED'))
)