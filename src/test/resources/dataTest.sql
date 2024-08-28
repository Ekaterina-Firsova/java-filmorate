MERGE INTO mpa_rating (name)
KEY(name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

MERGE INTO genre (name)
KEY(name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

MERGE INTO "user" (email, login, name, birthday)
KEY(email)
VALUES
    ('user1@gmail.com', 'user1', 'Jay Mueller', '1989-02-27'),
    ('user2@yandex.ru', 'user2', 'Fernando Harber', '1974-09-22'),
    ('user3@aol.com', 'user3', 'Madeline Blick', '1994-09-14'),
    ('user4@yahoo.com', 'user4', 'Alison Leffler', '2003-04-17');

MERGE INTO film (name, description, release_date, duration, mpa_rating_id)
KEY(name)
VALUES ('The Time Traveler', 'A thrilling adventure of a scientist who travels through time.', '2023-05-15', 120, 1);
MERGE INTO film (name, description, release_date, duration, mpa_rating_id)
KEY(name)
VALUES ('Galactic Odyssey', 'An epic space adventure involving interstellar travel and alien civilizations.', '2022-11-07', 135, 2);
MERGE INTO film (name, description, release_date, duration, mpa_rating_id)
KEY(name)
VALUES ('Mystery of the Lost City', 'A detective story set in a forgotten city full of secrets.', '2024-02-20', 105, 3);
MERGE INTO film (name, description, release_date, duration, mpa_rating_id)
KEY(name)
VALUES ('Rise of the AI', 'A futuristic thriller about the emergence of artificial intelligence and its impact on humanity.', '2024-01-15', 110, 4);

MERGE INTO film_genre (film_id, genre_id)
KEY(film_id, genre_id)
VALUES
    (1, 1), -- 'The Time Traveler' with genre 'Comedy'
    (1, 4), -- 'The Time Traveler' with genre 'Thriller'
    (2, 2), -- 'Galactic Odyssey' with genre 'Drama'
    (2, 4), -- 'Galactic Odyssey' with genre 'Thriller'
    (3, 3), -- 'Mystery of the Lost City' with genre 'Animation'
    (3, 5), -- 'Mystery of the Lost City' with genre 'Documentary'
    (4, 6); -- 'Rise of the AI' with genre 'Action'

MERGE INTO user_like (film_id, user_id)
KEY(film_id, user_id)
VALUES
    (2, 1),
    (2, 3),
    (2, 4),
    (3, 2),
    (4, 1),
    (4, 2),
    (4, 3),
    (4, 4);