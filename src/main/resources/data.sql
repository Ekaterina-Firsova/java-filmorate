INSERT INTO mpa_rating (name)
VALUES
  ('G'),
  ('PG'),
  ('PG-13'),
  ('R'),
  ('NC-17')
ON CONFLICT DO NOTHING;

INSERT INTO genre (name)
VALUES
  ('Комедия'),
  ('Драма'),
  ('Мультфильм'),
  ('Триллер'),
  ('Документальный'),
  ('Боевик')
ON CONFLICT DO NOTHING;

INSERT INTO director (name)
VALUES
  ('Uve Boll'),
  ('Spilberg')
ON CONFLICT DO NOTHING;