package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FriendListDb;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao storage;

    private final FriendListDb friendListDb;

    public User create(User user) {
        return storage.create(user);
    }

    public List<User> getAllUsers() {
        return storage.getAll();
    }

    public User getUserBy(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));
    }

    public User update(User user) {
        return storage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDb.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDb.removeFriend(userId, friendId);
    }

    public List<Long> getUserFriends(Long userId) {
        getUserBy(userId);
        return friendListDb.getFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        User user = getUserBy(userId);
        User otherUser = getUserBy(otherUserId);

        return user.getFriends().stream()
                .filter(f -> otherUser.getFriends().contains(f))
                .map(this::getUserBy)
                .collect(Collectors.toList());
    }
}