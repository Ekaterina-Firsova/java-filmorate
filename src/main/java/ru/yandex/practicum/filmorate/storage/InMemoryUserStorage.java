package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        checkUser(user);
        user.setId(FilmorateApplication.getNextId(users));
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            checkUser(newUser);
            User oldUser = users.get(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setFriends(newUser.getFriends());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    private void checkUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Некорректная дата рождения");
        }
    }

    public User addFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        if (user.getId().equals(friend.getId())) {
            throw new NotFoundException("Невозможно добавить в друзья самого себя");
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return user;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        User user = getUser(userId);
        Set<Long> friendsIds = user.getFriends();
        if (friendsIds != null) {
            return users.entrySet().stream()
                    .filter(entry -> friendsIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user.getFriends() == null || friend.getFriends() == null) {
            return user;
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        User user = users.get(userId);
        User otherUser = users.get(otherId);
        if (user.getFriends() == null || otherUser.getFriends() == null) {
            return Collections.emptySet();
        }
        Set<Long> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());

        return users.entrySet().stream()
                .filter(entry -> commonFriendsIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public User getUser(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

}
