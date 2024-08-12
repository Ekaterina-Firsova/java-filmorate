package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage extends ReadOnlyStorage<Genre> {

  Collection<Genre> getGenresForFilm(final Long filmId);

  Integer countExistedIds(Set<Long> genreIds);
}
