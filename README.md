## Ссылка на ER-диаграмму

https://dbdiagram.io/d/filmorate-6694ff719939893daef29ecb

# Описание структуры БД

### films

– содержит информацию о фильмах

- id – уникальный идентификатор
- name – название фильма
- description – описание фильма
- release_date – дата выпуска
- duration – продолжительность в секуднах
- id_mpa – рейтинг МРА

### users

– содержит данные пользователя

- id – уникальный идентификатор
- name – имя пользователя
- email – электронная почта
- login - логин
- birthday – дата рождения

### genres

– справочник жанров

- id – уникальный идентификатор
- name – название жанра

### film_genres

– содержит информацию у какого фильма какой жанр
- id – уникальный идентификатор
- id_genre – id жанра
- id_film – id фильма

### user_likes

– содержит информацию о том какой юзер какой фильм лайкнул
- id – уникальный идентификатор
- id_user – идентификатор пользователя
- id_film – идентификатор фильма

### friends

– содержит информацию кто с кем дружит

- id – уникальный идентификатор
- id_user - идентификатор пользователя к которому записались в друзья
- confirmed – подтверждение совместной дружбы
- id_friend – идентификатор пользователя, который записался в друзья

### mpa

– справочник видов рейтинга МРА

- id - уникальный идентификатор
- name – наименование рейтинга МРА

# Структура БД

```
Table films {
  id integer [primary key]
  name varchar
  description varchar
  release_date date
  duration integer
  id_mpa integer
}

Table users {
  id integer [primary key]
  name varchar
  email varchar
  login varchar
  birthday date
}

Table film_genres {
  id integer [primary key]
  id_genre integer
  id_film integer
}

Table genres {
  id integer [primary key]
  name varchar
}

Table user_likes {
  id integer [primary key]
  id_user integer
  id_film integer
}

Table friends {
  id integer [primary key]
  id_user integer
  confirmed bool
  id_friend integer 
}

Table mpa {
  id integer [primary key]
  name varchar
  description varchar
}

Ref: "films"."id" < "film_genres"."id_film"

Ref: "genres"."id" < "film_genres"."id_genre"

Ref: "films"."id" < "user_likes"."id_film"

Ref: "users"."id" < "user_likes"."id_user"

Ref: "users"."id" < "friends"."id_user"

Ref: "users"."id" < "friends"."id_friend"

Ref: "films"."id_mpa" > "mpa"."id"
```
