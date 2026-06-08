ALTER TABLE user_problem_engagement
DROP COLUMN solve_status,
ADD COLUMN solve_status VARCHAR(40) DEFAULT 'TODO',
ADD CONSTRAINT chk_solve_status
CHECK (
    solve_status IN (
        'TODO',
        'ATTEMPTED',
        'SOLVED'
    )
);