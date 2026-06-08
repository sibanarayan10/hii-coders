CREATE TABLE user_dashboard (

id               UUID      NOT NULL UNIQUE,
user_id          UUID      NOT NULL,
problem_id       UUID      NOT NULL,
submission_id    UUID      NOT NULL,
status          VARCHAR(50) NOT NULL,
created_at      TIMESTAMP   NOT NULL DEFAULT now(),

CONSTRAINT     pk_user_dashboard PRIMARY KEY (id)


);


CREATE INDEX idx_user_dashboard_user_id ON user_dashboard(user_id);
CREATE INDEX idx_user_dashboard_problem_id ON user_dashboard(problem_id);

