package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;


/**
 * Controller для обслуживания фильмов
 */
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;
    private final MpaService mpaService;


    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("{id}")
    public NewFilmRequest getFilm(@PathVariable long id) {
        Film resultFilm = filmService.getFilm(id);
        NewFilmRequest newRequest = FilmMapper.mapToFilmRequest(resultFilm);
        for (Genre reqGenre : newRequest.getGenres()) {
            String name = genreService.getById(reqGenre.getId()).getName();
            reqGenre.setName(name);
        }

        Mpa mpa = mpaService.getById(newRequest.getMpa().getId());
        newRequest.getMpa().setName(mpa.getName());
        newRequest.getMpa().setDescription(mpa.getDescription());

        return newRequest;
    }

    @PostMapping
    public NewFilmRequest createFilm(@Valid @RequestBody NewFilmRequest request) {
        log.info("POST createFilm: {}", request);
        Film film = FilmMapper.mapToFilm(request);
        Film resultFilm = filmService.createFilm(film);
        return FilmMapper.mapToFilmRequest(resultFilm);
    }

    @PutMapping
    public NewFilmRequest update(@Valid @RequestBody NewFilmRequest request) {
        log.info("PUT update film: {}", request);
        //приводим запрос к виду нашей таблицы
        Film film = FilmMapper.mapToFilm(request);
        Film resultFilm = filmService.update(film);
        //приводим ответ к виду запроса
        return FilmMapper.mapToFilmRequest(resultFilm);
    }

    /**
     * запрос добавляет лайк определенного пользователя
     *
     * @param id     идентификатор фильма
     * @param userId индетификатор пользователя
     * @return фильм
     */
    @PutMapping("{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("PUT addLike: {}", id);
        return filmService.addLike(id, userId);
    }

    /**
     * запрос удаляет лайк определенного пользователя
     *
     * @param id     идентификатор фильма
     * @param userId индетификатор пользователя
     * @return фильм
     */
    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("DELETE deleteLike: {}", id);
        return filmService.deleteLike(id, userId);
    }

    /**
     * возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, вернет первые 10
     *
     * @param count количество фильмом в выборке
     * @return коллекцию фильмов
     */
    @GetMapping("popular")
    public Collection<Film> getRating(@RequestParam(required = false, defaultValue = "10") String count) {
        return filmService.getRating(Integer.valueOf(count));
    }

}

