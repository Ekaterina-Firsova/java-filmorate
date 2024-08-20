package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {




    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Collection<Director> findAll() {
        return null;
    }

    @Override
    public boolean isExist(Long id) {
        return false;
    }

    @Override
    public Director save(Director director) {
        return null;
    }

    @Override
    public Director update(Director director) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Director> getDirectorFilmBySorted() {
        return null;
    }
}
