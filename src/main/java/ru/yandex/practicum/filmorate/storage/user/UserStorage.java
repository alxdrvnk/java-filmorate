package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User get(Long id);

    List<User> getAllUsers();

    List<User> getUserFriends(Long userId);
}
