CREATE TABLE user_problem_engagement (
    id                  UUID            NOT NULL,
    problem_id          UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    is_saved            Boolean         DEFAULT FALSE,
    is_liked            Boolean         DEFAULT FALSE,
    is_favorite         Boolean         DEFAULT FALSE,
    solve_status        VARCHAR(20)     NOT NULL DEFAULT 'UNSOLVED',
    created_at          TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT now(),
    record_status       VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT pk_user_problem_engagement PRIMARY KEY(id),

    CONSTRAINT fk_user_problem_engagement FOREIGN KEY(problem_id)
        REFERENCES problems(id),

    CONSTRAINT uq_user_problem UNIQUE (user_id, problem_id),

    CONSTRAINT chk_solve_status
        CHECK (solve_status IN ('UNSOLVED', 'ATTEMPTED', 'SOLVED')),

    CONSTRAINT chk_record_status
        CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))

);

CREATE INDEX idx_engagement_user_id ON user_problem_engagement(user_id);
CREATE INDEX idx_engagement_problem_id ON user_problem_engagement(problem_id);