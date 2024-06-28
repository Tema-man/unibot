CREATE EXTENSION IF NOT EXISTS pgcrypto;

------------------- TABLES ----------------------

CREATE TABLE IF NOT EXISTS chats
(
    id     VARCHAR(128) NOT NULL,
    name   VARCHAR(400),
    active BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT chats_pkey
        PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       VARCHAR(128) NOT NULL,
    name     VARCHAR(1000),
    username VARCHAR(1000),
    role     VARCHAR(100) NOT NULL DEFAULT 'user',
    CONSTRAINT users_pkey
        PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS messages
(
    chat_id VARCHAR(128),
    user_id VARCHAR(128),
    message TEXT,
    id      BIGSERIAL NOT NULL,
    CONSTRAINT chat_log_pkey
        PRIMARY KEY (id),
    CONSTRAINT chat_log_chat_id_fkey
        FOREIGN KEY (chat_id) REFERENCES chats,
    CONSTRAINT user_id
        FOREIGN KEY (user_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS users2chats
(
    chat_id VARCHAR(128) NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    active  BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT users2chats_chat_id_user_id_pk
        PRIMARY KEY (chat_id, user_id),
    CONSTRAINT users2chats_chat_id_fkey
        FOREIGN KEY (chat_id) REFERENCES chats,
    CONSTRAINT users2chats_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users
);

CREATE INDEX IF NOT EXISTS users2chats_chat_id_idx
    ON users2chats (chat_id);

CREATE INDEX IF NOT EXISTS users2chats_user_id_idx
    ON users2chats (user_id);
