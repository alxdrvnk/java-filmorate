package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FriendListDb;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        getUserBy(user.getId());
        return storage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        friendListDb.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserBy(userId);
        getUserBy(friendId);
        friendListDb.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        getUserBy(userId);
        return friendListDb.getFriends(userId);
    }

    public void approveFriend(Long userId, Long friendID) {
        friendListDb.approveFriend(userId, friendID);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        getUserBy(userId);
        getUserBy(otherUserId);
        return friendListDb.getCommonFriends(userId, otherUserId);
    }

    public List<Film> getRecommendations(Long userId) {
        getUserBy(userId);
        Map<Long, List<Long>> likes = storage.getAllLikes();
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        for (Map.Entry<Long, List<Long>> pair : likes.entrySet()) {
            for (Long filmId : pair.getValue()) {
                if (!freq.containsKey(filmId)) {
                    freq.put(filmId, new HashMap<>());
                }


            }
        }
        return null;
    }
}