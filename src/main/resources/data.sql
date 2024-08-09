INSERT INTO PUBLIC.MPA (ID, NAME, DESCRIPTION)
SELECT 1, 'G', 'у фильма нет возрастных ограничений'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.MPA)
UNION ALL
SELECT 2, 'PG', 'детям рекомендуется смотреть фильм с родителями'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.MPA)
UNION ALL
SELECT 3, 'PG-13', 'детям до 13 лет просмотр не желателен'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.MPA)
UNION ALL
SELECT 4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.MPA)
UNION ALL
SELECT 5, 'NC-17', 'лицам до 18 лет просмотр запрещён'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.MPA);

INSERT INTO PUBLIC.GENRES (ID, NAME)
SELECT 1, 'Комедия'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES)
UNION ALL
SELECT 2, 'Драма'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES)
UNION ALL
SELECT 3, 'Мультфильм'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES)
UNION ALL
SELECT 4, 'Триллер'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES)
UNION ALL
SELECT 5, 'Документальный'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES)
UNION ALL
SELECT 6, 'Боевик'
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.GENRES);

--это нужно для ручной вставки
--INSERT INTO PUBLIC.GENRE
--(ID, NAME) VALUES
--	(1, 'Комедия'),
--	(2, 'Драма'),
--	(3, 'Мультфильм'),
--	(4, 'Триллер'),
--	(5, 'Документальный'),
--	(6, 'Боевик');

--INSERT INTO PUBLIC.RATING
--(ID , NAME, DESCRIPTION) VALUES
--	(1, 'G', 'у фильма нет возрастных ограничений'),
--	(2, 'PG', 'детям рекомендуется смотреть фильм с родителями'),
--	(3, 'PG-13', 'детям до 13 лет просмотр не желателен'),
--	(4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
--	(5, 'NC-17', 'лицам до 18 лет просмотр запрещён');

