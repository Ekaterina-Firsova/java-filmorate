package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage extends Storage<Director> {
    List<Director> getDirectorFilmBySorted();
}
