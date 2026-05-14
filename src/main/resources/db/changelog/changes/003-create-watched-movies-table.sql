--liquibase formatted sql

--changeset ivan:003
CREATE TABLE watched_movies (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT        NOT NULL REFERENCES users(id),
    movie_id    INTEGER       NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    poster_path VARCHAR(255),
    watched_at  TIMESTAMP     NOT NULL,
    rating      INTEGER,
    notes       VARCHAR(2000),
    CONSTRAINT uq_watched_movies UNIQUE (user_id, movie_id)
);