package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (id_genre, id_film) " +
            "VALUES (?, ?)";

    public Film save(Film film) {
        long id = insert(
                "INSERT INTO films(NAME, DESCRIPTION, RELEASE_DATE, DURATION, ID_MPA) " +
                        "VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()
        );

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> insert(
                    INSERT_GENRE_QUERY, genre, id));
        }
        film.setId(id);
        return film;
    }

    public Optional<Film> getFilm(long id) {
        return findOne("SELECT * FROM films WHERE ID =?", id);
    }

    public Film update(Film newFilm) {
        update(
                "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, ID_MPA = ? WHERE id = ?",
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration(),
                newFilm.getReleaseDate(),
                newFilm.getMpa(),
                newFilm.getId()
        );
        delete("DELETE FROM film_genres WHERE id_film = ?", newFilm.getId());
        if (newFilm.getGenres() != null) {
            newFilm.getGenres().forEach(genre -> insert(
                    INSERT_GENRE_QUERY, genre, newFilm.getId()));
        }
        return newFilm;
    }

    public Set<Long> getFilmGenres(long id) {
        return new HashSet<>(findManyIds("SELECT id_genre FROM film_genres WHERE id_film = ? ORDER BY id_genre", id));
    }

    public List<Film> findAll() {
        List<Film> films = findMany("SELECT * FROM films");
        for (Film film : films) {
            Set<Integer> genres = getFilmGenres(film.getId()).stream()
                    .map(Long::intValue)
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }
        return films;
    }

    public Collection<Film> getRating(Integer count) {
        List<Film> films = findMany(
                "SELECT f.*, " +
                        " COUNT(ul.ID) AS like_count" +
                        " FROM FILMS f" +
                        " JOIN USER_LIKES ul ON f.ID = ul.ID_FILM" +
                        " GROUP BY f.ID, f.NAME " +
                        " ORDER BY like_count DESC" +
                        " LIMIT ?", count);
        return films;
    }

    public Film addLike(long filmId, long userId) {
        //добавить запись в USER_LIKES
        Film film = getFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден " + filmId));
        insert("INSERT INTO user_likes (ID_USER, ID_FILM) VALUES (?, ?)", userId, filmId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Optional<Film> film = getFilm(filmId);
        if (film.isPresent()) {
            delete("DELETE FROM user_likes WHERE id_film = ? AND id_user = ?", filmId, userId);
        }
        return film.orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }
}


