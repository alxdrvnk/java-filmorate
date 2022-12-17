package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage storage;

    public User create(User user) {
        return storage.create(user);
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User getUserBy(Long id) {
        return storage.get(id);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public User addFriend(Long userId, Long friendId) {
        return storage.addFriend(userId, friendId);
    }

    public User removeFriend(Long userId, Long friendId) {
        return storage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        return storage.getUserFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        return storage.getMutualFriends(userId, otherUserId);
    }
}
