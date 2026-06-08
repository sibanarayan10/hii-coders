CREATE TABLE solution (
id              UUID            NOT NULL DEFAULT gen_random_uuid(),
created_at      TIMESTAMP       NOT NULL DEFAULT now(),
updated_at      TIMESTAMP       NOT NULL DEFAULT now(),
record_status   VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
user_id         UUID            NOT NULL,
problem_id      UUID            NOT NULL,
"language"      VARCHAR(20)     NOT NULL DEFAULT 'PYTHON',
solution        TEXT
);

CREATE index idx_solution_user_id on solution(user_id);
CREATE index idx_solution_problem_id on solution(problem_id);
CREATE index idx_solution_language on solution("language");
