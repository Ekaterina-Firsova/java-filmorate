package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        checkUser(user);
        user = userRepository.save(user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        userRepository.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userRepository.update(newUser);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        boolean status = false;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        friend.setFriends(userRepository.getALLFriendsIds(friendId));

        if (userId == friendId) {
            throw new NotFoundException("Невозможно добавить в друзья самого себя");
        }
        if (friend.getFriends() != null && friend.getFriends().contains(userId)) {
            status = true;
            userRepository.updateFriendsStatus(friendId, userId, status);
        }
        User newFriend = userRepository.addFriend(user, friendId, status);
        newFriend.setFriends(userRepository.getALLFriendsIds(newFriend.getId()));
        return newFriend;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Collection<User> users = userRepository.getFriends(userId);
        return users;
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        User user = userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        userRepository.getUser(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        if (userRepository.deleteFriend(id, friendId)) {
            userRepository.updateFriendsStatus(friendId, id, false);
            return user;
        }
        return null;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userRepository.getUser(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherId + " не найден"));
        return userRepository.getCommonFriends(userId, otherId);
    }

    @Override
    public User getUser(long userId) {
        return userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private void checkUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Некорректная дата рождения");
        }
    }
}
