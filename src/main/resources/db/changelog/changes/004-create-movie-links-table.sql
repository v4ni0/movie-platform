--liquibase formatted sql

--changeset ivan:004
CREATE TABLE movie_links (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT    NOT NULL REFERENCES users(id),
    url      VARCHAR(2000) NOT NULL,
    added_at TIMESTAMP NOT NULL,
    rating   INTEGER
);