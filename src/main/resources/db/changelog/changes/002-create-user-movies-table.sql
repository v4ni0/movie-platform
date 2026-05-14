--liquibase formatted sql

--changeset ivan:002
CREATE TABLE user_movies (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES users(id),
    movie_id     INTEGER       NOT NULL,
    title        VARCHAR(255)  NOT NULL,
    poster_path  VARCHAR(255),
    release_date VARCHAR(50),
    vote_average DOUBLE PRECISION,
    is_favourite  BOOLEAN       NOT NULL DEFAULT FALSE,
    is_on_watchlist BOOLEAN     NOT NULL DEFAULT FALSE,
    added_at     TIMESTAMP     NOT NULL,
    CONSTRAINT uq_user_movies UNIQUE (user_id, movie_id)
);