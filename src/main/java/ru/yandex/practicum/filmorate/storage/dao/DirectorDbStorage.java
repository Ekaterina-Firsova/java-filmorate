package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class DirectorDbStorage extends BaseRepository<Director> implements Storage<Director> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM director GROUP BY id";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM director WHERE id = ?";
    private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM director WHERE id = ?)";
//    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO director (id, name) VALUES(?, ?)";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO director (name) VALUES(?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE director SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM director WHERE id = ?";

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        return findOne(SELECT_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Director> findAll() {
        return findMany(SELECT_ALL_QUERY);
    }

    @Override
    public boolean isExist(Long id) {
        return checkExistence(EXIST_QUERY, id);
    }

    @Override
    public Director save(Director director) {
        System.out.println(director);
//        insert(INSERT_DIRECTOR_QUERY, director.getId(), director.getName());
        Long id = insert(INSERT_DIRECTOR_QUERY, director.getName());
        director.setId(id);
        System.out.println(director);
        return director;
    }

    @Override
    public Director update(Director director) {
        findById(director.getId())
                .orElseThrow(() -> new NotFoundException("Genre by ID = " + director.getId() + " not found"));;
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_DIRECTOR_QUERY, id);

    }
}