package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of {@link DirectorStorage} for managing {@link Director} entities in the database.
 * <p>
 * This class provides methods to perform operations on director records, including saving, updating,
 * retrieving, and deleting directors. It extends {@link BaseRepository} and utilizes Spring's
 * {@link JdbcTemplate} for database interactions.
 *
 * @see DirectorStorage
 * @see BaseRepository
 * @see JdbcTemplate
 */
@Repository
@Slf4j
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM director GROUP BY id";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM director WHERE id = ?";
    private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM director WHERE id = ?)";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO director (name) VALUES(?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE director SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM director WHERE id = ?";
    private static final String SELECT_DIRECTOR_BY_NAME = "SELECT * FROM director WHERE name =?";

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Director> findById(final Long id) {
        return findOne(SELECT_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Director> findAll() {
        return findMany(SELECT_ALL_QUERY);
    }

    @Override
    public boolean isExist(final Long id) {
        return checkExistence(EXIST_QUERY, id);
    }

    @Override
    public Director save(final Director director) {
        if (findOne(SELECT_DIRECTOR_BY_NAME, director.getName()).isPresent()) {
            throw new InvalidDataException(String.format("Director with name %s already exists", director.getName()));
        }
        Long id = insert(INSERT_DIRECTOR_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director update(final Director director) {
        findById(director.getId())
                .orElseThrow(() -> new NotFoundException("Genre by ID = " + director.getId() + " not found"));
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_DIRECTOR_QUERY, id);
    }
}
