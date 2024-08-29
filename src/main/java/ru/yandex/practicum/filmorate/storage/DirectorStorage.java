package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage extends Storage<Director> {

    Optional<Director> findById(final Long id);

    Collection<Director> findAll();
}
