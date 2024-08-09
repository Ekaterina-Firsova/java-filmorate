package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage") // Выбор реализации
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film addLike(long filmId, long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(long filmId, long userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getRating(Integer count) {
        return filmStorage.getRating(count);
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }
}
