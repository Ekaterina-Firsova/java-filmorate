package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User addFriend(long useId, long friendId) {
        return userStorage.addFriend(useId, friendId);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User newUser) {
        return userStorage.createUser(newUser);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public Collection<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public User deleteFriend(long id, long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }
}
