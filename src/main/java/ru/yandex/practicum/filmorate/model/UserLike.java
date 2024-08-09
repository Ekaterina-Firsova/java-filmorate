package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserLike {
    private long id;
    private long userId;
    private long filmId;
}
