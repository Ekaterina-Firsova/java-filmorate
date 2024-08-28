CREATE TABLE IF NOT EXISTS mpa_rating
(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre
(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film
(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(200),
  release_date DATE NOT NULL,
  duration INTEGER NOT NULL CHECK (duration > 0),
  mpa_rating_id BIGINT,
  FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
  film_id BIGINT,
  genre_id BIGINT,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES film (id) ON DELETE CASCADE,
  FOREIGN KEY (genre_id) REFERENCES genre (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "user"
(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  login VARCHAR(100) NOT NULL UNIQUE,
  name VARCHAR,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_like
(
  film_id BIGINT,
  user_id BIGINT,
  PRIMARY KEY (film_id, user_id),
  FOREIGN KEY (film_id) REFERENCES film (id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship
(
  id BIGINT,
  friend_id BIGINT,
  status BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id, friend_id),
  FOREIGN KEY (id) REFERENCES "user" (id) ON DELETE CASCADE,
  FOREIGN KEY (friend_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_film_name ON film (name);