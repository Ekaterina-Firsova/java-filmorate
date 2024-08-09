package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserLikeMapper implements RowMapper<UserLike> {

    @Override
    public UserLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserLike userLike = new UserLike();
        userLike.setUserId(rs.getLong("id_user"));
        userLike.setId(rs.getLong("id"));
        userLike.setFilmId(rs.getLong("id_film"));
        return userLike;
    }
}
