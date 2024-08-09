package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

@Component("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    @Qualifier("inMemoryUserStorage")
    private final UserStorage userStorage; // Autowired reference to UserStorage

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        film.setId(FilmorateApplication.getNextId(films));
        film.setUsersLike(new HashSet<>());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new IllegalArgumentException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            checkFilm(newFilm);

            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Collection<Film> getAll() {
        if (films.isEmpty()) {
            throw new NotFoundException("Фильмов нет!");
        }
        return films.values();
    }

    // Метод для добавления лайка фильма пользователем
    @Override
    public Film addLike(long filmId, long userId) {
        User user = userStorage.getUser(userId);
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            film.getUsersLike().add(user.getId());
            return film;
        }
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        User user = userStorage.getUser(userId);
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            film.getUsersLike().remove(user.getId());
            return film;
        }
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    @Override
    public Collection<Film> getRating(Integer count) {
        List<Film> sortedFilms = new ArrayList<>(films.values());
        sortedFilms.sort((f1, f2) -> Long.compare(f2.getUsersLike().size(), f1.getUsersLike().size()));

        return sortedFilms.subList(0, Math.min(count, sortedFilms.size()));
    }

    @Override
    public Film getFilm(long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return film;
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(Film.MIN_RELEASE_DATE)) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше " + Film.MIN_RELEASE_DATE);
        }
        if (film.getDuration().toSeconds() <= 0) {
            throw new ConditionsNotMetException("Укажите корректную продолжительность фильма");
        }
    }
}
