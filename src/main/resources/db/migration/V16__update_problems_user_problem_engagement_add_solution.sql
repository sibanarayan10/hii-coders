ALTER TABLE problems
ADD COLUMN solution TEXT NOT NULL DEFAULT 'class Solution {}';

ALTER TABLE user_problem_engagement
ADD COLUMN solution TEXT;

