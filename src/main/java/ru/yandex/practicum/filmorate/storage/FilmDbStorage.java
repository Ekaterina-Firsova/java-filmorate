package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserLikeRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserLike;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final UserLikeRepository userLikeRepository;

    @Override
    public Film getFilm(long id) {
        Film film = filmRepository.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
        Set<Integer> genres = filmRepository.getFilmGenres(film.getId()).stream()
                .map(Long::intValue)
                .collect(Collectors.toSet());
        film.setGenres(genres);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        film = filmRepository.save(film);
        return film;
    }

    @Override
    public Film update(Film newfilm) {
        checkFilm(newfilm);
        filmRepository.getFilm(newfilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        return filmRepository.update(newfilm);
    }

    @Override
    public Collection<Film> getAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film addLike(long filmId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Film film = filmRepository.addLike(filmId, userId);
        putLikesToFilm(film);
        return film;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        Film film = filmRepository.deleteLike(filmId, userId);
        putLikesToFilm(film);
        return film;
    }

    @Override
    public Collection<Film> getRating(Integer count) {
        return filmRepository.getRating(count);
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(Film.MIN_RELEASE_DATE)) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше " + Film.MIN_RELEASE_DATE);
        }
        if (film.getDuration().toSeconds() <= 0) {
            throw new ConditionsNotMetException("Укажите корректную продолжительность фильма");
        }

        if (mpaRepository.getById(film.getMpa()).isEmpty()) {
            throw new ConditionsNotMetException("В справочнике такого MPA " + film.getMpa());
        }

        if (film.getGenres() != null) {
            for (Integer id : film.getGenres()) {
                if (genreRepository.getById(id).isEmpty()) {
                    throw new ConditionsNotMetException("В справочнике такого жанра нет " + id);
                }
            }
        }
    }

    private Film putLikesToFilm(Film film) {
        //положить лайки в структуру данных фильма
        List<UserLike> userLikes = userLikeRepository.getLike(film.getId());
        Set<Long> idUsers = new HashSet<Long>();
        for (UserLike userLike : userLikes) {
            idUsers.add(userLike.getUserId());
        }
        film.setUsersLike(idUsers);
        return film;
    }
}
