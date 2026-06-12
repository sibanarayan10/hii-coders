CREATE TABLE pending_links (
    id          UUID            NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    token       VARCHAR(255)    NOT NULL,
    expires_at  TIMESTAMP       NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_pending_links PRIMARY KEY (id),
    CONSTRAINT uq_pending_links_token UNIQUE (token),
    CONSTRAINT uq_pending_links_email UNIQUE (email),

    CONSTRAINT fk_pending_links_email
        FOREIGN KEY (email)
        REFERENCES users(email)
        ON DELETE CASCADE
);

CREATE INDEX idx_pending_links_token ON pending_links(token);
CREATE INDEX idx_pending_links_email ON pending_links(email);
CREATE INDEX idx_pending_links_expires_at ON pending_links(expires_at);