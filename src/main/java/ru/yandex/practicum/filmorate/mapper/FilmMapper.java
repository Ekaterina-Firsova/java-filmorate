package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setDuration(request.getDuration());
        film.setReleaseDate(request.getReleaseDate());
        film.setMpa(request.getMpa().getId());

        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet()));
        }
        return film;
    }

    public static NewFilmRequest mapToFilmRequest(Film film) {

        NewFilmRequest request = new NewFilmRequest();
        request.setId(film.getId());
        request.setName(film.getName());
        request.setDescription(film.getDescription());
        request.setDuration(film.getDuration());
        request.setReleaseDate(film.getReleaseDate());

        Mpa mpa = new Mpa();
        mpa.setId(film.getMpa());
        request.setMpa(mpa);

        if (film.getGenres() != null) {
            Set<Genre> genres = new TreeSet<>((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
            for (Integer id : film.getGenres()) {
                Genre genre = new Genre();
                genre.setId(id);
                genres.add(genre);
            }
            request.setGenres(genres);
        }
        return request;
    }

}
