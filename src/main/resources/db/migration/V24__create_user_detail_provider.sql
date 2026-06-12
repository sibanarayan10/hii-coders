CREATE TABLE user_detail_provider (
    user_id               UUID            NOT NULL,
    user_detail_provider  VARCHAR(50)     NOT NULL,

    CONSTRAINT pk_user_detail_provider
        PRIMARY KEY (user_id, user_detail_provider),

    CONSTRAINT fk_user_detail_provider_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);