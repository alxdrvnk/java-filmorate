package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
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

    // Нужно ли чтобы методы добавления/удаления друга возвращали юзера?
    // Если да, то тогда нужно возвращать копию изменного юзера?
    public void addFriend(Long userId, Long friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getUserFriends(Long userId) {
        return storage.getUserFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        User user = storage.get(userId);
        User otherUser = storage.get(otherUserId);

        return user.getFriends().stream()
                .filter(f -> otherUser.getFriends().contains(f))
                .map(this::getUserBy)
                .collect(Collectors.toList());
    }

    public void addLikedFilm(Long userId, Long filmId) {
        User user = storage.get(userId);

        if (user.getLikedFilms().stream()
                .anyMatch(filmId::equals)) {
            throw new FilmorateAlreadyExistException("Фильм уже добавлен в понравившиеся.");
        }

        user.getLikedFilms().add(filmId);
    }

    public void removeLikedFilm(Long userId, Long filmId) {
        User user = storage.get(userId);

        if (user.getLikedFilms().stream()
                .noneMatch(filmId::equals)) {
            throw new FilmorateNotFoundException("Фильм не добавлен в понравившиеся.");
        }

        user.getLikedFilms().remove(filmId);
    }
}