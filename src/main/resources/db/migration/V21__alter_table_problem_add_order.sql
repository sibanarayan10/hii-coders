CREATE SEQUENCE IF NOT EXISTS problem_order_seq START 1;

ALTER TABLE problems
ADD COLUMN IF NOT EXISTS order_no INTEGER DEFAULT nextval('problem_order_seq');