package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage storage;

    public User create(User user) {
        validateUserBirthday(user);
        int id = getNextId();
        User newUser = user.withId(id);
        if (user.getName() == null) {
            newUser = newUser.withName(user.getLogin());
        }
        users.put(id, newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserBy(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с id: %d не найден.", id));
        }
    }

    public User update(User user) {
        validateUserBirthday(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new FilmorateNotFoundException(String.format("Пользователь с id: %d не найден.", user.getId()));
        }
    }

    private void validateUserBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new FilmorateValidationException("День рождения не может быть в будущем.");
        }
    }
}
