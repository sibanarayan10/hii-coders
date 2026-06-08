ALTER TABLE problems
DROP COLUMN solution,
ADD COLUMN solution_by_language JSONB;

ALTER TABLE user_problem_engagement
DROP COLUMN solution;


CREATE INDEX idx_problems_solution_by_language
ON problems
USING GIN (solution_by_language);