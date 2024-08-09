package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserLike;

import java.util.List;

@Repository
public class UserLikeRepository extends BaseRepository<UserLike> {


    public UserLikeRepository(JdbcTemplate jdbc, RowMapper<UserLike> mapper) {
        super(jdbc, mapper);
    }

    public List<UserLike> getLike(long filmId) {
        return findMany("SELECT * FROM user_likes WHERE id_film = ? ORDER BY id_user", filmId);
    }

}
