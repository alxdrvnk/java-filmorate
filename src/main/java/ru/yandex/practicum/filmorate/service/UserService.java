package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

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
        User user = getUserBy(userId);
        User otherUser = getUserBy(otherUserId);

        return user.getFriends().stream()
                .filter(f -> otherUser.getFriends().contains(f))
                .map(this::getUserBy)
                .collect(Collectors.toList());
    }

    public void addLikedFilm(Long userId, Long filmId) {
        User user = getUserBy(userId);

        if (user.getLikedFilms().stream()
                .anyMatch(filmId::equals)) {
            throw new FilmorateAlreadyExistException("Фильм уже добавлен в понравившиеся");
        }
    }
}