-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users' table
CREATE TABLE IF NOT EXISTS users
(
    id                 UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username           VARCHAR(255) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    role               VARCHAR(255) DEFAULT 'USER' NOT NULL,
    display_name       VARCHAR(255) NOT NULL,
    bio                TEXT,
    profile_picture_id TEXT,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Follower table (self-referencing many-to-many)
CREATE TABLE IF NOT EXISTS users_followers
(
    follower_id  UUID      NOT NULL,
    following_id UUID      NOT NULL,
    followed_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_following FOREIGN KEY (following_id) REFERENCES users (id) ON DELETE CASCADE
);

-- For fast lookup of followers of user
CREATE INDEX IF NOT EXISTS idx_following_id ON users_followers (following_id);

-- For fast lookup of following of user
CREATE INDEX IF NOT EXISTS idx_follower_id ON users_followers (follower_id);