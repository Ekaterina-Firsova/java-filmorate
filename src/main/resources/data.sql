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

INSERT INTO event_type (type)
VALUES
  ('LIKE'),
  ('REVIEW'),
  ('FRIEND')
ON CONFLICT DO NOTHING;

INSERT INTO operation (name)
VALUES
  ('REMOVE'),
  ('ADD'),
  ('UPDATE')
ON CONFLICT DO NOTHING;