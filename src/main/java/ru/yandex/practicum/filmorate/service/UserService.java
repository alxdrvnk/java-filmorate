package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.FilmorateAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao storage;

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
        User user = storage.getBy(userId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));
        User friend = storage.getBy(friendId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = storage.getBy(userId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));
        User friend = storage.getBy(friendId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getUserFriends(Long userId) {
        return null;
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        User user = storage.getBy(userId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));
        User otherUser = storage.getBy(otherUserId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));

        return user.getFriends().stream()
                .filter(f -> otherUser.getFriends().contains(f))
                .map(this::getUserBy)
                .collect(Collectors.toList());
    }

    public void addLikedFilm(Long userId, Long filmId) {
        User user = storage.getBy(userId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));

        if (user.getLikedFilms().stream()
                .anyMatch(filmId::equals)) {
            throw new FilmorateAlreadyExistException("Фильм уже добавлен в понравившиеся.");
        }

        user.getLikedFilms().add(filmId);
    }

    public void removeLikedFilm(Long userId, Long filmId) {
        User user = storage.getBy(userId).orElseThrow(() -> new FilmorateNotFoundException("Пользователь не найден."));

        if (user.getLikedFilms().stream()
                .noneMatch(filmId::equals)) {
            throw new FilmorateNotFoundException("Фильм не добавлен в понравившиеся.");
        }

        user.getLikedFilms().remove(filmId);
    }
}