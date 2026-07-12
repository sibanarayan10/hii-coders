ALTER TABLE test_cases
DROP COLUMN time_limit,
DROP COLUMN memory_limit,
DROP COLUMN problem_id,
DROP COLUMN input_data,
DROP COLUMN input_file_key,
DROP COLUMN output_file_key,
DROP COLUMN is_sample,
DROP COLUMN storage_type,

ADD COLUMN problem_id UUID NOT NULL,
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN display_input JSONB,
ADD COLUMN display_output JSONB,
ADD COLUMN input TEXT;