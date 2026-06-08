ALTER TABLE problems
DROP COLUMN description,
ADD COLUMN blocks JSONB;